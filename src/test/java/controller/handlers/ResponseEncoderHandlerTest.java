package controller.handlers;

import java.util.LinkedHashMap;

import com.linkedin.replica.wall.controller.handlers.ResponseEncoderHandler;
import org.junit.Test;

import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

import static org.junit.Assert.assertEquals;

public class ResponseEncoderHandlerTest {
    EmbeddedChannel channel;

    @Test
    public void testEncodingSuccessfulResponse(){
        // create successful response
        LinkedHashMap<String, Object> htblResponseBody = new LinkedHashMap<String, Object>();
        htblResponseBody.put("type", HttpResponseStatus.ACCEPTED);
        htblResponseBody.put("code", HttpResponseStatus.ACCEPTED.code());
        htblResponseBody.put("message", "Changes are applied successfully and configuration files are updated");

        channel = new EmbeddedChannel(new ResponseEncoderHandler());
        channel.write(htblResponseBody);

        FullHttpResponse httpResponse = channel.readOutbound();
        String responseBody = httpResponse.content().toString(CharsetUtil.UTF_8);
        assertEquals("Wrong returned response body.", "{\"code\":202,\"message\":\"Changes are applied successfully and configuration files are updated\"}", responseBody);
        assertEquals("Wrong returned response code.", HttpResponseStatus.ACCEPTED,httpResponse.status());
    }

    @Test
    public void testEncodingErrorResponse(){
        // create successful response
        LinkedHashMap<String, Object> htblResponseBody = new LinkedHashMap<String, Object>();
        htblResponseBody.put("type", HttpResponseStatus.BAD_REQUEST);
        htblResponseBody.put("code", HttpResponseStatus.BAD_REQUEST.code());
        htblResponseBody.put("errMessage", "Access Denied, forbidden request");

        channel = new EmbeddedChannel(new ResponseEncoderHandler());
        channel.write(htblResponseBody);

        FullHttpResponse httpResponse = channel.readOutbound();
        String responseBody = httpResponse.content().toString(CharsetUtil.UTF_8);

        assertEquals("Wrong returned response body.", "{\"code\":400,\"errMessage\":\"Access Denied, forbidden request\"}", responseBody);
        assertEquals("Wrong returned response code.", HttpResponseStatus.BAD_REQUEST,httpResponse.status());
    }
}
