package databaseHandlers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.entity.DocumentCreateEntity;
import com.linkedin.replica.wall.config.DatabaseConnection;
import com.linkedin.replica.wall.handlers.DatabaseHandler;
import com.linkedin.replica.wall.handlers.impl.ArangoWallHandler;
import com.linkedin.replica.wall.main.Wall;
import com.linkedin.replica.wall.models.Post;
import com.linkedin.replica.wall.services.WallService;
import javafx.geometry.Pos;
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
    private static ArangoWallHandler arangoWallHandler;
    private static String dbName;
    private static String postsCollection;

    @BeforeClass
    public static void setup() throws ClassNotFoundException, IOException, SQLException{
        // startup SearchEngine
        String[] args = {"db_config", "src/main/resources/command_config"};
        Wall.start(args);

        WallService wallService = new WallService();
        arangoDB = DatabaseConnection.getInstance().getArangodb();
        arangoWallHandler = new ArangoWallHandler();
        Properties properties = new Properties();
        properties.load(new FileInputStream("db_config"));
        dbName = properties.getProperty("arangodb.name");
        postsCollection = properties.getProperty("collections.posts.name");

        dbSeed = new DatabaseSeed();
//        dbSeed.insertUsers();
        //dbSeed.deleteAllPosts();
//        dbSeed.insertPosts();
//        dbSeed.insertReplies();
//        dbSeed.insertLikes();
//        dbSeed.insertComments();



    }


    public String addPost(Post post) {
        String response = "";
        try {
            DocumentCreateEntity addDoc =  arangoDB.db(dbName).collection(postsCollection).insertDocument(post);
            System.out.println("Post Created");
            response = "Post Created";
        }catch (ArangoDBException e){
            System.err.println("Failed to add Post " + e.getMessage());
            response = "Failed to add Post " + e.getMessage();
        }

        return response;
    }



   public Post getPosts(String userID){

        Post post = null;
       try {

           post = arangoDB.db(dbName).collection(postsCollection).getDocument(userID, Post.class);

       } catch (ArangoDBException e) {

           System.err.println("Failed to get post: postId; " + e.getMessage());
       }

        return post;
   }

   @Test
   public void testAddPost(){

        Post post = new Post("postId", "authorId", null, "companyId", null, null,
                null,null, 12, "images", "videos", "urls", 30,
                "shares", "timestamp", true, false);
        arangoWallHandler.addPost(post);
        Post newPost = getPosts("postId");
        assertEquals("Expected to have a certain post in database", newPost.getCompanyId(), "companyId");

   }

   @Test
   public void testEditPost(){

       Post post = new Post("postId", "authorId", null, "companyId", null, null,
               null,null, 13, "images", "videos", "urls", 30,
               "shares", "timestamp", true, false);
       arangoWallHandler.editPost(post);
       Post newPost = getPosts("postId");
       assertEquals("Expected to have a certain post in database", newPost.getLikesCount(), 13);

   }

   @Test
   public void testDeletePost(){

       Post post = new Post("postId", "authorId", null, "companyId", null, null,
               null,null, 12, "images", "videos", "urls", 30,
               "shares", "timestamp", true, false);
       arangoWallHandler.deletePost(post);
       Post newPost = getPosts("postId");
       assertEquals("Expected to have a certain post in database", newPost, null);
   }


    @Test
    public void testGetPosts(){
        Post post = new Post("postId", "232", null, "companyId", null, null,
                null,null, 12, "images", "videos", "urls", 30,
                "shares", "timestamp", true, false);
        addPost(post);
        List<Post> newPost = arangoWallHandler.getPosts("232");
        assertEquals("Expected to have 1 post with that post ID", newPost.size(), 1);

    }

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
//        dbSeed.deleteAllUsers();
//        dbSeed.deleteAllPosts();
//        dbSeed.deleteAllReplies();
//        dbSeed.deleteAllComments();
//        dbSeed.deleteAllLikes();
//        Wall.shutdown();
   }



}