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
    private static JsonArray mentions;
    private static JsonArray videos;
    private static JsonArray urls;
    private static JsonArray images;
    private static JsonArray hashtags;
    private static JsonArray shares;
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
        hashtags = new JsonArray();
        urls = new JsonArray();
        images = new JsonArray();
        videos = new JsonArray();
        shares = new JsonArray();
        mentions.add("yara");
        hashtags.add("#msa");
        images.add("bla bla ");
        urls.add("hania");
        videos.add("videos");
        shares.add("shares");


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
        String response = (String) wallService.serve("addReply",request);
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
        request.put("parentCommentId", insertedReply.getParentCommentId());

        List<Reply> replies = (List<Reply>) wallService.serve("getReplies", request);
        String response =  (String) wallService.serve("deleteReply",request);
        List<Reply> testReplies = (List<Reply>) wallService.serve("getReplies", request);

        assertEquals("Size should decrement by one",replies.size() - 1,testReplies.size());
        assertEquals("response should be Reply deleted",response,"Reply deleted");
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
        List<Like> likes = (List<Like>) wallService.serve("getPostLikes", request);
        String response = (String)wallService.serve("addLike",request);
        List<Like> testLikes = (List<Like>)  wallService.serve("getPostLikes", request);

        assertEquals("response should be equal Reply created",response,"Like added");
        assertEquals("collection size should incremented by one",likes.size() + 1,testLikes.size());

    }

    @Test
    public void testDeleteLikeCommand() throws Exception {
        HashMap<String, Object> request = new HashMap<>();
        request.put("likeId", insertedLike.getLikeId());
        request.put("likedPostId", insertedPost.getPostId());
        List<Like> likes = (List<Like>) wallService.serve("getPostLikes", request);
        String response = (String) wallService.serve("deleteLike",request);
        List<Like> testLikes = (List<Like>) wallService.serve("getPostLikes", request);

        assertEquals("response should be equal Reply created",response,"Like deleted");
        assertEquals("collection size should decrement by one",likes.size() - 1,testLikes.size());

    }

    @Test
    public void testGetPostLikesCommand() throws Exception {
        HashMap<String, Object> request = new HashMap<>();
        String existingPostId =insertedLike.getLikedPostId();
        request.put("likedPostId", existingPostId);
        boolean equalsPostId = true;
        List<Like> likes = (List<Like>) wallService.serve("getPostLikes",request);
        for(Like like:likes){
            if(!like.getLikedPostId().equals(existingPostId))
                equalsPostId = false;
        }
        assertEquals("Incorrect like retrieved as the likedPostId does not match the existingPostId.", true, equalsPostId);
    }

    @Test
    public void testGetCommentLikesCommand() throws Exception {
        HashMap<String, Object> request = new HashMap<>();
        String existingCommentId = dbSeed.getInsertedLikes().get(1).getLikedCommentId();
        request.put("likedCommentId", existingCommentId);
        boolean equalsCommentId = true;
        List<Like> likes = (List<Like>) wallService.serve("getCommentLikes",request);
        for(Like like:likes){
            if(!like.getLikedCommentId().equals(existingCommentId))
                equalsCommentId = false;
        }
        assertEquals("Incorrect like retrieved as the likedCommentId does not match the existingCommentId.", true, equalsCommentId);
    }

    @Test
    public void testGetReplyLikesCommand() throws Exception {
        HashMap<String, Object> request = new HashMap<>();
        String existingReplyId = dbSeed.getInsertedLikes().get(2).getLikedReplyId();
        request.put("likedReplyId", existingReplyId);
        boolean equalsReplyId = true;
        List<Like> likes = (List<Like>) wallService.serve("getReplyLikes",request);
        for(Like like:likes){
            if(!like.getLikedReplyId().equals(existingReplyId))
                equalsReplyId = false;
        }
        assertEquals("Incorrect like retrieved as the likedReplyId does not match the existingReplyId.", true, equalsReplyId);

    }

    @Test
    public void testAddPostCommand() throws Exception {
        HashMap<String, Object> request = new HashMap<String, Object>();
        request.put("authorId","1");
        request.put("type","post");
        request.put("companyId", "3");
        request.put("privacy", "friends");
        request.put("text", "Testing add post command");
        request.put("hashtags", hashtags);
        request.put("mentions", mentions);
        request.put("likesCount",55);
        request.put("images", images);
        request.put("videos", videos);
        request.put("urls", urls);
        request.put("commentsCount", 50);
        request.put("shares", shares);
        request.put("isCompanyPost", false);
        request.put("isPrior", false);
        request.put("headLine", "test");
        request.put("isArticle", false);
        request.put("timestamp", "Mon Mar 19 2012 01:00 PM");

        String response = (String) wallService.serve("addPost",request);
        List<Post> posts = (List<Post>)  wallService.serve("getPosts", request);
        Boolean found = false;
        for(int i = 0;i < posts.size(); i++){
            if(posts.get(i).getText().equals("Testing add post command")){
                found = true;
                break;
            }
        }
        assertEquals("added post correctly", found, true);
        assertEquals("response should be equal Post created",response,"Post Created");


    }

    @Test
    public void testEditPostCommand() throws Exception {
        HashMap<String, Object> request = new HashMap<String, Object>();
        request.put("postId", insertedPost.getPostId());
        request.put("authorId",insertedPost.getAuthorId());
        request.put("type",insertedPost.getType());
        request.put("companyId", insertedPost.getCompanyId());
        request.put("privacy", insertedPost.getPrivacy());
        request.put("text", "Testing edit post command");
        request.put("hashtags", hashtags);
        request.put("mentions", mentions);
        request.put("likesCount",insertedPost.getLikesCount());
        request.put("images", images);
        request.put("videos", videos);
        request.put("urls", urls);
        request.put("commentsCount", insertedPost.getCommentsCount());
        request.put("shares", shares);
        request.put("isCompanyPost", insertedPost.isCompanyPost());
        request.put("isPrior", insertedPost.isPrior());
        request.put("headLine", insertedPost.getHeadLine());
        request.put("isArticle", insertedPost.isArticle());

        String response = (String) wallService.serve("editPost",request);

        List<Post> posts = (List<Post>) wallService.serve("getPosts", request);
        Boolean found = false;
        for(int i = 0;i < posts.size(); i++){
            if(posts.get(i).getText().equals("Testing edit post command")){
                found = true;
                break;
            }
        }
        assertEquals("response should be Post updated",response,"Post Updated");
        assertEquals("post should be updated", found, true);
    }

    @Test
    public void testDeletePostCommand() throws Exception {

        HashMap<String,Object> request = new HashMap<String, Object>();
        request.put("postId",insertedPost.getPostId());
        request.put("authorId", insertedPost.getAuthorId());
        List<Post> posts = (List<Post>) wallService.serve("getPosts", request);
        String response =  (String) wallService.serve("deletePost",request);
        List<Post> testPosts = (List<Post>) wallService.serve("getPosts", request);

        assertEquals("response should be Post deleted",response,"Post Deleted");
        assertEquals("collection size should decrement by one",posts.size() - 1,testPosts.size());

    }

    @Test
    public void testGetPostsCommand() throws Exception {
        HashMap<String,Object> request = new HashMap<String,Object>();
        request.put("authorId", insertedPost.getAuthorId());
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
        dbSeed.deleteAllLikes();
        DatabaseConnection.getInstance().closeConnections();
    }

}
