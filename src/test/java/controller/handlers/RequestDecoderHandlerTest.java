package controller.handlers;

import java.util.LinkedHashMap;

import com.linkedin.replica.wall.controller.handlers.RequestDecoderHandler;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class RequestDecoderHandlerTest {
    // recieve request
    private EmbeddedChannel channel;
    private static final String controllerURI = "/api/controller";

    @Test
    public void testDecoding(){
        // create request body using LinkedHashMap
        LinkedHashMap<String, Object> htbl = new LinkedHashMap<String, Object>();
        htbl.put("setMaxThreadCount", 20);
        htbl.put("setMaxDBConnectionsCount", 10);

        // convert to json string
        Gson gson = new Gson();
        String json = gson.toJson(htbl);

        // convert to bytes to be send into channel and wrap bytesArr into netty's ByteBuf
        byte[] bytes = json.getBytes();
        ByteBuf buf = Unpooled.wrappedBuffer(bytes);

        // construct FullHttpRequest
        FullHttpRequest fullHttpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, controllerURI, buf);

        // initialize embedded channel
        channel = new EmbeddedChannel(new RequestDecoderHandler());

        // write fullHttpRequest to channel
        assertTrue(channel.writeInbound(fullHttpRequest));
        assertTrue(channel.finish());

        // read result from channel which is the decoded JSON object
        JsonObject jsonObj = (JsonObject) channel.readInbound();
        assertEquals("Wrong returned value for key: setMaxThreadCount",20, jsonObj.get("setMaxThreadCount").getAsInt());
        assertEquals("Wrong returned value for key: setMaxDBConnectionsCount",10, jsonObj.get("setMaxDBConnectionsCount").getAsInt());
    }

    @Test
    public void testEmptyRequestBody(){
        // create an empty request body using LinkedHashMap
        LinkedHashMap<String, Object> htbl = new LinkedHashMap<String, Object>();

        // convert to json string
        Gson gson = new Gson();
        String json = gson.toJson(htbl);

        // convert to bytes to be send into channel and wrap bytesArr into netty's ByteBuf
        byte[] bytes = json.getBytes();
        ByteBuf buf = Unpooled.wrappedBuffer(bytes);

        // construct FullHttpRequest
        FullHttpRequest fullHttpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, controllerURI, buf);

        // initialize embedded channel
        channel = new EmbeddedChannel(new RequestDecoderHandler());

        // write fullHttpRequest to channel
        channel.writeInbound(fullHttpRequest);
        channel.finish();

        /*
         *  read result from channel which is null because an exception is thrown due to empty body
         *  and the exception will be caught and an error response is created and flushed to responseDecoderHandler
         *  so no inbound message to be read
         */
        Object out = channel.readInbound();
        assertEquals("Invalid message read from RequestDecoderHandler channel.",null, out);

        // get error response create after catching exception
        LinkedHashMap<String, Object> err = channel.readOutbound();
        assertEquals("Wrong type",HttpResponseStatus.BAD_REQUEST, (HttpResponseStatus)err.get("type"));
        assertEquals("Wrong error code", 400, err.get("code"));
        assertEquals("Wrong error errMessage", "Request Body must not be empty.", err.get("errMessage"));
    }

    @Test
    public void testWrongRequestURL(){

        ByteBuf buf = Unpooled.wrappedBuffer(new byte[0]);

        // construct FullHttpRequest with wrong request url eg. xyz
        FullHttpRequest fullHttpRequest =
                new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "xyz", buf);

        // initialize embedded channel
        channel = new EmbeddedChannel(new RequestDecoderHandler());

        // write fullHttpRequest to channel
        channel.writeInbound(fullHttpRequest);
        channel.finish();

        /*
         *  read result from channel which is null because an exception is thrown due to wrong request URL
         *  and the exception will be caught and an error response is created and flushed to responseDecoderHandler
         *  so no inbound message to be read
         */
        Object out = channel.readInbound();
        assertEquals("Invalid message read from RequestDecoderHandler channel.",null, out);

        // get error response create after catching exception
        LinkedHashMap<String, Object> err = channel.readOutbound();
        assertEquals("Wrong type", HttpResponseStatus.NOT_FOUND, (HttpResponseStatus)err.get("type"));
        assertEquals("Wrong error code", 404, err.get("code"));
        assertEquals("Wrong error errMessage", "Access Denied, forbidden request: xyz", err.get("errMessage"));

    }
}
