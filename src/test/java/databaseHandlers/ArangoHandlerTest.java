package databaseHandlers;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

import com.arangodb.ArangoDB;
import com.linkedin.replica.wall.config.Configuration;
import com.linkedin.replica.wall.database.DatabaseConnection;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.database.handlers.impl.ArangoWallHandler;
import com.linkedin.replica.wall.models.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.arangodb.ArangoDBException;

import static org.junit.Assert.assertEquals;

public class ArangoHandlerTest {
    private  DatabaseSeed dbSeed;
    private static WallHandler arangoWallHandler;
    private static ArangoDB arangoDB;
    static Configuration config;
    private static String dbName;
    private static String  commentsCollection;
    private static String usersCollection;
    private static String  repliesCollection;
    private static String postsCollection;
    private static Post insertedPost;
    private static Comment insertedComment;
    private static Reply insertedReply;
    private static UserProfile insertedUser;

    @BeforeClass
    public static void setup() throws ClassNotFoundException, IOException, ParseException {
        // startup SearchEngine
		String rootFolder = "src/main/resources/";
		Configuration.init(rootFolder + "app.config", rootFolder + "arango.test.config",
				rootFolder + "commands.config", rootFolder + "controller.config", rootFolder + "cache.config");
		config = Configuration.getInstance();
		DatabaseConnection.init();
        arangoDB = DatabaseConnection.getInstance().getArangodb();
        arangoWallHandler = new ArangoWallHandler();
        dbName = Configuration.getInstance().getArangoConfig("arangodb.name");
        usersCollection = Configuration.getInstance().getArangoConfig("collections.users.name");
        commentsCollection = Configuration.getInstance().getArangoConfig("collections.comments.name");
        repliesCollection = Configuration.getInstance().getArangoConfig("collections.replies.name");
        postsCollection = Configuration.getInstance().getArangoConfig("collections.posts.name");
    }

    @Before
    public void startup() throws ClassNotFoundException, IOException, ParseException{
        dbSeed = new DatabaseSeed();
        dbSeed.insertPosts();
        dbSeed.insertComments();
        dbSeed.insertReplies();
        dbSeed.insertUsers();

        insertedComment = dbSeed.getInsertedComments().get(0);
        insertedPost = dbSeed.getInsertedPosts().get(0);
        insertedReply = dbSeed.getInsertedReplies().get(0);
        insertedUser = dbSeed.getInsertedUsers().get(0);
    }
    
    @Test
    public void testAddLikeToPost(){
        String postId = dbSeed.getInsertedPosts().get(0).getPostId();
        String userId = dbSeed.getInsertedUsers().get(0).getUserId();

        arangoWallHandler.addLikeToPost(userId,postId);

        Post postAfterEdit = getPost(postId);
        assertEquals("Expected to have 1 liker" , 1, postAfterEdit.getLikers().size());

    }
    @Test
    public void testAddLikeToComment(){
        String commentId = dbSeed.getInsertedComments().get(0).getCommentId();
        String userId = dbSeed.getInsertedUsers().get(0).getUserId();

        arangoWallHandler.addLikeToComment(userId,commentId);
        Comment commentAfterAdd = getComment(commentId);
        assertEquals("Expected to have 1 liker" , 1, commentAfterAdd.getLikers().size());

    }

    @Test
    public void testAddLikeToReply(){
        String replyId = dbSeed.getInsertedReplies().get(0).getReplyId();
        String userId = dbSeed.getInsertedUsers().get(0).getUserId();

        arangoWallHandler.addLikeToReply(userId,replyId);
        Reply replyAfterAdd = getReply(replyId);
        assertEquals("Expected to have 1 liker" , 1, replyAfterAdd.getLikers().size());

    }

    @Test
    public void deleteLikeFromPost(){
        String postId = dbSeed.getInsertedPosts().get(0).getPostId();
        String userId1 = dbSeed.getInsertedUsers().get(0).getUserId();
        String userId2 = dbSeed.getInsertedUsers().get(1).getUserId();

        arangoWallHandler.addLikeToPost(userId1, postId);
        arangoWallHandler.addLikeToPost(userId2, postId);
        arangoWallHandler.deleteLikeFromPost(userId1,postId);

        Post post = getPost(postId);
        assertEquals("Expected to have 1 liker" , 1, post.getLikers().size());


    }

    @Test
    public void deleteLikeFromComment(){
        String commentId = dbSeed.getInsertedComments().get(0).getCommentId();
        String userId1 = dbSeed.getInsertedUsers().get(0).getUserId();
        String userId2 = dbSeed.getInsertedUsers().get(1).getUserId();

        arangoWallHandler.addLikeToComment(userId1, commentId);
        arangoWallHandler.addLikeToComment(userId2, commentId);

        arangoWallHandler.deleteLikeFromComment(userId1,commentId);

        Comment comment = getComment(commentId);
        assertEquals("Expected to have 1 liker" , 1, comment.getLikers().size());


    }

    @Test
    public void deleteLikeFromReply(){
        String replyId = dbSeed.getInsertedReplies().get(0).getReplyId();
        String userId1 = dbSeed.getInsertedUsers().get(0).getUserId();
        String userId2 = dbSeed.getInsertedUsers().get(1).getUserId();

        arangoWallHandler.addLikeToReply(userId1, replyId);
        arangoWallHandler.addLikeToReply(userId2, replyId);

        arangoWallHandler.deleteLikeFromReply(userId1,replyId);

        Reply reply = getReply(replyId);
        assertEquals("Expected to have 1 liker" , 1, reply.getLikers().size());


    }

    /**
     * adding post in database.
     * @param post
     * @return
     */
    public String addPost(Post post) {
        String response = "";
        try {
            arangoDB.db(dbName).collection(postsCollection).insertDocument(post);
            response = "Post Created";
        }catch (ArangoDBException e){
            response = "Failed to add Post " + e.getMessage();
        }

        return response;
    }


    /**
     * return specific post from database.
     * @param postId
     * @return
     */
   public Post getPost(String postId){
       Post post = insertedPost;
       try {
           post = arangoDB.db(dbName).collection(postsCollection).getDocument(postId, Post.class);

       } catch (Exception e) {
           e.printStackTrace();
       }

        return post;
   }


    /**
     * Test to add post.
     * @throws ParseException
     */
   @Test
   public void testAddPost() throws ParseException {
       ArrayList<String> images = new ArrayList<String>();
       images.add("images");
       ArrayList<String> videos = new ArrayList<String>();
       videos.add("videos");
       Post post = new Post();
       post.setArticle(false);
       post.setAuthorId(insertedUser.getUserId());
       post.setImages(images);
       post.setVideos(videos);
       post.setText("Test Add");
       post.setTimestamp(System.currentTimeMillis());

       arangoWallHandler.addPost(post);
       Post newPost = getPost(post.getPostId());
       assertEquals("Expected to have a certain post in database", "Test Add", newPost.getText());

   }

    /**
     * test of editing post arango handler.
     * @throws ParseException
     */
   @Test
   public void testEditPost() throws ParseException {

       Post post = insertedPost;
       HashMap<String, Object> editArgs = new HashMap<String, Object>();
       editArgs.put("postId", post.getPostId());
       editArgs.put("text", "Yara discovered the bug :D");
       arangoWallHandler.editPost(editArgs);
       Post newPost = getPost(insertedPost.getPostId());
       assertEquals("Expected to have a certain post in database","Yara discovered the bug :D", newPost.getText());

   }

    /**
     * function to test delete post arango function.
     * @throws ParseException
     */
   @Test
   public void testDeletePost() throws ParseException {

       arangoWallHandler.deletePost(insertedPost.getPostId());
       Post newPost = getPost(insertedPost.getPostId());
       assertEquals("Expected to have a certain post in database" , null, newPost);
   }

    /**
     * testing getPosts arango function.
     * @throws ParseException
     */
    @Test
    public void testGetPosts() throws ParseException {
        ArrayList<String> images = new ArrayList<String>();
        images.add("images");
        ArrayList<String> videos = new ArrayList<String>();
        videos.add("videos");
        ArrayList<String> urls = new ArrayList<String>();

        Post post = new Post();
        post.setArticle(false);
        post.setAuthorId(insertedUser.getUserId());
        post.setCommentsCount(12);
        post.setImages(images);
        post.setVideos(videos);
        post.setText("Text");

        addPost(post);
        List<Post> newPost = arangoWallHandler.getPosts(insertedUser.getUserId());
        assertEquals("Expected to have 1 post with that post ID", 1, newPost.size());
    }

    /**
     * function to get specific reply from database.
     * @param replyId
     * @return
     */
    public Reply getReply(String replyId) {
        Reply reply = null;
        try {
            reply = arangoDB.db(dbName).collection(repliesCollection).getDocument(replyId,
                    Reply.class);
        } catch (ArangoDBException e) {
            e.printStackTrace();
        }
        return reply;
    }

    /**
     * @throws ParseException
     */
    @Test
    public void testAddReply() throws ParseException{
        ArrayList<String> mentionsImagesUrls = new ArrayList<String>();
        mentionsImagesUrls.add("Test");
        Reply reply = new Reply();
        reply.setReplyId(UUID.randomUUID().toString());
        reply.setAuthorId(insertedUser.getUserId());
        reply.setParentPostId(insertedPost.getPostId());
        reply.setParentCommentId(insertedComment.getCommentId());
        reply.setLikesCount(2000);
        reply.setText("Test Add Reply");
        reply.setTimestamp(System.currentTimeMillis());
        arangoWallHandler.addReply(reply);
        Reply replyDocument = arangoDB.db(dbName).collection(repliesCollection).getDocument(reply.getReplyId(),Reply.class);
        assertEquals("Reply text should be", "Test Add Reply", replyDocument.getText());
    }

    @Test
    public void testDeleteReply() throws ParseException {
        arangoWallHandler.deleteReply(insertedReply.getReplyId());
        Reply newReply = getReply(insertedReply.getReplyId());
        assertEquals("Expected to not have that comment",null, newReply);
    }

    /**
     * test edit reply function.
     * @throws ParseException
     */
    @Test
    public void testEditReply() throws ParseException {

        String replyID = insertedReply.getReplyId();
        Reply reply = insertedReply;
        reply.setText("Some edited text");
        HashMap<String, Object> editArgs = new HashMap<String, Object>();
        editArgs.put("replyId", replyID);
        editArgs.put("text",reply.getText());
        arangoWallHandler.editReply(editArgs);
        Reply testReply = arangoWallHandler.getReply(replyID);
        assertEquals("Texts should be the same", "Some edited text", testReply.getText());
    }


    /**
     * testing Adding bookmark function
     */
    @Test
    public void testAddBookmark() {
        UserProfile user = insertedUser;
        String userId = user.getUserId();
        String postId = insertedPost.getPostId();
        int bookmarkNo = user.getBookmarks().size() + 1;
        arangoWallHandler.addBookmark(userId, postId);
        UserProfile retrievedUser = arangoDB.db(dbName).collection(usersCollection).getDocument(userId, UserProfile.class);
        ArrayList<String> updatedBookmarks = retrievedUser.getBookmarks();
        String retrievedBookmark = updatedBookmarks.get(updatedBookmarks.size() - 1);
        assertEquals("size of bookmarks should have increased by one", updatedBookmarks.size() , bookmarkNo);
        assertEquals("userID should be the same in the inserted bookmark", postId, retrievedBookmark);
    }

    /**
     * test DeleteBookmark function
     */
    @Test
    public void testDeleteBookmark(){
        UserProfile user = insertedUser;
        String userId = user.getUserId();
        int bookmarkNo = user.getBookmarks().size();
        String postId = insertedUser.getBookmarks().get(0);
        arangoWallHandler.deleteBookmark(userId, postId);
        UserProfile retrievedUser = arangoDB.db(dbName).collection(usersCollection).getDocument(userId, UserProfile.class);
        ArrayList<String> updatedBookmarks = retrievedUser.getBookmarks();
        assertEquals("size of bookmarks should decreased by one", bookmarkNo - 1, updatedBookmarks.size());
    }

    /**
     * testing getBookmarks arango function
     */
    @Test
    public void testGetBookmarks(){
        UserProfile user = insertedUser;
        String userId = user.getUserId();
        ArrayList<String> bookmarks = user.getBookmarks();
        ArrayList<String> retrievedBookmarks = arangoWallHandler.getBookmarks(userId);
        assertEquals("size of bookmarks arrays should be equal", bookmarks.size() , retrievedBookmarks.size());
        boolean check = true;
        for (int i = 0; i < bookmarks.size(); i++){
            if(!bookmarks.get(i).equals(retrievedBookmarks.get(i))){
                check = false;
            }
        }

        assertEquals("bookmarks arrays should be identical", true , check);


    }

    /**
     * function to get specific comment from database.
     * @param commentId
     * @return
     */
    public Comment getComment(String commentId) {
        Comment comment = null;
        try {
            comment = arangoDB.db(dbName).collection(commentsCollection).getDocument(commentId,
                    Comment.class);
        } catch (ArangoDBException e) {
            e.printStackTrace();
        }
        return comment;
    }

    /**
     * testing add comment arango handler function.
     */
    @Test
    public void testAddComment(){
        Comment comment = new Comment();
        comment.setAuthorId(insertedUser.getUserId());
        comment.setParentPostId(insertedPost.getPostId());
        comment.setCommentId(UUID.randomUUID().toString());
        comment.setLikesCount(12);
        comment.setRepliesCount(22);
        comment.setText("Comment Test");
        comment.setTimestamp(System.currentTimeMillis());
        arangoWallHandler.addComment(comment);
        Comment newComment = getComment(comment.getCommentId());
        assertEquals("Expected to have a certain comment in database",insertedPost.getPostId(), newComment.getParentPostId());

    }

    /**
     * testing editing comment arango handler function.
     */
    @Test
    public void testEditComment(){
        Comment updatedComment  = insertedComment;
        updatedComment.setParentPostId(dbSeed.getInsertedPosts().get(1).getPostId());
        HashMap<String, Object> editCommentArgs = new HashMap<String, Object>();
        editCommentArgs.put("commentId", updatedComment.getCommentId());
        editCommentArgs.put("text", "test edit");
        arangoWallHandler.editComment(editCommentArgs);
        Comment newComment = getComment(insertedComment.getCommentId());
        assertEquals("Expected to edit a certain comment in database","test edit", newComment.getText());
    }

    /**
     * testing deleting comment arango handler function.
     */
    @Test
    public void testDeleteComment(){
        arangoWallHandler.deleteComment(insertedComment.getCommentId());
        Comment newComment = getComment(insertedComment.getCommentId());
        assertEquals("Expected to not have that comment", null, newComment);

    }

    /**
     * test get comments.
     */
    @Test
    public void testGetComments(){
        List<Comment> newComments = arangoWallHandler.getComments(insertedComment.getParentPostId());
        assertEquals("Expected to have 1 comment with that post ID", 10, newComments.size());

    }

    /**
     * test get comment.
     */
    @Test
    public void testGetComment() {
        Comment newComment = getComment(insertedComment.getCommentId());
        assertEquals("Expected to get a certain comment", insertedComment.getAuthorId(), newComment.getAuthorId());

    }
 
    @After
    public void tearDown() throws ArangoDBException, ClassNotFoundException, IOException {
        dbSeed.deleteAllUsers();
        dbSeed.deleteAllPosts();
        dbSeed.deleteAllReplies();
        dbSeed.deleteAllComments();
        DatabaseConnection.getInstance().closeConnections();
      }

}