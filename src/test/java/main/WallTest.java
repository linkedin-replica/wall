
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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.linkedin.replica.wall.cache.Cache;
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
    private static JsonArray videos;
    private static JsonArray images;
    @BeforeClass
    public static void setup() throws ClassNotFoundException, IOException, ParseException {
        String rootFolder = "src/main/resources/";
        Configuration.init(rootFolder + "app.config",
                rootFolder + "arango.test.config",
                rootFolder + "commands.config", rootFolder + "controller.config",rootFolder+ "cache.config");
        config = Configuration.getInstance();
        wallService = new WallService();
        DatabaseConnection.init();
        Cache.init();
        arangoDB = DatabaseConnection.getInstance().getArangodb().db(
                Configuration.getInstance().getArangoConfig("db.name")
        );
        dbSeed = new DatabaseSeed();
        dbSeed.insertPosts();
        dbSeed.insertComments();
        dbSeed.insertReplies();
        dbSeed.insertUsers();
        dbSeed.setFriendsAndTheirPosts();

        commentsCollection = Configuration.getInstance().getArangoConfig("collections.comments.name");
        insertedComment = dbSeed.getInsertedComments().get(0);
        insertedPost = dbSeed.getInsertedPosts().get(0);
        insertedReply = dbSeed.getInsertedReplies().get(0);
        insertedUser = dbSeed.getInsertedUsers().get(0);
        images = new JsonArray();
        videos = new JsonArray();
        images.add("OH LALA");
        videos.add("videos");


    }
      @Test
    public void testNewsfeed() throws Exception {
        HashMap<String, Object> request = new HashMap<String, Object>();
        JsonObject object = new JsonObject();
        JsonObject user = new JsonObject();
        JsonArray friends = new JsonArray();

        for(String friend:dbSeed.getInsertedUsers().get(5).getFriendsList())
            friends.add(friend);
        user.addProperty("email", dbSeed.getInsertedUsers().get(5).getEmail());
        user.addProperty("firstName", dbSeed.getInsertedUsers().get(5).getFirstName());
        user.addProperty("lastName", dbSeed.getInsertedUsers().get(5).getLastName());
        user.addProperty("firstName", dbSeed.getInsertedUsers().get(5).getFirstName());
        user.addProperty("userId", dbSeed.getInsertedUsers().get(5).getUserId());
        user.add("friendsList", friends);
          object.add("user", user);
        object.addProperty("limit",10);
        object.addProperty("offset",0);
        request.put("request", object);

        List<Post> newsfeed = (List<Post>) wallService.serve("getNewsfeed", request);
        assertEquals("Newsfeed Size should be 4", 4, newsfeed.size());
        assertEquals("Newsfeed should be ordered", "Post 1", newsfeed.get(0).getText());
        assertEquals("Newsfeed should be ordered", "Post 4", newsfeed.get(3).getText());
    }

    @Test
    public void testAddReplyService() throws Exception {

        HashMap<String, Object> request = new HashMap<String, Object>();
        JsonObject object = new JsonObject();
        object.addProperty("authorId","1");
        object.addProperty("parentPostId", insertedPost.getPostId());
        object.addProperty("parentCommentId",insertedComment.getCommentId());
        object.addProperty("text","TestTestTest");
        request.put("request", object);
        boolean response = (boolean) wallService.serve("addReply",request);
        List<Reply> replies = (List<Reply>)  wallService.serve("getReplies", request);
        Boolean found = false;
        for(int i = 0;i < replies.size(); i++){
                if(replies.get(i).getText().equals("TestTestTest")){
                    found = true;
                    break;
                }
        }
        assertEquals("added reply correctly", found, true);
        assertEquals("response should be equal Reply created",response,true);


    }

    @Test
    public void testEditReply() throws Exception {

        HashMap<String, Object> request = new HashMap<String, Object>();
        JsonObject object = new JsonObject();
        object.addProperty("replyId", insertedReply.getReplyId());
        object.addProperty("authorId",insertedReply.getAuthorId());
        object.addProperty("parentPostId",insertedReply.getParentPostId());
        object.addProperty("parentCommentId",insertedReply.getParentCommentId());
        object.addProperty("text", "Edit Working");
        request.put("request", object);
        boolean response = (boolean) wallService.serve("editReply",request);
        List<Reply> replies = (List<Reply>) wallService.serve("getReplies", request);
        Boolean found = false;
        for(int i = 0;i < replies.size(); i++){
            if(replies.get(i).getText().equals("Edit Working") && replies.get(i).getReplyId().equals(insertedReply.getReplyId())){
                found = true;
                break;
            }
        }
        assertEquals("response should true",response,true);
        assertEquals("reply should be updated", found, true);


  }

    @Test
    public void testDeleteReply() throws Exception {

        HashMap<String,Object> request = new HashMap<String, Object>();
        JsonObject object = new JsonObject();
        object.addProperty("replyId",insertedReply.getReplyId());
        object.addProperty("parentCommentId", insertedReply.getParentCommentId());
        request.put("request", object);

        List<Reply> replies = (List<Reply>) wallService.serve("getReplies", request);
        boolean response =  (boolean) wallService.serve("deleteReply",request);
        List<Reply> testReplies = (List<Reply>) wallService.serve("getReplies", request);

        assertEquals("Size should decrement by one",replies.size() - 1,testReplies.size());
        assertEquals("response should be true",response,true);
    }

    @Test
    public void testEditComment() throws Exception {
        HashMap<String,Object> request = new HashMap<String,Object>();
        JsonObject object = new JsonObject();
        object.addProperty("commentId", insertedComment.getCommentId());
        object.addProperty("authorId", "1");
        object.addProperty("parentPostId", insertedPost.getPostId());
        object.addProperty("text", "Edited Text in comment");
        request.put("request", object);
        Boolean response = (boolean) wallService.serve("editComment", request);
        List<Comment> comments = (List<Comment>) wallService.serve("getComments", request);
        Boolean found = false;
        for(int i = 0;i < comments.size(); i++){
            if(comments.get(i).getText().equals("Edited Text in comment") && comments.get(i).getCommentId().equals(insertedComment.getCommentId())){
                found = true;
                break;
            }
        }
        assertEquals("The comment should have a new Text", found,true);
        assertEquals("Response should true", response, true);



    }

    @Test
    public void testDeleteComments() throws Exception {
        HashMap<String,Object> request = new HashMap<String,Object>();
        JsonObject object = new JsonObject();
        object.addProperty("commentId", insertedComment.getCommentId());
        object.addProperty("parentPostId", insertedPost.getPostId());
        request.put("request", object);

        // LinkedHashMap<String, Object> result = (LinkedHashMap<String, Object>) wallService.serve("getReplies", request);
        List<Comment> comments = (List<Comment>) wallService.serve("getComments", request);

        boolean response =  (boolean) wallService.serve("deleteComment",request);

        // LinkedHashMap<String, Object> testResult = (LinkedHashMap<String, Object>) wallService.serve("getReplies", request);
        List<Comment> testComment = (List<Comment>) wallService.serve("getComments", request);

        assertEquals("Size should decrement by one",comments.size() - 1,testComment.size());

        assertEquals("response should be true",response,true);


    }
    public Comment getComment(String commentId) {
        Comment comment = null;
        try {
            comment = arangoDB.collection(commentsCollection).getDocument(commentId,
                    Comment.class);
        } catch (ArangoDBException e) {
            e.printStackTrace();
        }
        return comment;
    }

    @Test
    public void testGetComments() throws Exception {
        HashMap<String,Object> request = new HashMap<String,Object>();
        JsonObject object = new JsonObject();
        object.addProperty("parentPostId", insertedPost.getPostId());
        request.put("request", object);
        List<Comment> newComments = (List<Comment>) wallService.serve("getComments", request);
        assertEquals("The comment should not exist", newComments.size(),10);


    }

    @Test
    public void testAddBookmark() throws Exception {
        HashMap<String, Object> request = new HashMap<>();
        JsonObject object = new JsonObject();
        String userId = insertedUser.getUserId();
        String postId = insertedPost.getPostId();
        object.addProperty("userId", userId);
        object.addProperty("postId", postId);
        request.put("request", object);
        boolean response = (boolean) wallService.serve("addBookmark", request);
        assertEquals("response should be Success to add bookmark", response, true);
    }

    @Test
    public void testDeleteBookmark() throws Exception {
        HashMap<String, Object> request = new HashMap<>();
        JsonObject object = new JsonObject();
        String userId = insertedUser.getUserId();
        String postId = insertedPost.getPostId();
        object.addProperty("userId", userId);
        object.addProperty("postId", postId);
        request.put("request", object);
        boolean response = (boolean) wallService.serve("deleteBookmark", request);
        assertEquals("response should be Success to delete bookmark", response, true);
    }

    @Test
    public void testGetBookmark() throws Exception {
        HashMap<String, Object> request = new HashMap<>();
        JsonObject object = new JsonObject();
        String userId = insertedUser.getUserId();
        object.addProperty("userId", userId);
        request.put("request", object);
        List <Bookmark>result = (List<Bookmark>)wallService.serve("getBookmarks", request);
        int size = 1;
        assertEquals("response should be user's bookmark arraylist", result.size(), size);
    }



    @Test
    public void testAddPostCommand() throws Exception {
        HashMap<String, Object> request = new HashMap<String, Object>();
        JsonObject object = new JsonObject();
        object.addProperty("authorId","1");
        object.addProperty("type","post");
        object.addProperty("text", "Testing add post command");
        object.add("images", images);
        object.add("videos", videos);
        object.addProperty("headLine", "test");
        object.addProperty("isArticle", false);
        request.put("request", object);
        boolean response = (boolean) wallService.serve("addPost",request);
        List<Post> posts = (List<Post>)  wallService.serve("getPosts", request);
        Boolean found = false;
        for(int i = 0;i < posts.size(); i++){
            if(posts.get(i).getText().equals("Testing add post command")){
                found = true;
                break;
            }
        }
        assertEquals("added post correctly", found, true);
        assertEquals("response should be equal Post created",response,true);

    }

    @Test
    public void testEditPostCommand() throws Exception {
        HashMap<String, Object> request = new HashMap<String, Object>();
        JsonObject object = new JsonObject();
        object.addProperty("postId", insertedPost.getPostId());
        object.addProperty("authorId",insertedPost.getAuthorId());
        object.addProperty("type","post");
        object.addProperty("headLine","headLine");
        object.addProperty("isArticle",false);
        object.addProperty("text", "Testing edit post command");
        object.add("images", images);
        object.addProperty("commentsCount", 2);
        request.put("request", object);
        boolean response = (boolean) wallService.serve("editPost",request);
        List<Post> posts = (List<Post>) wallService.serve("getPosts", request);
        Boolean found = false;
        for(int i = 0;i < posts.size(); i++){
            if(posts.get(i).getText().equals("Testing edit post command")){
                found = true;
                break;
            }
        }
        assertEquals("response should be Post updated",response,true);
        assertEquals("post should be updated", found, true);
    }

    @Test
    public void testDeletePostCommand() throws Exception {

        HashMap<String,Object> request = new HashMap<String, Object>();
        JsonObject object = new JsonObject();
        object.addProperty("postId",insertedPost.getPostId());
        object.addProperty("authorId", insertedPost.getAuthorId());
        request.put("request", object);
        List<Post> posts = (List<Post>) wallService.serve("getPosts", request);
        boolean response =  (boolean) wallService.serve("deletePost",request);
        List<Post> testPosts = (List<Post>) wallService.serve("getPosts", request);

        assertEquals("response should be Post deleted",response,true);
        assertEquals("collection size should decrement by one",posts.size() - 1,testPosts.size());

    }

    @Test
    public void testGetPostsCommand() throws Exception {
        HashMap<String,Object> request = new HashMap<String,Object>();
        JsonObject object = new JsonObject();
        object.addProperty("authorId", insertedPost.getAuthorId());
        request.put("request", object);
        List<Post> posts = (List<Post>) wallService.serve("getPosts", request);
        Boolean found = false;
        for(int i = 0;i < posts.size(); i++){
            if(posts.get(i).getAuthorId().equals(insertedPost.getAuthorId())){
                found = true;
                break;
            }
        }
        assertEquals("Incorrect post retrieved as the authorId does not match the existing authorId.", true, found);

    }


    @AfterClass
    public static void tearDown() throws ArangoDBException, ClassNotFoundException, IOException {
        dbSeed.deleteAllUsers();
        dbSeed.deleteAllPosts();
        dbSeed.deleteAllReplies();
        dbSeed.deleteAllComments();
        DatabaseConnection.getInstance().closeConnections();
    }

}

