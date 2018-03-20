package main;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeoutException;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.util.MapBuilder;
import com.linkedin.replica.wall.config.DatabaseConnection;
import com.linkedin.replica.wall.handlers.impl.ArangoWallHandler;
import com.linkedin.replica.wall.main.Main;
import com.linkedin.replica.wall.models.Comment;
import com.linkedin.replica.wall.services.WallService;
import databaseHandlers.DatabaseSeed;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.arangodb.ArangoDBException;
import org.junit.Test;


public class WallTest {
    private static DatabaseSeed dbSeed;
    private static WallService service;
    private static Properties properties;
    private static ArangoDB arangoDB;
    private static ArangoWallHandler arangoWallHandler;
    private static String dbName;
    private static String  likesCollection;
    private static String  commentsCollection;

    @BeforeClass
    public static void setup() throws ClassNotFoundException, IOException, SQLException, TimeoutException {
        // startup SearchEngine
        String[] args = {"db_config", "src/main/resources/command_config"};
        //Main.start(args);
        service = new WallService();
        arangoDB = DatabaseConnection.getInstance().getArangodb();
        arangoWallHandler = new ArangoWallHandler();
        properties = new Properties();
        properties.load(new FileInputStream("db_config"));
        dbName = properties.getProperty("arangodb.name");
        likesCollection = properties.getProperty("collections.likes.name");
        commentsCollection = properties.getProperty("collections.comments.name");
        dbSeed = new DatabaseSeed();
        dbSeed.insertUsers();
        dbSeed.insertPosts();


    }

    public Comment getComment(String commentId) {
        Comment comment = null;
        try {
            comment = arangoDB.db(dbName).collection(commentsCollection).getDocument(commentId,
                    Comment.class);
        } catch (ArangoDBException e) {
            System.err.println("Failed to get comment: commentId; " + e.getMessage());
        }
        return comment;
    }

    public List<Comment> getComments(String postId) {
        ArrayList<Comment> comments = new ArrayList<Comment>();
        try {
            String query = "FOR l IN " + commentsCollection + " FILTER l.parentPostId == @parentPostId RETURN l";
            Map<String, Object> bindVars = new MapBuilder().put("parentPostId", postId).get();
            ArangoCursor<Comment> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    Comment.class);
            cursor.forEachRemaining(commentDocument -> {
                comments.add(commentDocument);
                System.out.println("Key: " + commentDocument.getCommentId());
            });
        } catch (ArangoDBException e) {
            System.err.println("Failed to get posts' comments." + e.getMessage());
        }
        return comments;
    }

    @Test
    public void testAddComments() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        HashMap<String,String> request = new HashMap<String,String>();
        request.put("commentId", "1234");
        request.put("authorId", "12");
        request.put("parentPostId", "14");
        request.put("likesCount", 20+"");
        request.put("repliesCount", 2+"");
        request.put("images", "sdgg");
        request.put("urls", "fhdfhj");
        request.put("mentions", "sdgfh");
        request.put("text", "Comment Text");
        request.put("timeStamp", "Time Stamp");
        try {
            LinkedHashMap<String, Object> response = service.serve("add.comment", request);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<Comment> newComments = getComments("14");
        assertEquals("Only one comment should have the parentPostId", newComments.size(),1 );


    }

    @Test
    public void testEditComments() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        HashMap<String,String> request = new HashMap<String,String>();
        request.put("commentId", "1234");
        request.put("authorId", "12");
        request.put("parentPostId", "14");
        request.put("likesCount", 20+"");
        request.put("repliesCount", 2+"");
        request.put("images", "sdgg");
        request.put("urls", "fhdfhj");
        request.put("mentions", "sdgfh");
        request.put("text", "Text");
        request.put("timeStamp", "Time Stamp");
        try {
            LinkedHashMap<String, Object> response = service.serve("edit.comment", request);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<Comment> newComments = getComments("14");
        assertEquals("The comment should have a new Text", newComments.get(0).getText(),"Text");


    }

    @Test
    public void testDeleteComments() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        HashMap<String,String> request = new HashMap<String,String>();
        request.put("commentId", "1234");
        request.put("authorId", "12");
        request.put("parentPostId", "14");
        request.put("likesCount", 20+"");
        request.put("repliesCount", 2+"");
        request.put("images", "sdgg");
        request.put("urls", "fhdfhj");
        request.put("mentions", "sdgfh");
        request.put("text", "Text");
        request.put("timeStamp", "Time Stamp");
        try {
            LinkedHashMap<String, Object> response = service.serve("delete.comment", request);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Comment newComment = getComment("1234");
        assertEquals("The comment should not exist", newComment,null);


    }

    @Test
    public void testGetComments() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        HashMap<String,String> request = new HashMap<String,String>();
        request.put("parentPostId", "14");
        try {
            LinkedHashMap<String, Object> response = service.serve("get.comments", request);
            List<Comment> newComments = (List<Comment>) response.get("response");
            assertEquals("The comment should not exist", newComments.size(),1);
        } catch (ParseException e) {
            e.printStackTrace();
        }




    }


    @AfterClass
    public static void tearDown() throws ArangoDBException, ClassNotFoundException, IOException, SQLException{
        dbSeed.deleteAllUsers();
        dbSeed.deleteAllPosts();
//        Main.shutdown();
    }

}
