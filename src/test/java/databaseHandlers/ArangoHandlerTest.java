package databaseHandlers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.linkedin.replica.wall.handlers.DatabaseHandler;
import com.linkedin.replica.wall.handlers.impl.ArangoWallHandler;
import com.linkedin.replica.wall.main.Wall;
import com.linkedin.replica.wall.models.Like;
import com.linkedin.replica.wall.models.Post;
import javafx.geometry.Pos;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.arangodb.ArangoDBException;

import static org.junit.Assert.assertEquals;

public class ArangoHandlerTest {
    private static DatabaseSeed dbSeed;

    @BeforeClass
    public static void setup() throws ClassNotFoundException, IOException, SQLException{
        // startup SearchEngine
        String[] args = {"db_config", "src/main/resources/command_config"};
        Wall.start(args);

        dbSeed = new DatabaseSeed();
        dbSeed.insertUsers();
        dbSeed.insertPosts();
        dbSeed.insertReplies();
        dbSeed.insertLikes();
        dbSeed.insertComments();
    }

    @Test
    public void testGetPostLikes() throws FileNotFoundException, ClassNotFoundException, IOException {
        String postId = "1";
        DatabaseHandler dbHandler = new ArangoWallHandler();
        List<Like> likesResponse = dbHandler.getPostLikes(postId);

        boolean check = false;
        for(Like like : likesResponse){
            if(like.getLikedPostId().equals(postId))
                check = true;

            assertEquals("Wrong Fetched Like as the likedPostId does not match the postId.", true, check);
            check = false;
        }
    }

    @Test
    public void testGetCommentLikes() throws FileNotFoundException, ClassNotFoundException, IOException {
        String commentId = "45";
        DatabaseHandler dbHandler = new ArangoWallHandler();
        List<Like> likesResponse = dbHandler.getCommentLikes(commentId);

        boolean check = false;
        for(Like like : likesResponse){
            if(like.getLikedCommentId().equals(commentId))
                check = true;

            assertEquals("Wrong Fetched Like as the likedCommentId does not match the commentId.", true, check);
            check = false;
        }
    }

    @Test
    public void testGetReplyLikes() throws FileNotFoundException, ClassNotFoundException, IOException {
        String replyId = "8";
        DatabaseHandler dbHandler = new ArangoWallHandler();
        List<Like> likesResponse = dbHandler.getReplyLikes(replyId);

        boolean check = false;
        for(Like like : likesResponse){
            if(like.getLikedCommentId().equals(replyId))
                check = true;

            assertEquals("Wrong Fetched Like as the likedReplyId does not match the replyId.", true, check);
            check = false;
        }
    }

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