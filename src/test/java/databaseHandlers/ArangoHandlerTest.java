package databaseHandlers;

import java.awt.print.Book;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import com.arangodb.ArangoDB;
import com.linkedin.replica.wall.config.DatabaseConnection;
import com.linkedin.replica.wall.handlers.DatabaseHandler;
import com.linkedin.replica.wall.handlers.impl.ArangoWallHandler;
import com.linkedin.replica.wall.main.Wall;
import com.linkedin.replica.wall.models.Bookmark;
import com.linkedin.replica.wall.models.Post;
import com.linkedin.replica.wall.models.UserProfile;
import javafx.geometry.Pos;
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
    private String dbName;
    private String likesCollection;
    private String repliesCollection;
    private String commentsCollection;
    private String postsCollection;
    private String usersCollection;
    private ArrayList<UserProfile> insertedUsers;


    @BeforeClass
    public static void setup() throws ClassNotFoundException, IOException, SQLException{
        // startup SearchEngine
        String[] args = {"db_config", "src/main/resources/command_config"};
        Wall.start(args);

        dbSeed = new DatabaseSeed();
        arangoWallHandler = new ArangoWallHandler();
        arangoDB = DatabaseConnection.getInstance().getArangodb();

        dbSeed.insertUsers();
        dbSeed.insertPosts();
        dbSeed.insertReplies();
        dbSeed.insertLikes();
        dbSeed.insertComments();
    }

    @Test
    public void testAddBookmark() throws IOException, ClassNotFoundException {
        UserProfile user = dbSeed.getInsertedUsers().get(0);
        String userId = user.getUserId();
        String postId = "P123";

        int bookmarkNo = user.getBookmarks().size() + 1;
        Bookmark bookmark = new Bookmark(userId, postId);
        arangoWallHandler.addBookmark(bookmark);

        UserProfile retrievedUser = arangoDB.db(dbName).collection(usersCollection).getDocument(user.getUserId(), UserProfile.class);
        ArrayList<Bookmark> updatedBookmarks = retrievedUser.getBookmarks();
        Bookmark retrievedBookmark = updatedBookmarks.get(updatedBookmarks.size() - 1);
        assertEquals("size of bookmarks should increased by one", updatedBookmarks.size() , bookmarkNo);
        assertEquals("userID should be the same in the inserted bookmark", retrievedBookmark.getUserId(), bookmark.getUserId());
        assertEquals("postID should be the same in the inserted bookmark", retrievedBookmark.getPostId(), bookmark.getPostId());
    }

//    @Test
//    public void testDeleteBookmark(){
//        UserProfile user = dbSeed.getInsertedUsers().get(0);
//        String userId = user.getUserId();
//        String postId = "P123";
//
//
//
//    }

 //   @Test
//    public void testSearchUsers() throws FileNotFoundException, ClassNotFoundException, IOException, SQLException{
//        String searchKey = "hm";
//        DatabaseHandler dbHandler = new ArangoWallHandler();
//        List<User> results = dbHandler.searchUsers(searchKey);
//
//        boolean check = false;
//        for(User user : results){
//            if(user.getFirstName().contains(searchKey))
//                check = true;
//
//            if(user.getLastName().contains(searchKey))
//                check = true;
//
//            assertEquals("Wrong Fetched User as his/her firstName and lastName does not match search key.", true, check);
//            check = false;
//        }
//    }
//
//
//    @Test
//    public void testSearchPosts() throws FileNotFoundException, ClassNotFoundException, IOException, SQLException{
//        String searchKey = "Lorem";
//        DatabaseHandler dbHandler = new ArangoWallHandler();
//        List<Post> results = dbHandler.searchPosts(searchKey);
//        searchKey = searchKey.toLowerCase();
//        boolean check = false;
//        for(Post post : results){
//
//            if(post.getText().toLowerCase().contains(searchKey))
//                check = true;
//
//            assertEquals("Wrong Fetched post as its text does not match search key.", true, check);
//            check = false;
//        }
//    }
//

    @AfterClass
    public static void tearDown() throws ArangoDBException, FileNotFoundException, ClassNotFoundException, IOException, SQLException{
        dbSeed.deleteAllUsers();
        dbSeed.deleteAllPosts();
        dbSeed.deleteAllReplies();
        dbSeed.deleteAllComments();
        dbSeed.deleteAllLikes();
        Wall.shutdown();
    }

}