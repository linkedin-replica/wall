package databaseHandlers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.arangodb.entity.DocumentCreateEntity;
import com.linkedin.replica.wall.handlers.DatabaseHandler;
import com.linkedin.replica.wall.handlers.impl.ArangoWallHandler;
import com.linkedin.replica.wall.main.Wall;
import com.linkedin.replica.wall.models.Like;
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


    @BeforeClass
    public static void setup() throws ClassNotFoundException, IOException {
        // startup SearchEngine
        String[] args = {"db_config", "src/main/resources/command_config"};
        Wall.start(args);
        wallService = new WallService();

        dbSeed = new DatabaseSeed();
        //dbSeed.insertUsers();
        //dbSeed.insertPosts();
        //dbSeed.insertReplies();
        dbSeed.insertLikes();
        //dbSeed.insertComments();
    }
//
    @Test
    public void testGetPostLikes() throws ClassNotFoundException, IOException, IllegalAccessException, ParseException, InstantiationException {
        String postId = "15";
        HashMap<String,String> request = new HashMap<String,String>();
        request.put("likedPostId", postId);
        LinkedHashMap<String, Object> response = wallService.serve("getPostLikes", request);
        List<Like> postLikes = (List<Like>) response.get("response");
        System.out.println(postLikes.size());
        boolean check = false;
        for(Like like : postLikes){
            if(like.getLikedPostId().equals(postId))
                check = true;

            assertEquals("Wrong Fetched Like as the likedPostId does not match the postId.", true, check);
            check = false;
        }
    }

    @Test
    public void testGetCommentLikes() throws ClassNotFoundException, IOException, IllegalAccessException, ParseException, InstantiationException {
        String commentId = "16";
        HashMap<String,String> request = new HashMap<String,String>();
        request.put("likedCommentId", commentId);
        LinkedHashMap<String, Object> response = wallService.serve("getCommentLikes", request);
        List<Like> commentLikes = (List<Like>) response.get("response");
        System.out.println(commentLikes.get(0).toString());
        System.out.println(commentLikes.size());
        boolean check = false;
        for(Like like : commentLikes){
            if(like.getLikedCommentId().equals(commentId))
                check = true;

            assertEquals("Wrong Fetched Like as the likedCommentId does not match the commentId.", true, check);
            check = false;
        }
    }

    @Test
    public void testGetReplyLikes() throws ClassNotFoundException, IOException, IllegalAccessException, ParseException, InstantiationException {
        String replyId = "18";
        HashMap<String,String> request = new HashMap<String,String>();
        request.put("likedReplyId", replyId);
        LinkedHashMap<String, Object> response = wallService.serve("getReplyLikes", request);
        List<Like> replyLikes = (List<Like>) response.get("response");
        System.out.println(replyLikes.get(0).toString());
        System.out.println(replyLikes.size());
        boolean check = false;
        for(Like like : replyLikes){
            if(like.getLikedReplyId().equals(replyId))
                check = true;

            assertEquals("Wrong Fetched Like as the likedReplyId does not match the replyId.", true, check);
            check = false;
        }
    }

    @AfterClass
    public static void tearDown() throws ArangoDBException, ClassNotFoundException, IOException, SQLException{
        //dbSeed.deleteAllUsers();
        //dbSeed.deleteAllPosts();
        //dbSeed.deleteAllReplies();
        //dbSeed.deleteAllComments();
        dbSeed.deleteAllLikes();
        Wall.shutdown();
    }

}