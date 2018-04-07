package cache;

import com.arangodb.ArangoDBException;
import com.arangodb.ArangoDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.linkedin.replica.wall.cache.Cache;
import com.linkedin.replica.wall.cache.handlers.impl.JedisCacheHandler;
import com.linkedin.replica.wall.config.Configuration;
import com.linkedin.replica.wall.database.DatabaseConnection;
import com.linkedin.replica.wall.models.*;
import com.linkedin.replica.wall.services.WallService;
import databaseHandlers.DatabaseSeed;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class CachePostTest {
    private static WallService wallService;
    private static ArangoDatabase arangoDB;
    static Configuration config;
    private static DatabaseSeed dbSeed;
    private static JedisCacheHandler postsCacheHandler;
    private static Post insertedPost;
    private static Gson gson;
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
                rootFolder + "commands.config", rootFolder + "controller.config" ,rootFolder + "cache.config");
        config = Configuration.getInstance();
        wallService = new WallService();
        gson = new Gson();
        Cache.init();
        postsCacheHandler = new JedisCacheHandler();
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
        mentions = new JsonArray();
        hashtags = new JsonArray();
        urls = new JsonArray();
        images = new JsonArray();
        videos = new JsonArray();
        shares = new JsonArray();
        mentions.add("safa");
        hashtags.add("#scalable");
        images.add("ahla image");
        urls.add("scalable");
        videos.add("videos");
        shares.add("shares");
        insertedPost = dbSeed.getInsertedPosts().get(0);
    }
    @Test
    public void testGetCachedPost() throws Exception {
        HashMap<String, Object> request = new HashMap<>();
        String postId = "317844";
        request.put("postId", postId);
        Post post = (Post) wallService.serve("getPost", request);
        Post postCached = (Post) postsCacheHandler.getPost(postId,Post.class);
       assertEquals("Both posts should have the same ID",post.getPostId(),postCached.getPostId());
    }
    @Test
    public void testDeleteCachedPost() throws Exception {

        HashMap<String, Object> request = new HashMap<String, Object>();
        request.put("postId", insertedPost.getPostId());
        request.put("authorId",insertedPost.getAuthorId());
        request.put("type",insertedPost.getType());
        request.put("companyId", insertedPost.getCompanyId());
        request.put("privacy", insertedPost.getPrivacy());
        request.put("text", "Testing edit post in cache");
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
        postsCacheHandler.cachePost(insertedPost.getPostId(),insertedPost);
        wallService.serve("deletePost",request);
        Post postCached = (Post) postsCacheHandler.getPost(insertedPost.getPostId(),Post.class);
        assertEquals("Returned post should be null",null,postCached);
    }
    @Test
    public void testEditCachedPost() throws Exception {

        HashMap<String, Object> request = new HashMap<String, Object>();
        request.put("postId", insertedPost.getPostId());
        request.put("authorId",insertedPost.getAuthorId());
        request.put("type",insertedPost.getType());
        request.put("companyId", insertedPost.getCompanyId());
        request.put("privacy", insertedPost.getPrivacy());
        request.put("text", "Testing edit post in cache");
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
        postsCacheHandler.cachePost(insertedPost.getPostId(),insertedPost);
        wallService.serve("editPost",request);
        Post postCached = (Post) postsCacheHandler.getPost(insertedPost.getPostId(),Post.class);
        assertEquals("Text should be same in both","Testing edit post command",postCached.getText());
    }

    @Test
    public void testAddPostToCache() throws Exception {

        HashMap<String, Object> request = new HashMap<String, Object>();
        request.put("authorId","1");
        request.put("type","post");
        request.put("companyId", "3");
        request.put("privacy", "friends");
        request.put("text", "Testing add post command");
        request.put("hashtags", hashtags);
        request.put("mentions", mentions);
        request.put("likesCount",67);
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
        wallService.serve("addPost",request);
        List<Post> posts = (List<Post>)  wallService.serve("getPosts", request);
        Boolean found = false;
        String postId="";
        for(int i = 0;i < posts.size(); i++){
            if(posts.get(i).getText().equals("Testing add post command")){
                found = true;
                postId = posts.get(i).getPostId();
                break;
            }
        }
        Post postCached = (Post) postsCacheHandler.getPost(postId,Post.class);
        assertEquals("post text should be the same in both", "Testing add post command", postCached.getText());
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
