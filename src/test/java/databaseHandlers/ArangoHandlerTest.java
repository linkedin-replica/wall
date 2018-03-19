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
import com.linkedin.replica.wall.models.Like;
import com.linkedin.replica.wall.main.Main;
import com.linkedin.replica.wall.models.Bookmark;
import com.linkedin.replica.wall.models.UserProfile;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.arangodb.ArangoDBException;

import static org.junit.Assert.assertEquals;

public class ArangoHandlerTest {
    private static DatabaseSeed dbSeed;
    private static DatabaseHandler arangoWallHandler;
    private static ArangoDB arangoDB;
    private static Properties properties;
    private static String dbName;
    private static String likesCollection;
    private String repliesCollection;
    private String commentsCollection;
    private String postsCollection;
    private static String usersCollection;
    private ArrayList<UserProfile> insertedUsers;

    @BeforeClass
    public static void setup() throws ClassNotFoundException, IOException {
        // startup SearchEngine
        String[] args = {"db_config", "src/main/resources/command_config"};
        arangoDB = DatabaseConnection.getInstance().getArangodb();
        properties = new Properties();
        properties.load(new FileInputStream("db_config"));
        dbName = properties.getProperty("arangodb.name");
        likesCollection = properties.getProperty("collections.likes.name");
        usersCollection = properties.getProperty("collections.users.name");
        arangoWallHandler = new ArangoWallHandler();
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
        List<Like> postLikes = arangoWallHandler.getPostLikes(postId);
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
        List<Like> commentLikes = arangoWallHandler.getCommentLikes(commentId);
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
        List<Like> replyLikes = arangoWallHandler.getReplyLikes(replyId);
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
        arangoWallHandler.addLike(like);
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
        arangoWallHandler.deleteLike(existingLike);
        Long newCollectionSize = arangoDB.db(dbName).collection(likesCollection).count().getCount();
        Boolean docWithIdExists = arangoDB.db(dbName).collection(likesCollection).documentExists(existingLikeId);
        assertEquals("The size of the likesCollection should have decreased by one", expectedCollectionSize, newCollectionSize);
        assertEquals("There should be no document in collection with this id", false, docWithIdExists);

    }
    /**
     * testing Adding bookmark function
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Test
    public void testAddBookmark() throws IOException, ClassNotFoundException {
        UserProfile user = dbSeed.getInsertedUsers().get(0);
        String userId = user.getUserId();
        String postId = "P123";
        int bookmarkNo = user.getBookmarks().size() + 1;
        Bookmark bookmark = new Bookmark(userId, postId);
        arangoWallHandler.addBookmark(bookmark);
        UserProfile retrievedUser = arangoDB.db(dbName).collection(usersCollection).getDocument(userId, UserProfile.class);
        ArrayList<Bookmark> updatedBookmarks = retrievedUser.getBookmarks();
        Bookmark retrievedBookmark = updatedBookmarks.get(updatedBookmarks.size() - 1);
        assertEquals("size of bookmarks should increased by one", updatedBookmarks.size() , bookmarkNo);
        assertEquals("userID should be the same in the inserted bookmark", retrievedBookmark.getUserId(), bookmark.getUserId());
        assertEquals("postID should be the same in the inserted bookmark", retrievedBookmark.getPostId(), bookmark.getPostId());
    }

    /**
     * test DeleteBookmark function
     */
    @Test
    public void testDeleteBookmark(){
        UserProfile user = dbSeed.getInsertedUsers().get(0);
        String userId = user.getUserId();
        int bookmarkNo = user.getBookmarks().size() - 1;
        Bookmark bookmark = user.getBookmarks().get(0);
        arangoWallHandler.deleteBookmark(bookmark);
        UserProfile retrievedUser = arangoDB.db(dbName).collection(usersCollection).getDocument(userId, UserProfile.class);
        ArrayList<Bookmark> updatedBookmarks = retrievedUser.getBookmarks();
        System.out.println(updatedBookmarks.size());
        assertEquals("size of bookmarks should decreased by one", updatedBookmarks.size() , bookmarkNo);

    }

    /**
     * testing getBookmarks function
     */
    @Test
    public void testGetBookmarks(){
        UserProfile user = dbSeed.getInsertedUsers().get(0);
        String userId = user.getUserId();
        ArrayList<Bookmark> bookmarks = user.getBookmarks();
        ArrayList<Bookmark> retrievedBookmarks = arangoWallHandler.getBookmarks(userId);
        assertEquals("size of bookmarks arrays should be equal", bookmarks.size() , retrievedBookmarks.size());
        boolean check = true;
        for (int i = 0; i < bookmarks.size(); i++){
            if(!bookmarks.get(i).equals(retrievedBookmarks.get(i))){
                check = false;
                break;
            }
        }

        assertEquals("bookmarks arrays should be identical", check , true);


    }

    @AfterClass
    public static void tearDown() throws ArangoDBException, ClassNotFoundException, IOException, SQLException{
        dbSeed.deleteAllUsers();
        dbSeed.deleteAllPosts();
        dbSeed.deleteAllReplies();
        dbSeed.deleteAllComments();
        dbSeed.deleteAllLikes();
        DatabaseConnection.getInstance().closeConnections();    }

}