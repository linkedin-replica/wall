package databaseHandlers;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
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
    private static Properties properties;
    private static ArangoDB arangoDB;
    private static String dbName;
    private static String  likesCollection;
    private static DatabaseHandler dbHandler;


    @BeforeClass
    public static void setup() throws ClassNotFoundException, IOException {
        // startup SearchEngine
        String[] args = {"db_config", "src/main/resources/command_config"};
        Wall.start(args);
        arangoDB = DatabaseConnection.getInstance().getArangodb();
        properties = new Properties();
        properties.load(new FileInputStream("db_config"));
        dbName = properties.getProperty("arangodb.name");
        likesCollection = properties.getProperty("collections.likes.name");
        dbHandler = new ArangoWallHandler();
        dbSeed = new DatabaseSeed();
        dbSeed.insertUsers();
        dbSeed.insertPosts();
        dbSeed.insertReplies();
        dbSeed.insertLikes();
        dbSeed.insertComments();
    }
    @Test
    public void testGetPostsLikes() {
        String postId = "15";
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
    public void testGetCommentsLikes() {
        String commentId = "16";
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
    public void testGetRepliesLikes(){
        String replyId = "18";
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
    public void testAddLikes() {
        Long likesCollectionSize = arangoDB.db(dbName).collection(likesCollection).count().getCount();
        Like like = new Like( "100", "200", null, null, "name", "headLine", "urlX");
        dbHandler.addLike(like);
        Long newLikesCollectionSize = arangoDB.db(dbName).collection(likesCollection).count().getCount();
        Long expectedCollectionSize = likesCollectionSize + 1;
        Like retrievedLike = arangoDB.db(dbName).collection(likesCollection).getDocument(like.getLikeId(),Like.class);
        assertEquals("The size of the likesCollection should have increased by one", expectedCollectionSize, newLikesCollectionSize);
        assertEquals("The likerId should match the one in the like inserted", "100", retrievedLike.getLikerId());
        assertEquals("The likedPostId should match the one in the like inserted", "200", retrievedLike.getLikedPostId());
        assertEquals("The likedCommentId should match the one in the like inserted", null, retrievedLike.getLikedCommentId());
        assertEquals("The likedReplyId should match the one in the like inserted", null, retrievedLike.getLikedReplyId());
        assertEquals("The userName should match the one in the like inserted", "name", retrievedLike.getUserName());
        assertEquals("The headLine should match the one in the like inserted", "headLine", retrievedLike.getHeadLine());
        assertEquals("The imageUrl should match the one in the like inserted", "urlX", retrievedLike.getImageUrl());

    }
    @Test public void testDeleteLikes() {
        String query = "FOR l in " + likesCollection + " RETURN l";
        ArangoCursor<Like> likesCursor = arangoDB.db(dbName).query(query, new HashMap<String, Object>(), null, Like.class);
        ArrayList<Like> allLikes = new ArrayList<Like>(likesCursor.asListRemaining());
        Like existingLike = allLikes.get(0);
        String existingLikeId = existingLike.getLikeId();
        Long collectionSize = arangoDB.db(dbName).collection(likesCollection).count().getCount();
        Long expectedCollectionSize = collectionSize - 1;
        dbHandler.deleteLike(existingLike);
        Long newCollectionSize = arangoDB.db(dbName).collection(likesCollection).count().getCount();
        Boolean docWithIdExists = arangoDB.db(dbName).collection(likesCollection).documentExists(existingLikeId);
        assertEquals("The size of the likesCollection should have decreased by one", expectedCollectionSize, newCollectionSize);
        assertEquals("There should be no document in collection with this id", false, docWithIdExists);

    }

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