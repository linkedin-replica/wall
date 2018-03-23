package com.linkedin.replica.wall.commands.impl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.config.Configuration;
import com.linkedin.replica.wall.exceptions.WallException;
import com.linkedin.replica.wall.services.Workers;


public class ControllerCommand extends Command {
    private static Configuration config = Configuration.getInstance();

    public ControllerCommand(HashMap<String, Object> args) {
        super(args);
    }

    @Override
    public Object execute() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException {
        String methodName = args.get("methodName").toString();
        Object val = args.get("param");

        // get method
        Method method = ControllerCommand.class.getMethod(methodName, Object.class);
        // invoke method, null because it is a static method
        try{
            method.invoke(null, val);
        }catch(InvocationTargetException ex){
            throw new WallException(ex.getTargetException().getMessage());
        }
        return null;
    }

    /**
     * set the maxium number of threads that could be created by the thread pool library
     * @param val
     */
    public static void setMaxThreadCount(Object val){
        if(val == null || ! ((JsonPrimitive) val).isNumber())
            throw new WallException(String.format("Invalid parameters : %s. expected an integer representing maximum number of threads", val ));

        int maxThreadCount = ((JsonPrimitive) val).getAsInt();
        config.setAppControllerProp("app.max_thread_count", maxThreadCount+"");

        // set number of threads of workers' pool
        Workers.getInstance().setNumThreads(maxThreadCount);
    }

    /**
     * set the maximum number of database connections that could be created to connect to the database
     * @param val
     */
    public static void setMaxDBConnectionsCount(Object val){
        if(val == null || !((JsonPrimitive) val).isNumber())
            throw new WallException(String.format("Invalid parameters : %s. expected an integer representing maximum number of DB connections", val ));

        int maxDBConnectionCount = ((JsonPrimitive) val).getAsInt();
        config.setAppControllerProp("app.max_db_connections_count", maxDBConnectionCount +"");
        //TODO set number of db connections
    }

    /**
     * Add a new command class
     * @param val
     * @throws IOException
     */
    public static void addCommand(Object val) throws IOException{
        // convert value to JSONObject
        JsonObject obj = convert(val);

        // validate embedded JSON object obj to check that all obligatory key value pairs are available
        validate(obj, new String[]{"fileName", "configPropKey","handler", "bytes"});

        // remove double quotations of string value in JSON object
        String s = obj.get("bytes").toString().replaceAll("\"", "");
        String commandConfigPropKey = obj.get("configPropKey").toString().replaceAll("\"", "");
        String fileName = obj.get("fileName").toString().replaceAll("\"", "");
        String handlerClassName = obj.get("handler").toString().replaceAll("\"", "");

        // decode to bytes to write file
        byte[] bytes = Base64.getDecoder().decode(s);
        // write file to .class folder in commands/impl package
        writeFile(fileName, "commands/impl",  bytes);

        // update command config file
        config.setCommandsConfigProp(commandConfigPropKey, fileName);
        config.setCommandsConfigProp(commandConfigPropKey+".handler", handlerClassName);
    }

    /**
     * Delete an existing command class
     * @param val
     * @throws IOException
     */
    public static void deleteCommand(Object val) throws IOException{
        // convert value to JSONObject
        JsonObject obj = convert(val);

        // validate embedded JSON object obj to check that all obligatory key value pairs are available
        validate(obj, new String[]{"fileName", "configPropKey"});

        // remove double quotations of string value in JSON object
        String commandConfigPropKey = obj.get("configPropKey").toString().replaceAll("\"", "");
        String fileName = obj.get("fileName").toString().replaceAll("\"", "");

        // delete command if exits
        deleteFile(fileName, "commands/impl");

        // update command configuration file
        config.setCommandsConfigProp(commandConfigPropKey, null);
        config.setCommandsConfigProp(commandConfigPropKey+".handler", null);
    }

    /**
     * Update an existing command class
     * @param val
     * @throws IOException
     */
    public static void updateCommand(Object val) throws IOException{
        // addCommand will delete file and write the updated one coming in request body
        addCommand(val);
    }

    /**
     * update an existing class
     * @param val
     * @throws IOException
     */
    public static void updateClass(Object val) throws IOException{
        // convert value to JSONObject
        JsonObject obj = convert(val);

        // validate embedded JSON object obj to check that all obligatory key value pairs are available
        validate(obj, new String[]{"packageName","fileName", "configPropKey", "bytes"});

        // remove double quotations of string value in JSON object
        String s = obj.get("bytes").toString().replaceAll("\"", "");
        String packageName = obj.get("packageName").toString().replaceAll("\"", "");
        String fileName = obj.get("fileName").toString().replaceAll("\"", "");

        // decode to bytes to write file
        byte[] bytes = Base64.getDecoder().decode(s);

        // write file to .class folder in commands/impl package
        writeFile(fileName, packageName,  bytes);

        // update configuration file
        // TODO
    }

    /**
     * freeze the independent app  i.e. stop accepting new requests and release all resources from pools
     * @param val
     */
    public static void freeze(Object val){
        // TODO
    }

    /**
     * continue accepting new requests and ask pools to acquire resources again
     * @param val
     */
    public static void resume(Object val){
        // TODO
    }

    /**
     * set the type of errors that must be logged
     * @param val
     */
    public static void setErrorReportingLevel(Object val){
        if(val == null || ! ((JsonPrimitive) val).isNumber())
            throw new WallException(String.format("Invalid parameters : %s. expected an integer representing logging level.", val ));

        int loggingLevel = ((JsonPrimitive) val).getAsInt();
        config.setAppControllerProp("app.error_reporting_level", loggingLevel+"");
        // TODO
    }

    /**
     * convert an embedded JSON object to JSONObject
     * @param val
     * @return
     */
    private static JsonObject convert(Object val){
        Gson gson = new Gson();
        return gson.fromJson(val.toString(), JsonObject.class);
    }

    private static void validate(JsonObject obj, String... args){
        for(String arg : args){
            if(obj.get(arg) == null )
                throw new WallException(String.format("Invalid parameters : %s. expected : %s", obj.keySet().toString(), Arrays.toString(args)));
        }
    }

    private static void writeFile(String fileName, String packageName, byte[] bytes) throws IOException{
        // get .class folder path from configuration
        String folderPath = config.getAppConfigProp("app.classes.path");
        // get full path of new file
        Path path = Paths.get(folderPath + "/" + packageName + "/" + fileName+".class");
        // delete file if exist to avoid throwing FileAlreadyExistsException
        Files.deleteIfExists(path);
        // write file
        Files.write(path, bytes, new OpenOption[]{StandardOpenOption.CREATE_NEW});
    }

    private static void deleteFile(String fileName, String packageName) throws IOException{
        // get .class folder path from configuration
        String folderPath = config.getAppConfigProp("app.classes.path");
        // get full path of new file
        Path path = Paths.get(folderPath + "/" + packageName + "/" + fileName+".class");
        // delete file if exist to avoid throwing FileAlreadyExistsException
        Files.deleteIfExists(path);
    }
}

