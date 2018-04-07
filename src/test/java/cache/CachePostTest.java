package cache;

import com.arangodb.ArangoDBException;
import com.arangodb.ArangoDatabase;
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
import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;

public class cachePostTest {
    private static WallService wallService;
    private static ArangoDatabase arangoDB;
    static Configuration config;
    private static DatabaseSeed dbSeed;
    private static JedisCacheHandler postsCacheHandler;

    @BeforeClass
    public static void setup() throws ClassNotFoundException, IOException, ParseException {
        String rootFolder = "src/main/resources/";
        Configuration.init(rootFolder + "app.config",
                rootFolder + "arango.test.config",
                rootFolder + "commands.config", rootFolder + "controller.config" ,rootFolder + "cache.config");
        config = Configuration.getInstance();
        wallService = new WallService();
        Cache.init();
        postsCacheHandler = new JedisCacheHandler();
        DatabaseConnection.init();
        arangoDB = DatabaseConnection.getInstance().getArangodb().db(
                Configuration.getInstance().getArangoConfig("db.name")
        );
       dbSeed = new DatabaseSeed();
//        dbSeed.insertPosts();
//        dbSeed.insertComments();
//        dbSeed.insertReplies();
//        dbSeed.insertLikes();
//        dbSeed.insertUsers();


    }
    @Test
    public void testGetCachedPost() throws Exception {
        HashMap<String, Object> request = new HashMap<>();
        String postId = "317832";
        request.put("postId", postId);
        Post post = (Post) wallService.serve("getPost", request);
        Post postCached = (Post) postsCacheHandler.getPost(postId,Post.class);
       assertEquals("Both posts should have the same ID",post.getPostId(),postCached.getPostId());
    }
    @Test
    public void testDeleteCachedPost() throws Exception {
        HashMap<String, Object> request = new HashMap<>();
        String postId = "317832";
        request.put("postId", postId);
        wallService.serve("deletePost", request);
        postsCacheHandler.deletePost(postId);
        Post postCached = (Post) postsCacheHandler.getPost(postId,Post.class);
        assertEquals("Returned post should be null",null,postCached);
    }

    @AfterClass
    public static void tearDown() throws ArangoDBException, ClassNotFoundException, IOException {
//        dbSeed.deleteAllUsers();
//        dbSeed.deleteAllPosts();
//        dbSeed.deleteAllReplies();
//        dbSeed.deleteAllComments();
//        dbSeed.deleteAllLikes();
        DatabaseConnection.getInstance().closeConnections();
    }
}
