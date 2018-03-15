package databaseHandlers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.util.MapBuilder;
import com.linkedin.replica.wall.config.DatabaseConnection;
import com.linkedin.replica.wall.handlers.DatabaseHandler;
import com.linkedin.replica.wall.handlers.impl.ArangoWallHandler;
import com.linkedin.replica.wall.main.Wall;
import com.linkedin.replica.wall.models.Like;
import com.linkedin.replica.wall.services.WallService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.arangodb.ArangoDBException;

import static org.junit.Assert.assertEquals;

public class ArangoHandlerTest {
    private static DatabaseSeed dbSeed;
    private static WallService wallService;
    private static Properties properties;
    private static ArangoDB arangoDB;
    private static String dbName;
    private static String  likesCollection;


    @BeforeClass
    public static void setup() throws ClassNotFoundException, IOException {
        // startup SearchEngine
        String[] args = {"db_config", "src/main/resources/command_config"};
        Wall.start(args);
        wallService = new WallService();
        arangoDB = DatabaseConnection.getInstance().getArangodb();
        properties = new Properties();
        properties.load(new FileInputStream("db_config"));
        dbName = properties.getProperty("arangodb.name");
        likesCollection = properties.getProperty("collections.likes.name");

        dbSeed = new DatabaseSeed();
        dbSeed.insertUsers();
        dbSeed.insertPosts();
        dbSeed.insertReplies();
        dbSeed.insertLikes();
        dbSeed.insertComments();
    }
    @Test
    public void testGetPostsLikes() throws IOException, ClassNotFoundException {
        String postId = "15";
        DatabaseHandler dbHandler = new ArangoWallHandler();
        boolean equalsPostId = false;
        List<Like> postLikes = dbHandler.getPostLikes(postId);
        for(Like like: postLikes) {
            if(like.getLikedPostId().equals(postId))
                equalsPostId = true;
            assertEquals("Incorrect like retrieved as the likedPostId does not match the postId.", true, equalsPostId);
            equalsPostId = false;

        }
    }
    @Test
    public void testGetCommentsLikes() throws IOException, ClassNotFoundException {
        String commentId = "16";
        DatabaseHandler dbHandler = new ArangoWallHandler();
        boolean equalsCommentId = false;
        List<Like> commentLikes = dbHandler.getCommentLikes(commentId);
        for(Like like: commentLikes) {
            if(like.getLikedCommentId().equals(commentId))
                equalsCommentId = true;
            assertEquals("Incorrect like retrieved as the likedCommentId does not match the commentId.", true, equalsCommentId);
            equalsCommentId = false;

        }
    }
    @Test
    public void testGetRepliesLikes() throws IOException, ClassNotFoundException {
        String replyId = "18";
        DatabaseHandler dbHandler = new ArangoWallHandler();
        boolean equalsReplyId = false;
        List<Like> replyLikes = dbHandler.getReplyLikes(replyId);
        for(Like like: replyLikes) {
            if(like.getLikedReplyId().equals(replyId))
                equalsReplyId = true;
            assertEquals("Incorrect like retrieved as the likedReplyId does not match the replyId.", true, equalsReplyId);
            equalsReplyId = false;

        }
    }
    @Test
    public void testAddLikes() throws IOException, ClassNotFoundException {
        Long likesCollectionSize = arangoDB.db(dbName).collection(likesCollection).count().getCount();
        Like like = new Like( "100", "200", null, null, "name", "headLine", "urlX");
        DatabaseHandler dbHandler = new ArangoWallHandler();
        dbHandler.addLike(like);
        Long newLikesCollectionSize = arangoDB.db(dbName).collection(likesCollection).count().getCount();
        Long expectedCollectionSize = likesCollectionSize + 1;
        assertEquals("The size of the likesCollection should have increased by one", expectedCollectionSize, newLikesCollectionSize);
        Like retrievedLike = arangoDB.db(dbName).collection(likesCollection).getDocument(like.getLikeId(),Like.class);
        assertEquals("The likerId should match the one in the like inserted", "100", retrievedLike.getLikerId());
        assertEquals("The likedPostId should match the one in the like inserted", "200", retrievedLike.getLikedPostId());
        assertEquals("The likedCommentId should match the one in the like inserted", null, retrievedLike.getLikedCommentId());
        assertEquals("The likedReplyId should match the one in the like inserted", null, retrievedLike.getLikedReplyId());
        assertEquals("The userName should match the one in the like inserted", "name", retrievedLike.getUserName());
        assertEquals("The headLine should match the one in the like inserted", "headLine", retrievedLike.getHeadLine());
        assertEquals("The imageUrl should match the one in the like inserted", "urlX", retrievedLike.getImageUrl());
        
    }

//    @Test
//    public void testGetPostLikes() throws ClassNotFoundException, IllegalAccessException, ParseException, InstantiationException {
//        String postId = "15";
//        HashMap<String,String> request = new HashMap<String,String>();
//        request.put("likedPostId", postId);
//        LinkedHashMap<String, Object> response = wallService.serve("getPostLikes", request);
//        List<Like> postLikes = (List<Like>) response.get("response");
//        System.out.println(postLikes.size());
//        boolean check = false;
//        for(Like like : postLikes){
//            if(like.getLikedPostId().equals(postId))
//                check = true;
//
//            assertEquals("Incorrect like retrieved as the likedPostId does not match the postId.", true, check);
//            check = false;
//        }
//    }
//
//    @Test
//    public void testGetCommentLikes() throws ClassNotFoundException, IllegalAccessException, ParseException, InstantiationException {
//        String commentId = "16";
//        HashMap<String,String> request = new HashMap<String,String>();
//        request.put("likedCommentId", commentId);
//        LinkedHashMap<String, Object> response = wallService.serve("getCommentLikes", request);
//        List<Like> commentLikes = (List<Like>) response.get("response");
//        System.out.println(commentLikes.get(0).toString());
//        System.out.println(commentLikes.size());
//        boolean check = false;
//        for(Like like : commentLikes){
//            if(like.getLikedCommentId().equals(commentId))
//                check = true;
//
//            assertEquals("Incorrect like retrieved as the likedCommentId does not match the commentId.", true, check);
//            check = false;
//        }
//    }
//
//    @Test
//    public void testGetReplyLikes() throws ClassNotFoundException, IllegalAccessException, ParseException, InstantiationException {
//        String replyId = "18";
//        HashMap<String,String> request = new HashMap<String,String>();
//        request.put("likedReplyId", replyId);
//        LinkedHashMap<String, Object> response = wallService.serve("getReplyLikes", request);
//        List<Like> replyLikes = (List<Like>) response.get("response");
//        System.out.println(replyLikes.get(0).toString());
//        System.out.println(replyLikes.size());
//        boolean check = false;
//        for(Like like : replyLikes){
//            if(like.getLikedReplyId().equals(replyId))
//                check = true;
//
//            assertEquals("Incorrect like retrieved as the likedReplyId does not match the replyId.", true, check);
//            check = false;
//        }
//    }

//    @Test
//    public void testAddLikes() throws ClassNotFoundException, InstantiationException, ParseException, IllegalAccessException {
//        HashMap<String,String> request = new HashMap<String,String>();
//        request.put("likerId", "100");
//        request.put("likedPostId", "99");
//        request.put("likedCommentId", null);
//        request.put("likedReplyId", null);
//        request.put("userName", "Yara");
//        request.put("headLine", "Yara and 5 others");
//        request.put("imageUrl", "urlX");
//        LinkedHashMap<String, Object> response = wallService.serve("addLike", request);
//        String [] message = ((String) response.get("response")).split(",");
//        String query = "FOR l IN " + likesCollection + " FILTER l.likedPostId == @postId  && l.likerId == @likerId RETURN l";
//        Map<String, Object> bindVars = new MapBuilder().put("postId", "99").get();
//        bindVars.put("likerId", "100");
//        ArangoCursor<Like> cursor = arangoDB.db(dbName).query(query, bindVars, null,
//                Like.class);
//        List<Like> retrievedLikes = new ArrayList<Like>();
//        while (cursor.hasNext())
//            retrievedLikes.add(cursor.next());
//        System.out.println(retrievedLikes.size() + retrievedLikes.get(0).toString());
//        assertEquals("Only one like should have the likerId and postId", 1, retrievedLikes.size());
//

//    }

//    @Test
//    public void testDeleteLikes() throws ClassNotFoundException, InstantiationException, ParseException, IllegalAccessException {
////        HashMap<String,String> request = new HashMap<String,String>();
////        request.put("likerId", "100");
////        request.put("likedPostId", "99");
////        request.put("likedCommentId", null);
////        request.put("likedReplyId", null);
////        request.put("userName", "Yara");
////        request.put("headLine", "Yara and 5 others");
////        request.put("imageUrl", "urlX");
////        Like like = new Like(request.get("likerId"), request.get("likedPostId"), request.get("likedCommentId"), request.get("likedReplyId"), request.get("userName"), request.get("headLine"),request.get("imageUrl"));
////        arangoDB.db(dbName).collection(likesCollection).insertDocument(like);
////        String query = "FOR l IN " + likesCollection + " FILTER l.likedPostId == @postId  && l.likerId == @likerId RETURN l";
////        Map<String, Object> bindVars = new MapBuilder().put("postId", "99").get();
////        bindVars.put("likerId", "100");
////        ArangoCursor<Like> cursor = arangoDB.db(dbName).query(query, bindVars, null,
////                Like.class);
////        List<Like> retrievedLikes = new ArrayList<Like>();
////        while (cursor.hasNext())
////            retrievedLikes.add(cursor.next());
////        System.out.println(retrievedLikes.size() + retrievedLikes.get(0).toString());
////        assertEquals("Only one like should have the likerId and postId", 1, retrievedLikes.size());
////
////        LinkedHashMap<String, Object> response = wallService.serve("deleteLike", request);
////        String query1 = "FOR l IN " + likesCollection + " FILTER l.likedPostId == @postId  && l.likerId == @likerId RETURN l";
////        Map<String, Object> bindVars1 = new MapBuilder().put("postId", "99").get();
////        bindVars.put("likerId", "100");
////        ArangoCursor<Like> cursor1 = arangoDB.db(dbName).query(query1, bindVars1, null,
////                Like.class);
////        List<Like> retrievedLikes1 = new ArrayList<Like>();
////        while (cursor1.hasNext())
////            retrievedLikes1.add(cursor1.next());
////        System.out.println(retrievedLikes.size());
////        assertEquals("There should be no like with the likerId and postId", 0, retrievedLikes.size());

//    }


    @AfterClass
    public static void tearDown() throws ArangoDBException, ClassNotFoundException, IOException, SQLException{
        dbSeed.deleteAllUsers();
        dbSeed.deleteAllPosts();
        dbSeed.deleteAllReplies();
        dbSeed.deleteAllComments();
        dbSeed.deleteAllLikes();
        Wall.shutdown();
    }

}