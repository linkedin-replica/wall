package main;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeoutException;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.util.MapBuilder;
import com.linkedin.replica.wall.config.DatabaseConnection;
import com.linkedin.replica.wall.handlers.impl.ArangoWallHandler;
import com.linkedin.replica.wall.models.Comment;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.linkedin.replica.wall.models.Reply;
import com.linkedin.replica.wall.models.UserProfile;
import com.linkedin.replica.wall.services.WallService;
import databaseHandlers.DatabaseSeed;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.arangodb.ArangoDBException;
import org.junit.Test;


public class WallTest {
    private static DatabaseSeed dbSeed;
    private static WallService service;
    private static Properties properties;
    private static ArangoDB arangoDB;
    private static ArangoWallHandler arangoWallHandler;
    private static String dbName;
    private static String  likesCollection;
    private static String  commentsCollection;

    @BeforeClass
    public static void setup() throws ClassNotFoundException, IOException, SQLException, TimeoutException {
        // startup SearchEngine
        String[] args = {"db_config", "src/main/resources/command_config"};
        //Main.start(args);
        service = new WallService();
        arangoDB = DatabaseConnection.getInstance().getArangodb();
        arangoWallHandler = new ArangoWallHandler();
        properties = new Properties();
        properties.load(new FileInputStream("db_config"));
        dbName = properties.getProperty("arangodb.name");
        likesCollection = properties.getProperty("collections.likes.name");
        commentsCollection = properties.getProperty("collections.comments.name");
        dbSeed = new DatabaseSeed();
        dbSeed.deleteAllUsers();;
        dbSeed.deleteAllPosts();
        dbSeed.insertUsers();
        dbSeed.insertPosts();


    }

    public Comment getComment(String commentId) {
        Comment comment = null;
        try {
            comment = arangoDB.db(dbName).collection(commentsCollection).getDocument(commentId,
                    Comment.class);
        } catch (ArangoDBException e) {
            System.err.println("Failed to get comment: commentId; " + e.getMessage());
        }
        return comment;
    }

    public List<Comment> getComments(String postId) {
        ArrayList<Comment> comments = new ArrayList<Comment>();
        try {
            String query = "FOR l IN " + commentsCollection + " FILTER l.parentPostId == @parentPostId RETURN l";
            Map<String, Object> bindVars = new MapBuilder().put("parentPostId", postId).get();
            ArangoCursor<Comment> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    Comment.class);
            cursor.forEachRemaining(commentDocument -> {
                comments.add(commentDocument);
                System.out.println("Key: " + commentDocument.getCommentId());
            });
        } catch (ArangoDBException e) {
            System.err.println("Failed to get posts' comments." + e.getMessage());
        }
        return comments;
    }

    @Test
    public void testAddComments() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        HashMap<String, String> request = new HashMap<String, String>();
        request.put("commentId", "1234");
        request.put("authorId", "12");
        request.put("parentPostId", "14");
        request.put("likesCount", 20 + "");
        request.put("repliesCount", 2 + "");
        request.put("images", "sdgg");
        request.put("urls", "fhdfhj");
        request.put("mentions", "sdgfh");
        request.put("text", "Comment Text");
        request.put("timeStamp", "Time Stamp");
        try {
            LinkedHashMap<String, Object> response = service.serve("add.comment", request);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<Comment> newComments = getComments("14");
        assertEquals("Only one comment should have the parentPostId", newComments.size(), 1);
    }

    @Test
    public void testAddReplyService() throws ClassNotFoundException, InstantiationException, ParseException, IllegalAccessException {
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
        service.serve("addReply",request);

        LinkedHashMap<String, Object> result = service.serve("getReplies", request);
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
    public void testEditComments() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        HashMap<String,String> request = new HashMap<String,String>();
        request.put("commentId", "1234");
        request.put("authorId", "12");
        request.put("parentPostId", "14");
        request.put("likesCount", 20+"");
        request.put("repliesCount", 2+"");
        request.put("images", "sdgg");
        request.put("urls", "fhdfhj");
        request.put("mentions", "sdgfh");
        request.put("text", "Text");
        request.put("timeStamp", "Time Stamp");
        try {
            LinkedHashMap<String, Object> response = service.serve("edit.comment", request);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<Comment> newComments = getComments("14");
        assertEquals("The comment should have a new Text", newComments.get(0).getText(),"Text");


    }

    @Test
    public void testDeleteComments() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        HashMap<String,String> request = new HashMap<String,String>();
        request.put("commentId", "1234");
        request.put("authorId", "12");
        request.put("parentPostId", "14");
        request.put("likesCount", 20+"");
        request.put("repliesCount", 2+"");
        request.put("images", "sdgg");
        request.put("urls", "fhdfhj");
        request.put("mentions", "sdgfh");
        request.put("text", "Text");
        request.put("timeStamp", "Time Stamp");
        try {
            LinkedHashMap<String, Object> response = service.serve("delete.comment", request);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Comment newComment = getComment("1234");
        assertEquals("The comment should not exist", newComment,null);


    }

    @Test
    public void testGetComments() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        HashMap<String,String> request = new HashMap<String,String>();
        request.put("parentPostId", "14");
        try {
            LinkedHashMap<String, Object> response = service.serve("get.comments", request);
            List<Comment> newComments = (List<Comment>) response.get("response");
            assertEquals("The comment should not exist", newComments.size(),1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testEditReply() throws ClassNotFoundException, InstantiationException, ParseException, IllegalAccessException {
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
        service.serve("editReply",request);
        LinkedHashMap<String, Object> result = service.serve("getReplies", request);
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
    public void testDeleteReply() throws ClassNotFoundException, InstantiationException, ParseException, IllegalAccessException {

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

        LinkedHashMap<String, Object> result = service.serve("getReplies", request);
        List<Reply> replies = (List<Reply>) result.get("response");

        service.serve("deleteReply",request);

        LinkedHashMap<String, Object> testResult = service.serve("getReplies", request);
        List<Reply> testReplies = (List<Reply>) testResult.get("response");

        assertEquals("Size should decrement by one",replies.size()-1,testReplies.size());
    }

    @Test
    public void testAddBookmark() throws ClassNotFoundException, InstantiationException, ParseException, IllegalAccessException {
        HashMap<String, String> request = new HashMap<>();
        UserProfile user = dbSeed.getInsertedUsers().get(0);
        String userId = user.getUserId();
        String postId = "123";
        request.put("userId", userId);
        request.put("postId", postId);
        LinkedHashMap<String, Object> result = service.serve("addBookmark", request);
        String response = (String) result.get("response");
        assertEquals("response should be Success to add bookmark", response, "Success to add bookmark");
    }

    
    @AfterClass
    public static void tearDown() throws ArangoDBException, ClassNotFoundException, IOException, SQLException {
//        dbSeed.deleteAllUsers();
//        dbSeed.deleteAllPosts();
//        Main.shutdown();
    }

}
