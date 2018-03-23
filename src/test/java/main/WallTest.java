package main;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDBException;
import com.arangodb.ArangoDatabase;
import com.arangodb.util.MapBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.linkedin.replica.wall.config.Configuration;
import com.linkedin.replica.wall.models.*;
import com.linkedin.replica.wall.database.DatabaseConnection;

import java.util.HashMap;
import java.util.List;

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
    private static Post insertedPost;
    private static Comment insertedComment;
    private static Reply insertedReply;
    private static UserProfile insertedUser;
    private static Like insertedLike;
    private static   JsonArray mentions;
    private  static JsonArray urls;
   private static JsonArray images;
    @BeforeClass
    public static void setup() throws ClassNotFoundException, IOException, ParseException {
        String rootFolder = "src/main/resources/";
        Configuration.init(rootFolder + "app.config",
                rootFolder + "arango.test.config",
                rootFolder + "commands.config", rootFolder + "controller.config");
        config = Configuration.getInstance();
        wallService = new WallService();
        DatabaseConnection.init();
        arangoDB = DatabaseConnection.getInstance().getArangodb().db(
                Configuration.getInstance().getArangoConfig("db.name")
        );
        dbSeed = new DatabaseSeed();
        dbSeed.insertPosts();
        dbSeed.insertComments();
        dbSeed.insertReplies();
        dbSeed.insertLikes();
        dbSeed.insertUsers();

        commentsCollection = Configuration.getInstance().getArangoConfig("collections.comments.name");

        insertedComment = dbSeed.getInsertedComments().get(0);
        insertedLike = dbSeed.getInsertedLikes().get(0);
        insertedPost = dbSeed.getInsertedPosts().get(0);
        insertedReply = dbSeed.getInsertedReplies().get(0);
        insertedUser = dbSeed.getInsertedUsers().get(0);
        mentions = new JsonArray();
        urls = new JsonArray();
        images = new JsonArray();
        mentions.add("yara");
        images.add("bla bla ");
        urls.add("hania");

    }

    @Test
    public void testAddReplyService() throws Exception {

        HashMap<String, Object> request = new HashMap<String, Object>();
        request.put("authorId","1");
        request.put("parentPostId", insertedPost.getPostId());
        request.put("parentCommentId",insertedComment.getCommentId());
        request.put("mentions",mentions);
        request.put("likesCount",45);
        request.put("text","TestTestTest");
        request.put("timestamp","Thu Jan 19 2012 01:00 PM");
        request.put("images",images);
        request.put("urls",urls);
        int beforeReplyComment = insertedComment.getRepliesCount() + 1;
        int beforeReplyPost = insertedPost.getCommentsCount() + 1;
        String response = (String)wallService.serve("addReply",request);
        int afterReplyPost = insertedPost.getCommentsCount();
        int afterReplyComment = insertedComment.getRepliesCount();
        List<Reply> replies = (List<Reply>)  wallService.serve("getReplies", request);
        Boolean found = false;
        for(int i = 0;i < replies.size(); i++){
                if(replies.get(i).getText().equals("TestTestTest")){
                    found = true;
                    break;
                }
        }
        assertEquals("added reply correctly", found, true);
        assertEquals("response should be equal Reply created",response,"Reply created");


    }

    @Test
    public void testEditReply() throws Exception {
        HashMap<String, Object> request = new HashMap<String, Object>();
        request.put("replyId", insertedReply.getReplyId());
        request.put("authorId","1");
        request.put("parentPostId",insertedPost.getPostId());
        request.put("parentCommentId",insertedComment.getCommentId());
        request.put("mentions", mentions);
        request.put("likesCount",45);
        request.put("text","Testing service edit");
        request.put("timestamp","Thu Jan 19 2012 01:00 PM");
        request.put("images", images);
        request.put("urls", urls);
        String response = (String) wallService.serve("editReply",request);
        List<Reply> replies = (List<Reply>) wallService.serve("getReplies", request);
        Boolean found = false;
        for(int i = 0;i < replies.size(); i++){
            if(replies.get(i).getText().equals("Testing service edit") && replies.get(i).getReplyId().equals(insertedReply.getReplyId())){
                found = true;
                break;
            }
        }
        assertEquals("response should be Reply updated",response,"Reply updated");
        assertEquals("reply should be updated", found, true);
    }

    @Test
    public void testDeleteReply() throws Exception {

        HashMap<String,Object> request = new HashMap<String, Object>();
        request.put("replyId",insertedReply.getReplyId());

        request.put("authorId","3");
        request.put("parentPostId",insertedPost.getPostId());
        request.put("parentCommentId",insertedComment.getCommentId());
        request.put("mentions",mentions);
        request.put("likesCount",45);
        request.put("text","Testing");
        request.put("timestamp","Thu Jan 19 2012 01:00 PM");
        request.put("images",images);
        request.put("urls", urls);

       // LinkedHashMap<String, Object> result = (LinkedHashMap<String, Object>) wallService.serve("getReplies", request);
        List<Reply> replies = (List<Reply>) wallService.serve("getReplies", request);

        String response =  (String) wallService.serve("deleteReply",request);

       // LinkedHashMap<String, Object> testResult = (LinkedHashMap<String, Object>) wallService.serve("getReplies", request);
        List<Reply> testReplies = (List<Reply>) wallService.serve("getReplies", request);

        assertEquals("Size should decrement by one",replies.size() - 1,testReplies.size());

        assertEquals("response should be Reply deleted",response,"Reply deleted");
    }

    public Comment getComment(String commentId) {
        Comment comment = null;
        try {
            comment = arangoDB.collection(commentsCollection).getDocument(commentId,
                    Comment.class);
            System.out.println("das");
            System.out.println(comment.toString());

        } catch (ArangoDBException e) {
            System.out.println("catch");
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
    public void testEditComments() throws Exception {
        HashMap<String,Object> request = new HashMap<String,Object>();
        request.put("commentId", insertedComment.getCommentId());
        request.put("authorId", "1");
        request.put("parentPostId", insertedPost.getPostId());
        request.put("likesCount", 45);
        request.put("repliesCount", 45);
        request.put("images", images);
        request.put("urls", urls);
        request.put("mentions", mentions);
        request.put("text", "Edited Text");
        request.put("timestamp","Thu Jan 19 2012 01:00 PM");

        String response = (String) wallService.serve("editComment", request);


        List<Comment> comments = (List<Comment>) wallService.serve("getComments", request);
        Boolean found = false;
        for(int i = 0;i < comments.size(); i++){
            if(comments.get(i).getText().equals("Edited Text") && comments.get(i).getCommentId().equals(insertedComment.getCommentId())){
                found = true;
                break;
            }
        }
        System.out.println(insertedComment.getCommentId());

        assertEquals("The comment should have a new Text", found,true);
        assertEquals("Response should be Comment Updated", response, "Comment Updated");


    }

    @Test
    public void testDeleteComments() throws Exception {
        HashMap<String,Object> request = new HashMap<String,Object>();
        request.put("commentId", insertedComment.getCommentId());
        request.put("authorId", "1");
        request.put("parentPostId", insertedPost.getPostId());
        request.put("likesCount", 45);
        request.put("repliesCount", 45);
        request.put("images", images);
        request.put("urls", urls);
        request.put("mentions", mentions);

        request.put("text", "Text");
        request.put("timeStamp", "Thu Jan 19 2012 01:00 PM");


        // LinkedHashMap<String, Object> result = (LinkedHashMap<String, Object>) wallService.serve("getReplies", request);
        List<Comment> comments = (List<Comment>) wallService.serve("getComments", request);

        String response =  (String) wallService.serve("deleteComment",request);

        // LinkedHashMap<String, Object> testResult = (LinkedHashMap<String, Object>) wallService.serve("getReplies", request);
        List<Comment> testComment = (List<Comment>) wallService.serve("getComments", request);

        assertEquals("Size should decrement by one",comments.size() - 1,testComment.size());

        assertEquals("response should be comment deleted",response,"Comment deleted");


    }

    @Test
    public void testGetComments() throws Exception {
        HashMap<String,Object> request = new HashMap<String,Object>();
        request.put("parentPostId", insertedPost.getPostId());
            List<Comment> newComments = (List<Comment>) wallService.serve("getComments", request);
            assertEquals("The comment should not exist", newComments.size(),10);


    }

    @Test
    public void testAddBookmark() throws Exception {
        HashMap<String, Object> request = new HashMap<>();
        String userId = insertedUser.getUserId();
        String postId = insertedPost.getPostId();
        request.put("userId", userId);
        request.put("postId", postId);
        String response = (String) wallService.serve("addBookmark", request);
        assertEquals("response should be Success to add bookmark", response, "Success to add bookmark");
    }

    @Test
    public void testDeleteBookmark() throws Exception {
        HashMap<String, Object> request = new HashMap<>();
        String userId = insertedUser.getUserId();
        String postId = insertedPost.getPostId();
        request.put("userId", userId);
        request.put("postId", postId);
        String response = (String) wallService.serve("deleteBookmark", request);
        assertEquals("response should be Success to delete bookmark", response, "Success to delete bookmark");
    }

    @Test
    public void testGetBookmark() throws Exception {
        HashMap<String, Object> request = new HashMap<>();
        String userId = insertedUser.getUserId();
        request.put("userId", userId);
        LinkedHashMap<String, Object> result = (LinkedHashMap<String, Object>) wallService.serve("getBookmarks", request);
        int size = 1;
        assertEquals("response should be user's bookmark arraylist", result.size(), size);
    }

    @Test
    public void testAddLikeCommand() throws Exception {
        HashMap<String, Object> request = new HashMap<>();
        request.put("likerId", "100");
        request.put("likedPostId", insertedPost.getPostId());
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
    }

}
