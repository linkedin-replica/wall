package databaseHandlers;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.linkedin.replica.wall.config.Configuration;
import com.linkedin.replica.wall.database.DatabaseConnection;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.database.handlers.impl.ArangoWallHandler;
import com.linkedin.replica.wall.models.Like;
import com.linkedin.replica.wall.models.Bookmark;
import com.linkedin.replica.wall.models.UserProfile;
import com.linkedin.replica.wall.models.Comment;
import com.linkedin.replica.wall.models.Reply;

import java.util.List;


import com.arangodb.entity.DocumentCreateEntity;

import com.linkedin.replica.wall.models.Post;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.arangodb.ArangoDBException;

import static org.junit.Assert.assertEquals;

public class ArangoHandlerTest {
    private static DatabaseSeed dbSeed;
    private static WallHandler arangoWallHandler;
    private static ArangoDB arangoDB;
    static Configuration config;
    private static String dbName;
    private static String  commentsCollection;
    private static String likesCollection;
    private static String usersCollection;
    private static String  repliesCollection;
    private static String postsCollection;
    private static Post insertedPost;
    private static Comment insertedComment;
    private static Reply insertedReply;
    private static UserProfile insertedUser;
    private static Like insertedLike;
    @BeforeClass
    public static void setup() throws ClassNotFoundException, IOException, ParseException {
        // startup SearchEngine
        String rootFolder = "src/main/resources/";
        Configuration.init(rootFolder + "app.config",
                rootFolder + "arango.test.config",
                rootFolder + "commands.config", rootFolder + "controller.config");
        config = Configuration.getInstance();
        DatabaseConnection.init();
        arangoDB = DatabaseConnection.getInstance().getArangodb();
        arangoWallHandler = new ArangoWallHandler();
        dbName = Configuration.getInstance().getArangoConfig("arangodb.name");
        likesCollection = Configuration.getInstance().getArangoConfig("collections.likes.name");
        usersCollection = Configuration.getInstance().getArangoConfig("collections.users.name");
        commentsCollection = Configuration.getInstance().getArangoConfig("collections.comments.name");
        repliesCollection = Configuration.getInstance().getArangoConfig("collections.replies.name");
        postsCollection = Configuration.getInstance().getArangoConfig("collections.posts.name");

        dbSeed = new DatabaseSeed();
        dbSeed.insertPosts();
        dbSeed.insertComments();
        dbSeed.insertReplies();
        dbSeed.insertLikes();
        dbSeed.insertUsers();

        insertedComment = dbSeed.getInsertedComments().get(0);
        insertedLike = dbSeed.getInsertedLikes().get(0);
        insertedPost = dbSeed.getInsertedPosts().get(0);
        insertedReply = dbSeed.getInsertedReplies().get(0);
        insertedUser = dbSeed.getInsertedUsers().get(0);
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
   public Post getPosts(String postId){

        Post post = null;
       try {

           post = arangoDB.db(dbName).collection(postsCollection).getDocument(postId, Post.class);

       } catch (ArangoDBException e) {

           System.err.println("Failed to get post: postId; " + e.getMessage());
       }

        return post;
   }

    /**
     * Test to add post.
     * @throws ParseException
     */
   @Test
   public void testAddPost() throws ParseException {

       DateFormat format = new SimpleDateFormat("EEE MMM dd yyyy hh:mm a", Locale.ENGLISH);
       Date timestamp = format.parse("Thu Jan 19 2012 01:00 PM");

       ArrayList<String> images = new ArrayList<String>();
       images.add("images");
       ArrayList<String> videos = new ArrayList<String>();
       videos.add("videos");
       ArrayList<String> urls = new ArrayList<String>();
       urls.add("urls");
       ArrayList<String> hashtags = new ArrayList<String>();
       hashtags.add("hashtags");
       ArrayList<String> mentions = new ArrayList<String>();
       mentions.add("mentions");

        Post post = new Post(insertedUser.getUserId(), null, "companyId", null, null,
                hashtags,mentions, 12, images, videos, urls, 30, timestamp, true, false);
        arangoWallHandler.addPost(post);
        Post newPost = getPosts(post.getPostId());
        assertEquals("Expected to have a certain post in database", newPost.getCompanyId(), "companyId");

   }

    /**
     * test of editing post arango handler.
     * @throws ParseException
     */
   @Test
   public void testEditPost() throws ParseException {

       DateFormat format = new SimpleDateFormat("EEE MMM dd yyyy hh:mm a", Locale.ENGLISH);
       Date timestamp = format.parse("Thu Jan 19 2012 01:00 PM");
       Post post = insertedPost;
       post.setLikesCount(13);
       arangoWallHandler.editPost(post);
       Post newPost = getPosts(insertedPost.getPostId());
       assertEquals("Expected to have a certain post in database", newPost.getLikesCount(), 13);

   }

    /**
     * function to test delete post arango function.
     * @throws ParseException
     */
   @Test
   public void testDeletePost() throws ParseException {

       DateFormat format = new SimpleDateFormat("EEE MMM dd yyyy hh:mm a", Locale.ENGLISH);
       Date timestamp = format.parse("Thu Jan 19 2012 01:00 PM");
       arangoWallHandler.deletePost(insertedPost);
       Post newPost = getPosts(insertedPost.getPostId());
       assertEquals("Expected to have a certain post in database", newPost, null);
   }

    /**
     * testing getPosts arango function.
     * @throws ParseException
     */
    @Test
    public void testGetPosts() throws ParseException {
        DateFormat format = new SimpleDateFormat("EEE MMM dd yyyy hh:mm a", Locale.ENGLISH);
        Date timestamp = format.parse("Thu Jan 19 2012 01:00 PM");

        ArrayList<String> images = new ArrayList<String>();
        images.add("images");
        ArrayList<String> videos = new ArrayList<String>();
        videos.add("videos");
        ArrayList<String> urls = new ArrayList<String>();
        urls.add("urls");
        ArrayList<String> hashtags = new ArrayList<String>();
        hashtags.add("hashtags");
        ArrayList<String> mentions = new ArrayList<String>();
        mentions.add("mentions");

        Post post = new Post(insertedUser.getUserId(), null, "companyId", null, null,
                hashtags,mentions, 13, images, videos, urls, 30, timestamp, true, false);

        addPost(post);
        List<Post> newPost = arangoWallHandler.getPosts(insertedUser.getUserId());
        assertEquals("Expected to have 1 post with that post ID", newPost.size(), 1);
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
            System.err.println("Failed to get reply: replyId; " + e.getMessage());
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
        DateFormat format = new SimpleDateFormat("EEE MMM dd yyyy hh:mm a", Locale.ENGLISH);
        Date timestamp = format.parse("Thu Jan 19 2012 01:00 PM");
        Reply reply = new Reply(insertedUser.getUserId(),insertedPost.getPostId(),insertedComment.getCommentId(),mentionsImagesUrls,2000,"You are so cute",timestamp,mentionsImagesUrls,mentionsImagesUrls);
        arangoWallHandler.addReply(reply);
        Reply replyDocument = arangoDB.db(dbName).collection(repliesCollection).getDocument(reply.getReplyId(),Reply.class);
        assertEquals("Reply text should be", replyDocument.getText() , "You are so cute");
    }

    @Test
    public void testDeleteReply() throws ParseException {
        arangoWallHandler.deleteReply(insertedReply);
        Reply newReply = getReply(insertedReply.getReplyId());
        assertEquals("Expected to not have that comment", newReply, null);
    }

    /**
     * test edit reply function.
     * @throws ParseException
     */
    @Test
    public void testEditReplies() throws ParseException {
        ArrayList<String> mentionsImagesUrls = new ArrayList<String>();
        mentionsImagesUrls.add("Test");
        String replyID = insertedReply.getReplyId();
        DateFormat format = new SimpleDateFormat("EEE MMM dd yyyy hh:mm a", Locale.ENGLISH);
        Date timestamp = format.parse("Thu Jan 19 2012 01:00 PM");
        Reply reply = insertedReply;
        reply.setText("Some edited text");
        arangoWallHandler.editReply(reply);
        Reply testReply = arangoWallHandler.getReply(replyID);
        assertEquals("Texts should be the same", testReply.getText(), "Some edited text");
    }

    /**
     * test get post likes.
     */
    @Test
    public void testGetPostsLikes() {
        String postId = insertedPost.getPostId();
        boolean equalsPostId = true;
        List<Like> postLikes = arangoWallHandler.getPostLikes(postId);
        for(Like like: postLikes) {
            if(!like.getLikedPostId().equals(postId))
                equalsPostId = false;
        }
        assertEquals("Incorrect like retrieved as the likedPostId does not match the postId.", true, equalsPostId);

    }

    /**
     * test get comment likes arango function.
     */
    @Test
    public void testGetCommentsLikes() {
        String commentId = insertedComment.getCommentId();
        boolean equalsCommentId = true;
        List<Like> commentLikes = arangoWallHandler.getCommentLikes(commentId);
        for(Like like: commentLikes) {
            if(!like.getLikedCommentId().equals(commentId))
                equalsCommentId = false;
        }
        assertEquals("Incorrect like retrieved as the likedCommentId does not match the commentId.", true, equalsCommentId);

    }

    /**
     * test get replies likes arango function.
     */
    @Test
    public void testGetRepliesLikes(){
        String replyId = insertedReply.getReplyId();
        boolean equalsReplyId = true;
        List<Like> replyLikes = arangoWallHandler.getReplyLikes(replyId);
        for(Like like: replyLikes) {
            if(!like.getLikedReplyId().equals(replyId))
                equalsReplyId = false;
        }
        assertEquals("Incorrect like retrieved as the likedReplyId does not match the replyId.", true, equalsReplyId);

    }

    /**
     * test add likes arango handler.
     */
    @Test
    public void testAddLikes() {
            Long likesCollectionSize = arangoDB.db(dbName).collection(likesCollection).count().getCount();
        Like like = new Like(insertedUser.getUserId(), insertedPost.getPostId(), null, null, insertedUser.getFirstName(), "headLine", "urlX");
        arangoWallHandler.addLike(like);
        Long newLikesCollectionSize = arangoDB.db(dbName).collection(likesCollection).count().getCount();
        Long expectedCollectionSize = likesCollectionSize + 1;
        Like retrievedLike = arangoDB.db(dbName).collection(likesCollection).getDocument(like.getLikeId(),Like.class);
        assertEquals("The size of the likesCollection should have increased by one", expectedCollectionSize, newLikesCollectionSize);
        assertEquals("The likerId should match the one in the like inserted", insertedUser.getUserId(), retrievedLike.getLikerId());
        assertEquals("The likedPostId should match the one in the like inserted", insertedPost.getPostId(), retrievedLike.getLikedPostId());
        assertEquals("The likedCommentId should match the one in the like inserted", null, retrievedLike.getLikedCommentId());
        assertEquals("The likedReplyId should match the one in the like inserted", null, retrievedLike.getLikedReplyId());
        assertEquals("The userName should match the one in the like inserted", insertedUser.getFirstName(), retrievedLike.getUserName());
        assertEquals("The headLine should match the one in the like inserted", "headLine", retrievedLike.getHeadLine());
        assertEquals("The imageUrl should match the one in the like inserted", "urlX", retrievedLike.getImageUrl());

    }

    /**
     * test delete likes arango handler.
     */
    @Test
    public void testDeleteLikes() {
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
        UserProfile user = insertedUser;
        String userId = user.getUserId();
        String postId = insertedPost.getPostId();
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
        UserProfile user = insertedUser;
        String userId = user.getUserId();
        int bookmarkNo = user.getBookmarks().size();
        Bookmark bookmark = new Bookmark(insertedUser.getUserId(), insertedPost.getPostId());
        arangoWallHandler.addBookmark(bookmark);
        arangoWallHandler.deleteBookmark(bookmark);
        UserProfile retrievedUser = arangoDB.db(dbName).collection(usersCollection).getDocument(userId, UserProfile.class);
        ArrayList<Bookmark> updatedBookmarks = retrievedUser.getBookmarks();
        System.out.println(updatedBookmarks.size());
        System.out.println(bookmarkNo);
        assertEquals("size of bookmarks should decreased by one", updatedBookmarks.size() , bookmarkNo);
    }

    /**
     * testing getBookmarks arango function
     */
    @Test
    public void testGetBookmarks(){
        UserProfile user = insertedUser;
        String userId = user.getUserId();
        ArrayList<Bookmark> bookmarks = user.getBookmarks();
        ArrayList<Bookmark> retrievedBookmarks = arangoWallHandler.getBookmarks(userId);
        assertEquals("size of bookmarks arrays should be equal", bookmarks.size() , retrievedBookmarks.size());
        boolean check = true;
        for (int i = 0; i < bookmarks.size(); i++){
            if(!bookmarks.get(i).equals(retrievedBookmarks.get(i))){
                check = false;
            }
        }

        assertEquals("bookmarks arrays should be identical", check , true);


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
            System.err.println("Failed to get comment: commentId; " + e.getMessage());
        }
        return comment;
    }

    /**
     * testing add comment arango handler function.
     */
    @Test
    public void testAddComment(){
        Comment comment  = new Comment(insertedUser.getUserId(),insertedPost.getPostId(),12,22,null,null,null,"comment Text","time Stamp");
        arangoWallHandler.addComment(comment);
        Comment newComment = getComment(comment.getCommentId());
        assertEquals("Expected to have a certain comment in database", newComment.getParentPostId(), insertedPost.getPostId());

    }

    /**
     * testing editing comment arango handler function.
     */
    @Test
    public void testEditComment(){
        Comment updatedComment  = insertedComment;
        updatedComment.setParentPostId(dbSeed.getInsertedPosts().get(1).getPostId());
        arangoWallHandler.editComment(updatedComment);
        Comment newComment = getComment(insertedComment.getCommentId());
        assertEquals("Expected to edit a certain comment in database", newComment.getParentPostId(), dbSeed.getInsertedPosts().get(1).getPostId());
    }

    /**
     * testing deleting comment arango handler function.
     */
    @Test
    public void testDeleteComment(){
        arangoWallHandler.deleteComment(insertedComment);
        Comment newComment = getComment(insertedComment.getCommentId());
        assertEquals("Expected to not have that comment", newComment, null);

    }

    /**
     * test get comments.
     */
    @Test
    public void testGetComments(){
        List<Comment> newComments = arangoWallHandler.getComments(insertedComment.getParentPostId());
        assertEquals("Expected to have 1 comment with that post ID", newComments.size(), 10);

    }

    /**
     * test get comment.
     */
    @Test
    public void testGetComment() {
        Comment newComment = getComment(insertedComment.getCommentId());
        assertEquals("Expected to get a certain comment", newComment.getAuthorId(), insertedComment.getAuthorId());

    }


    @AfterClass
    public static void tearDown() throws ArangoDBException, ClassNotFoundException, IOException {
        dbSeed.deleteAllUsers();
        dbSeed.deleteAllPosts();
        dbSeed.deleteAllReplies();
        dbSeed.deleteAllComments();
        dbSeed.deleteAllLikes();
        DatabaseConnection.getInstance().closeConnections();
      }

}