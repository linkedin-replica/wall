package controller.handlers;

import static org.junit.Assert.*;

        import java.io.IOException;
        import java.lang.reflect.InvocationTargetException;
        import java.nio.file.Files;
        import java.nio.file.Path;
        import java.nio.file.Paths;
        import java.util.Base64;
        import java.util.LinkedHashMap;

import com.linkedin.replica.wall.config.Configuration;
import com.linkedin.replica.wall.controller.handlers.RequestProcessingHandler;
import org.junit.BeforeClass;
        import org.junit.Test;

        import com.google.gson.Gson;
        import com.google.gson.JsonObject;

        import io.netty.channel.embedded.EmbeddedChannel;
        import io.netty.handler.codec.http.HttpResponseStatus;

public class RequestProcessingHandlerTest {
    private EmbeddedChannel channel;

    @BeforeClass
    public static void setup() throws IOException{
        // initialize configuration
        String[] a = {"src/main/resources/app.config","src/main/resources/arango.test.config", "src/main/resources/commands.config", "src/main/resources/controller.config"};
        Configuration.init(a[0], a[1], a[2], a[3]);
    }

    @Test
    public void testProcessing(){
        // create JsonObject that is passed to RequestProcessingChannel
        LinkedHashMap<String, Object> htbl = new LinkedHashMap<String, Object>();
        htbl.put("setMaxThreadCount", 20);
        htbl.put("setMaxDBConnectionsCount", 10);
        Gson gson = new Gson();
        String json = gson.toJson(htbl);
        JsonObject jsonObj = gson.fromJson(json, JsonObject.class);

        // initialize embedded channel and write buf to it
        channel = new EmbeddedChannel(new RequestProcessingHandler());
        channel.writeInbound(jsonObj);

        // get response created after handling requests
        LinkedHashMap<String, Object> response = channel.readOutbound();
        assertEquals("Wrong type", HttpResponseStatus.ACCEPTED, (HttpResponseStatus)response.get("type"));
        assertEquals("Wrong code", 202, response.get("code"));
        assertEquals("Wrong message", "Changes are applied successfully and configuration files are updated", response.get("message"));
    }

    @Test
    public void testAddingCommand() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException{
        // add a new command file by sending file to RequestProcessingChannel
        addCommandFile();

        // get response created after handling requests
        LinkedHashMap<String, Object> response = channel.readOutbound();
        assertEquals("Wrong type", HttpResponseStatus.ACCEPTED, (HttpResponseStatus)response.get("type"));
        assertEquals("Wrong code", 202, response.get("code"));
        assertEquals("Wrong message", "Changes are applied successfully and configuration files are updated", response.get("message"));

        // get .class folder path from configuration
        String folderPath = Configuration.getInstance().getAppConfigProp("app.classes.path");
        // get full path of new file
        Path path = Paths.get(System.getProperty("user.dir")+ "/" +folderPath +"/commands/impl/testCommand.class");
        // check that the file was added
        assertTrue(Files.exists(path));
        // check that app.config file was updated
        assertEquals(Configuration.getInstance().getCommandConfigProp("search.test"), "testCommand");
        assertEquals(Configuration.getInstance().getCommandConfigProp("search.test.handler"), "ArangoSearchHandler");
        // delete added file
        Files.deleteIfExists(path);
    }

    @Test
    public void testDeletingCommand() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException{
        // add a new command file by sending file to RequestProcessingChannel
        addCommandFile();

        // get path of testCommand class
        Path path = Paths.get("src/test/resources/testCommand.class");
        // create JsonObject that is passed to RequestProcessingChannel
        LinkedHashMap<String, Object> htbl = new LinkedHashMap<String, Object>();
        htbl.put("fileName", "testCommand");

        // add property key that will be added to command configuration file
        htbl.put("configPropKey", "search.test");


        // construct json
        Gson gson = new Gson();
        String json = gson.toJson(htbl);
        JsonObject jsonObj = new JsonObject();
        jsonObj.add("deleteCommand", gson.fromJson(json, JsonObject.class));

        // initialize embedded channel and write buf to it
        channel = new EmbeddedChannel(new RequestProcessingHandler());
        channel.writeInbound(jsonObj);

        // get response created after handling requests
        LinkedHashMap<String, Object> response = channel.readOutbound();
        assertEquals("Wrong type", HttpResponseStatus.ACCEPTED, (HttpResponseStatus)response.get("type"));
        assertEquals("Wrong code", 202, response.get("code"));
        assertEquals("Wrong message", "Changes are applied successfully and configuration files are updated", response.get("message"));

        // get .class folder path from configuration
        String folderPath = Configuration.getInstance().getAppConfigProp("app.classes.path");
        // get full path of new file
        path = Paths.get(System.getProperty("user.dir")+ "/" +folderPath +"/commands/impl/testCommand.class");
        // check that the file was deleted
        assertFalse(Files.exists(path));
        // check that app.config file was updated
        assertNotEquals(Configuration.getInstance().getCommandConfigProp("search.test"), "testCommand");
        assertNotEquals(Configuration.getInstance().getCommandConfigProp("search.test.handler"), "ArangoSearchHandler");
    }

    /**
     * testing sending request without configPropKey parameter
     * @throws IOException
     */
    @Test
    public void testMissingParameter() throws IOException{
        // get path of testCommand class
        Path path = Paths.get("src/test/resources/testCommand.class");
        // create JsonObject that is passed to RequestProcessingChannel
        LinkedHashMap<String, Object> htbl = new LinkedHashMap<String, Object>();
        htbl.put("fileName", "testCommand");

        // convert file to bytes and encode it to string
        byte[] bytes = Files.readAllBytes(path);
        String s = Base64.getEncoder().encodeToString(bytes);
        htbl.put("bytes", s);

        // add handler class name that will be added to command configuration file
        htbl.put("handler", "ArangoSearchHandler");

        // construct json
        Gson gson = new Gson();
        String json = gson.toJson(htbl);
        JsonObject jsonObj = new JsonObject();
        jsonObj.add("addCommand", gson.fromJson(json, JsonObject.class));

        // initialize embedded channel and write buf to it
        channel = new EmbeddedChannel(new RequestProcessingHandler());
        channel.writeInbound(jsonObj);

        /*
         *  read result from channel which is null because an exception is thrown due to empty body
         *  and the exception will be caught and an error response is created and flushed to responseDecoderHandler
         *  so no inbound message to be read
         */
        Object out = channel.readInbound();
        assertEquals("Invalid message read from RequestDecoderHandler channel.",null, out);

        // get error response create after catching exception
        LinkedHashMap<String, Object> err = channel.readOutbound();
        System.out.println(err);
        assertEquals("Wrong type", HttpResponseStatus.BAD_REQUEST, (HttpResponseStatus)err.get("type"));
        assertEquals("Wrong code", 400, err.get("code"));
        assertEquals("Wrong errMessage", "Invalid parameters : [fileName, bytes, handler]. expected : [fileName, configPropKey, handler, bytes]", err.get("errMessage"));
    }

    private void addCommandFile() throws IOException{
        // get path of testCommand class
        Path path = Paths.get("src/test/resources/testCommand.class");
        // create JsonObject that is passed to RequestProcessingChannel
        LinkedHashMap<String, Object> htbl = new LinkedHashMap<String, Object>();
        htbl.put("fileName", "testCommand");

        // convert file to bytes and encode it to string
        byte[] bytes = Files.readAllBytes(path);
        String s = Base64.getEncoder().encodeToString(bytes);
        htbl.put("bytes", s);

        // add property key that will be added to command configuration file
        htbl.put("configPropKey", "search.test");
        // add handler class name that will be added to command configuration file
        htbl.put("handler", "ArangoWallHandler");

        // construct json
        Gson gson = new Gson();
        String json = gson.toJson(htbl);
        JsonObject jsonObj = new JsonObject();
        jsonObj.add("addCommand", gson.fromJson(json, JsonObject.class));

        // initialize embedded channel and write buf to it
        channel = new EmbeddedChannel(new RequestProcessingHandler());
        channel.writeInbound(jsonObj);
    }
}
