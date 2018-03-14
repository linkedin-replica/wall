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
import com.linkedin.replica.wall.handlers.impl.ArangoWallHandler;
import com.linkedin.replica.wall.main.Wall;
import com.linkedin.replica.wall.models.Comment;
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
    private static ArangoWallHandler arangoWallHandler;
    private static String dbName;
    private static String  likesCollection;
    private static String  commentsCollection;


    @BeforeClass
    public static void setup() throws ClassNotFoundException, IOException {
        // startup SearchEngine
        String[] args = {"db_config", "src/main/resources/command_config"};
        Wall.start(args);
        wallService = new WallService();
        arangoDB = DatabaseConnection.getInstance().getArangodb();
        arangoWallHandler = new ArangoWallHandler();
        properties = new Properties();
        properties.load(new FileInputStream("db_config"));
        dbName = properties.getProperty("arangodb.name");
        likesCollection = properties.getProperty("collections.likes.name");
        commentsCollection = properties.getProperty("collections.comments.name");

        dbSeed = new DatabaseSeed();
       // dbSeed.insertUsers();
       // dbSeed.insertPosts();
        //dbSeed.insertReplies();
        //dbSeed.insertLikes();
        //dbSeed.insertComments();
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

    @Test
    public void testAddComment(){
        Comment comment  = new Comment("commentID2","authorID","parentPostID",12,22,null,null,null,"comment Text","time Stamp");
        arangoWallHandler.addComment(comment);
        Comment newComment = getComment("commentID2");
        assertEquals("Expected to have a certain comment in database", newComment.getParentPostId(), "parentPostID");

    }

    @Test
    public void testEditComment(){
        Comment comment  = new Comment("commentID2","authorID","parentPostID2",12,22,null,null,null,"comment Text","time Stamp");
        arangoWallHandler.editComment(comment);
        Comment newComment = getComment("commentID2");
        assertEquals("Expected to edit a certain comment in database", newComment.getParentPostId(), "parentPostID2");
    }

    @Test
    public void testDeleteComment(){
        Comment comment  = new Comment("commentID2","authorID","parentPostID2",12,22,null,null,null,"comment Text","time Stamp");
        arangoWallHandler.deleteComment(comment);
        Comment newComment = getComment("commentID2");
        assertEquals("Expected to not have that comment", newComment, null);

    }

/*    @Test
    public void testGetPostLikes() throws ClassNotFoundException, IllegalAccessException, ParseException, InstantiationException {
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

            assertEquals("Incorrect like retrieved as the likedPostId does not match the postId.", true, check);
            check = false;
        }
    }

    @Test
    public void testGetCommentLikes() throws ClassNotFoundException, IllegalAccessException, ParseException, InstantiationException {
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

            assertEquals("Incorrect like retrieved as the likedCommentId does not match the commentId.", true, check);
            check = false;
        }
    }

    @Test
    public void testGetReplyLikes() throws ClassNotFoundException, IllegalAccessException, ParseException, InstantiationException {
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

            assertEquals("Incorrect like retrieved as the likedReplyId does not match the replyId.", true, check);
            check = false;
        }
    }

    @Test
    public void testAddLikes() throws ClassNotFoundException, InstantiationException, ParseException, IllegalAccessException {
        HashMap<String,String> request = new HashMap<String,String>();
        request.put("likerId", "100");
        request.put("likedPostId", "99");
        request.put("likedCommentId", null);
        request.put("likedReplyId", null);
        request.put("userName", "Yara");
        request.put("headLine", "Yara and 5 others");
        request.put("imageUrl", "urlX");
        LinkedHashMap<String, Object> response = wallService.serve("addLike", request);
        String [] message = ((String) response.get("response")).split(",");
        String query = "FOR l IN " + likesCollection + " FILTER l.likedPostId == @postId  && l.likerId == @likerId RETURN l";
        Map<String, Object> bindVars = new MapBuilder().put("postId", "99").get();
        bindVars.put("likerId", "100");
        ArangoCursor<Like> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                Like.class);
        List<Like> retrievedLikes = new ArrayList<Like>();
        while (cursor.hasNext())
            retrievedLikes.add(cursor.next());
        System.out.println(retrievedLikes.size() + retrievedLikes.get(0).toString());
        assertEquals("Only one like should have the likerId and postId", 1, retrievedLikes.size());


    }

    @Test
    public void testDeleteLikes() throws ClassNotFoundException, InstantiationException, ParseException, IllegalAccessException {
//

    }

    @Test
    public void testAddComments() throws ClassNotFoundException, InstantiationException, ParseException, IllegalAccessException {
        HashMap<String,String> request = new HashMap<String,String>();
        request.put("authorId", "12");
        request.put("parentPostId", "14");
        request.put("likesCount", 20+"");
        request.put("repliesCount", 2+"");
        request.put("images", null);
        request.put("urls", null);
        request.put("mentions", null);
        request.put("text", "Comment Text");
        request.put("timeStamp", "Time Stamp");
        LinkedHashMap<String, Object> response = wallService.serve("addComment", request);
        String query = "FOR l IN " + commentsCollection + " FILTER l.parentPostId == @parentPostId  && l.authorId == @authorId RETURN l";
        Map<String, Object> bindVars = new MapBuilder().put("authorId", "12").get();
        bindVars.put("parentPostId", "14");
        ArangoCursor<Comment> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                Comment.class);
        List<Comment> retrievedComments = new ArrayList<Comment>();
        while (cursor.hasNext())
            retrievedComments.add(cursor.next());
        System.out.println(retrievedComments.size() + retrievedComments.get(0).toString());
        assertEquals("Only one comment should have the authorId and parentPostId", 1, retrievedComments.size());


    }

    @Test
    public void testGetComments() throws ClassNotFoundException, IllegalAccessException, ParseException, InstantiationException {
        String parentPostId = "2";
        HashMap<String,String> request = new HashMap<String,String>();
        request.put("parentPostId", parentPostId);
        LinkedHashMap<String, Object> response = wallService.serve("getComments", request);
        List<Comment> gottenComments = (List<Comment>) response.get("response");
        System.out.println(gottenComments.get(0).toString());
        System.out.println(gottenComments.size());
        boolean check = false;
        for(Comment comment : gottenComments){
            if(comment.getParentPostId().equals(parentPostId))
                check = true;

            assertEquals("Incorrect like retrieved as the likedReplyId does not match the replyId.", true, check);
            check = false;
        }
    }


    @AfterClass
    public static void tearDown() throws ArangoDBException, ClassNotFoundException, IOException, SQLException{
        dbSeed.deleteAllUsers();
        dbSeed.deleteAllPosts();
        dbSeed.deleteAllReplies();
        dbSeed.deleteAllComments();
        dbSeed.deleteAllLikes();
        Wall.shutdown();
    }*/

}