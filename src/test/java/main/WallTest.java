package main;


import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeoutException;

import com.arangodb.ArangoDBException;
import com.arangodb.ArangoDatabase;
import com.linkedin.replica.wall.config.Configuration;
import com.linkedin.replica.wall.config.DatabaseConnection;

import java.util.HashMap;
import java.util.List;

import com.linkedin.replica.wall.models.Reply;
import com.linkedin.replica.wall.services.WallService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class WallTest {
    private static WallService wallService;
    private static ArangoDatabase arangoDB;
    static Configuration config;

    @BeforeClass
    public static void setup() throws ClassNotFoundException, IOException {
        String rootFolder = "src/main/resources/";
        Configuration.init(rootFolder + "app_config",
                rootFolder + "arango_config",
                rootFolder + "command_config");
        config = Configuration.getInstance();
        wallService = new WallService();
        arangoDB = DatabaseConnection.getInstance().getArangodb().db(
                Configuration.getInstance().getArangoConfig("db.name")
        );
    }

    @Test
    public void testAddReplyService() throws ClassNotFoundException, InstantiationException, ParseException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        HashMap<String,String> request = new HashMap<String, String>();
        request.put("authorId","3");
        request.put("parentPostId","1");
        request.put("parentCommentId","45");
        request.put("mentions","y");
        request.put("likesCount","45");
        request.put("text","TestTestTest");
        request.put("timestamp","Thu Jan 19 2012 01:00 PM");
        request.put("images","y");
        request.put("urls","y");
        wallService.serve("addReply",request);

        LinkedHashMap<String, Object> result = (LinkedHashMap<String, Object>) wallService.serve("getReplies", request);
        List<Reply> replies = (List<Reply>) result.get("response");
        Boolean found = false;
        for(int i =0;i<replies.size();i++){
            if(replies.get(i).getText().equals("TestTestTest")){
                found = true;
                break;
            }
        }
        assertEquals("Texts should be the same",found,true);

    }

    @Test
    public void testEditReply() throws ClassNotFoundException, InstantiationException, ParseException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        HashMap<String,String> request = new HashMap<String, String>();
        request.put("replyId","1");
        request.put("authorId","3");
        request.put("parentPostId","1");
        request.put("parentCommentId","45");
        request.put("mentions","y");
        request.put("likesCount","45");
        request.put("text","Testing service edit");
        request.put("timestamp","Thu Jan 19 2012 01:00 PM");
        request.put("images","y");
        request.put("urls","y");
        wallService.serve("editReply",request);
        LinkedHashMap<String, Object> result = (LinkedHashMap<String, Object>) wallService.serve("getReplies", request);
        List<Reply> replies = (List<Reply>) result.get("response");
        Boolean found = false;
        for(int i =0;i<replies.size();i++){
            if(replies.get(i).getText().equals("Testing service edit") && replies.get(i).getReplyId().equals("1")){
                found = true;
                break;
            }
        }
        assertEquals("Texts should be the same",found,true);
    }

    @Test
    public void testDeleteReply() throws ClassNotFoundException, InstantiationException, ParseException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        HashMap<String,String> request = new HashMap<String, String>();
        request.put("replyId","1");
        request.put("authorId","3");
        request.put("parentPostId","1");
        request.put("parentCommentId","45");
        request.put("mentions","y");
        request.put("likesCount","45");
        request.put("text","Testing");
        request.put("timestamp","Thu Jan 19 2012 01:00 PM");
        request.put("images","y");
        request.put("urls","y");

        LinkedHashMap<String, Object> result = (LinkedHashMap<String, Object>) wallService.serve("getReplies", request);
        List<Reply> replies = (List<Reply>) result.get("response");

        wallService.serve("deleteReply",request);

        LinkedHashMap<String, Object> testResult = (LinkedHashMap<String, Object>) wallService.serve("getReplies", request);
        List<Reply> testReplies = (List<Reply>) testResult.get("response");

        assertEquals("Size should decrement by one",replies.size()-1,testReplies.size());
    }




//    @BeforeClass
//    public static void setup() throws ClassNotFoundException, IOException, SQLException, TimeoutException {
//        // startup SearchEngine
//        String[] args = {"arango_config", "src/main/resources/command_config"};
//        //Main.start(args);
//        service = new WallService();
//        arangoDB = DatabaseConnection.getInstance().getArangodb();
//        arangoWallHandler = new ArangoWallHandler();
//        properties = new Properties();
//        properties.load(new FileInputStream("arango_config"));
//        dbName = properties.getProperty("arangodb.name");
//        likesCollection = properties.getProperty("collections.likes.name");
//        commentsCollection = properties.getProperty("collections.comments.name");
//        dbSeed = new DatabaseSeed();
////        dbSeed.insertUsers();
////        dbSeed.insertPosts();
//
//
//    }

//    public Comment getComment(String commentId) {
//        Comment comment = null;
//        try {
//            comment = arangoDB.db(dbName).collection(commentsCollection).getDocument(commentId,
//                    Comment.class);
//        } catch (ArangoDBException e) {
//            System.err.println("Failed to get comment: commentId; " + e.getMessage());
//        }
//        return comment;
//    }
//
//    public List<Comment> getComments(String postId) {
//        ArrayList<Comment> comments = new ArrayList<Comment>();
//        try {
//            String query = "FOR l IN " + commentsCollection + " FILTER l.parentPostId == @parentPostId RETURN l";
//            Map<String, Object> bindVars = new MapBuilder().put("parentPostId", postId).get();
//            ArangoCursor<Comment> cursor = arangoDB.db(dbName).query(query, bindVars, null,
//                    Comment.class);
//            cursor.forEachRemaining(commentDocument -> {
//                comments.add(commentDocument);
//                System.out.println("Key: " + commentDocument.getCommentId());
//            });
//        } catch (ArangoDBException e) {
//            System.err.println("Failed to get posts' comments." + e.getMessage());
//        }
//        return comments;
//    }
//


//
//    @Test
//    public void testEditComments() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
//        HashMap<String,String> request = new HashMap<String,String>();
//        request.put("commentId", "1234");
//        request.put("authorId", "12");
//        request.put("parentPostId", "14");
//        request.put("likesCount", 20+"");
//        request.put("repliesCount", 2+"");
//        request.put("images", "sdgg");
//        request.put("urls", "fhdfhj");
//        request.put("mentions", "sdgfh");
//        request.put("text", "Text");
//        request.put("timeStamp", "Time Stamp");
//        try {
//            LinkedHashMap<String, Object> response = service.serve("edit.comment", request);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        List<Comment> newComments = getComments("14");
//        assertEquals("The comment should have a new Text", newComments.get(0).getText(),"Text");
//
//
//    }
//
//    @Test
//    public void testDeleteComments() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
//        HashMap<String,String> request = new HashMap<String,String>();
//        request.put("commentId", "1234");
//        request.put("authorId", "12");
//        request.put("parentPostId", "14");
//        request.put("likesCount", 20+"");
//        request.put("repliesCount", 2+"");
//        request.put("images", "sdgg");
//        request.put("urls", "fhdfhj");
//        request.put("mentions", "sdgfh");
//        request.put("text", "Text");
//        request.put("timeStamp", "Time Stamp");
//        try {
//            LinkedHashMap<String, Object> response = service.serve("delete.comment", request);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        Comment newComment = getComment("1234");
//        assertEquals("The comment should not exist", newComment,null);
//
//
//    }
//
//    @Test
//    public void testGetComments() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
//        HashMap<String,String> request = new HashMap<String,String>();
//        request.put("parentPostId", "14");
//        try {
//            LinkedHashMap<String, Object> response = service.serve("get.comments", request);
//            List<Comment> newComments = (List<Comment>) response.get("response");
//            assertEquals("The comment should not exist", newComments.size(),1);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//    }
//

//






//        @Test
//        public void testAddLikeCommand() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ParseException{
//            HashMap<String, String> request = new HashMap<>();
//            request.put("likerId", "100");
//            request.put("likedPostId", "99");
//            request.put("likedCommentId", null);
//            request.put("likedReplyId", null);
//            request.put("userName", "Yara");
//            request.put("headLine", "Yara and 5 others");
//            request.put("imageUrl", "urlX");
//
//            wallService.serve("addLike", request);
//        }

    @AfterClass
    public static void tearDown() throws ArangoDBException, ClassNotFoundException, IOException, SQLException {
            DatabaseConnection.getInstance().closeConnections();
          //  Main.shutdown();
    }

}
