package main;


import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeoutException;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDBException;
import com.arangodb.ArangoDatabase;
import com.arangodb.util.MapBuilder;
import com.linkedin.replica.wall.config.Configuration;
import com.linkedin.replica.wall.config.DatabaseConnection;
import com.linkedin.replica.wall.handlers.impl.ArangoWallHandler;
import com.linkedin.replica.wall.models.Bookmark;
import com.linkedin.replica.wall.models.Comment;

import java.util.HashMap;
import java.util.List;

import com.linkedin.replica.wall.models.Comment;
import com.linkedin.replica.wall.models.Reply;
import com.linkedin.replica.wall.models.UserProfile;
import com.linkedin.replica.wall.services.WallService;
import databaseHandlers.DatabaseSeed;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class WallTest {
    private static WallService wallService;
    private static ArangoDatabase arangoDB;
    static Configuration config;
    private static DatabaseSeed dbSeed;
    private static String commentsCollection;


    @BeforeClass
    public static void setup() throws ClassNotFoundException, IOException, ParseException {
        String rootFolder = "src/main/resources/";
        Configuration.init(rootFolder + "app_config",
                rootFolder + "arango_config",
                rootFolder + "command_config");
        config = Configuration.getInstance();
        wallService = new WallService();
        arangoDB = DatabaseConnection.getInstance().getArangodb().db(
                Configuration.getInstance().getArangoConfig("db.name")
        );
        dbSeed = new DatabaseSeed();
        dbSeed.insertUsers();
        dbSeed.insertPosts();
        dbSeed.insertReplies();
        dbSeed.insertLikes();
        dbSeed.insertComments();
        commentsCollection = Configuration.getInstance().getArangoConfig("collections.comments.name");

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

    public Comment getComment(String commentId) {
        Comment comment = null;
        try {
            comment = arangoDB.collection(commentsCollection).getDocument(commentId,
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
            ArangoCursor<Comment> cursor = arangoDB.query(query, bindVars, null,
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
    public void testEditComments() throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
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
            LinkedHashMap<String, Object> response = (LinkedHashMap<String, Object>) wallService.serve("editComment", request);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<Comment> newComments = getComments("14");
        assertEquals("The comment should have a new Text", newComments.get(0).getText(),"Text");


    }

    @Test
    public void testDeleteComments() throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
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
            LinkedHashMap<String, Object> response = (LinkedHashMap<String, Object>) wallService.serve("deleteComment", request);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Comment newComment = getComment("1234");
        assertEquals("The comment should not exist", newComment,null);


    }

    @Test
    public void testGetComments() throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        HashMap<String,String> request = new HashMap<String,String>();
        request.put("parentPostId", "14");
        try {
            LinkedHashMap<String, Object> response = (LinkedHashMap<String, Object>) wallService.serve("getComments", request);
            List<Comment> newComments = (List<Comment>) response.get("response");
            assertEquals("The comment should not exist", newComments.size(),1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testAddBookmark() throws ClassNotFoundException, InstantiationException, ParseException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        HashMap<String, String> request = new HashMap<>();
        UserProfile user = dbSeed.getInsertedUsers().get(0);
        String userId = user.getUserId();
        String postId = "123";
        request.put("userId", userId);
        request.put("postId", postId);
        LinkedHashMap<String, Object> result = (LinkedHashMap<String, Object>) wallService.serve("addBookmark", request);
        String response = (String) result.get("response");
        assertEquals("response should be Success to add bookmark", response, "Success to add bookmark");
    }

    @Test
    public void testDeleteBookmark() throws ClassNotFoundException, InstantiationException, ParseException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        HashMap<String, String> request = new HashMap<>();
        UserProfile user = dbSeed.getInsertedUsers().get(0);
        String userId = user.getUserId();
        String postId = userId;
        request.put("userId", userId);
        request.put("postId", postId);
        LinkedHashMap<String, Object> result = (LinkedHashMap<String, Object>) wallService.serve("deleteBookmark", request);
        String response = (String) result.get("response");
        assertEquals("response should be Success to delete bookmark", response, "Success to delete bookmark");
    }

    @Test
    public void testGetBookmark() throws ClassNotFoundException, InstantiationException, ParseException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        HashMap<String, String> request = new HashMap<>();
        UserProfile user = dbSeed.getInsertedUsers().get(0);
        String userId = user.getUserId();
        String postId = userId;
        request.put("userId", userId);
        LinkedHashMap<String, Object> result = (LinkedHashMap<String, Object>) wallService.serve("deleteBookmark", request);
        int size = 1;
        List<Bookmark> response = (ArrayList<Bookmark>) result.get("response");
        assertEquals("response should be user's bookmark arraylist", response.size(), size);
    }

    @Test
    public void testAddLikeCommand() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ParseException{
        HashMap<String, String> request = new HashMap<>();
        request.put("likerId", "100");
        request.put("likedPostId", "99");
        request.put("likedCommentId", null);
        request.put("likedReplyId", null);
        request.put("userName", "Yara");
        request.put("headLine", "Yara and 5 others");
        request.put("imageUrl", "urlX");
        wallService.serve("addLike", request);
        }

    @AfterClass
    public static void tearDown() throws ArangoDBException, ClassNotFoundException, IOException {
        dbSeed.deleteAllUsers();
        dbSeed.deleteAllPosts();
        dbSeed.deleteAllReplies();
        dbSeed.deleteAllComments();
        dbSeed.deleteAllLikes();
        DatabaseConnection.getInstance().closeConnections();
          //  Main.shutdown();
    }

}
