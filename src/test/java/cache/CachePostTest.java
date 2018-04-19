package cache;

import com.arangodb.ArangoDBException;
import com.arangodb.ArangoDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
        mentions.add("test");
        hashtags.add("#scalable");
        images.add("ahla image");
        urls.add("scalable");
        videos.add("videos");
        shares.add("shares");
        insertedPost = dbSeed.getInsertedPosts().get(0);
    }
    @Test
    public void testDeleteCachedPost() throws Exception {

        HashMap<String,Object> request = new HashMap<String, Object>();
        JsonObject object = new JsonObject();
        object.addProperty("postId",insertedPost.getPostId());
        object.addProperty("authorId", insertedPost.getAuthorId());
        request.put("request", object);

        postsCacheHandler.cachePost(insertedPost.getPostId(),insertedPost);
        wallService.serve("deletePost",request);
        Post postCached = (Post) postsCacheHandler.getPost(insertedPost.getPostId(),Post.class);
        assertEquals("Returned post should be null",null,postCached);
    }
    @Test
    public void testEditCachedPost() throws Exception {

        HashMap<String, Object> request = new HashMap<String, Object>();
        JsonObject object = new JsonObject();
        object.addProperty("postId", insertedPost.getPostId());
        object.addProperty("authorId",insertedPost.getAuthorId());
        object.addProperty("type","post");
        object.addProperty("headLine","headLine");
        object.addProperty("isArticle",false);
        object.addProperty("text", "Testing edit post command");
        request.put("request", object);
        postsCacheHandler.cachePost(insertedPost.getPostId(),insertedPost);
        wallService.serve("editPost",request);
        Post postCached = (Post) postsCacheHandler.getPost(insertedPost.getPostId(),Post.class);
        assertEquals("Text should be same in both","Testing edit post command",postCached.getText());
    }

    @Test
    public void testAddPostToCache() throws Exception {

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
