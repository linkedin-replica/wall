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
    private ArrayList<UserProfile> insertedUsers;

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
        dbSeed.insertUsers();
        dbSeed.insertPosts();
        dbSeed.insertReplies();
        dbSeed.insertLikes();
        dbSeed.insertComments();
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
   public void testAddPost() throws ParseException {

       DateFormat format = new SimpleDateFormat("EEE MMM dd yyyy hh:mm a", Locale.ENGLISH);
       Date timestamp = format.parse("Thu Jan 19 2012 01:00 PM");


        Post post = new Post("postId", "authorId", null, "companyId", null, null,
                null,null, 12, "images", "videos", "urls", 30,
                "shares", timestamp, true, false);
        arangoWallHandler.addPost(post);
        Post newPost = getPosts("postId");
        assertEquals("Expected to have a certain post in database", newPost.getCompanyId(), "companyId");

   }

   @Test
   public void testEditPost() throws ParseException {

       DateFormat format = new SimpleDateFormat("EEE MMM dd yyyy hh:mm a", Locale.ENGLISH);
       Date timestamp = format.parse("Thu Jan 19 2012 01:00 PM");
       Post post = new Post("postId", "authorId", null, "companyId", null, null,
               null,null, 13, "images", "videos", "urls", 30,
               "shares", timestamp, true, false);
       arangoWallHandler.editPost(post);
       Post newPost = getPosts("postId");
       assertEquals("Expected to have a certain post in database", newPost.getLikesCount(), 13);

   }

   @Test
   public void testDeletePost() throws ParseException {

       DateFormat format = new SimpleDateFormat("EEE MMM dd yyyy hh:mm a", Locale.ENGLISH);
       Date timestamp = format.parse("Thu Jan 19 2012 01:00 PM");
       Post post = new Post("postId", "authorId", null, "companyId", null, null,
               null,null, 12, "images", "videos", "urls", 30,
               "shares", timestamp, true, false);
       arangoWallHandler.deletePost(post);
       Post newPost = getPosts("postId");
       assertEquals("Expected to have a certain post in database", newPost, null);
   }


    @Test
    public void testGetPosts() throws ParseException {
        DateFormat format = new SimpleDateFormat("EEE MMM dd yyyy hh:mm a", Locale.ENGLISH);
        Date timestamp = format.parse("Thu Jan 19 2012 01:00 PM");
        Post post = new Post("postId", "232", null, "companyId", null, null,
                null,null, 12, "images", "videos", "urls", 30,
                "shares", timestamp, true, false);
        addPost(post);
        List<Post> newPost = arangoWallHandler.getPosts("232");
        assertEquals("Expected to have 1 post with that post ID", newPost.size(), 1);

    }

    @Test
    public void testAddReply() throws ClassNotFoundException, InstantiationException, ParseException, IllegalAccessException {
        ArrayList<String> mentionsImagesUrls = new ArrayList<String>();
        mentionsImagesUrls.add("Test");
        String replyID = "112";
        DateFormat format = new SimpleDateFormat("EEE MMM dd yyyy hh:mm a", Locale.ENGLISH);
        Date timestamp = format.parse("Thu Jan 19 2012 01:00 PM");
        Reply reply = new Reply(replyID,"6","1","4",mentionsImagesUrls,2l,"You are so cute",timestamp,mentionsImagesUrls,mentionsImagesUrls);
        arangoWallHandler.addReply(reply);
        Reply replyDocument = arangoDB.db(dbName).collection(repliesCollection).getDocument(replyID,Reply.class);
        System.out.println("reply doc " + replyDocument);
        assertEquals("Reply ID should be", replyDocument.getReplyId() , "112");
        assertEquals("Reply text should be", replyDocument.getText() , "You are so cute");
    }

    @Test
    public void testDeleteReply() throws ParseException {
        arangoWallHandler.getTopPosts();
//        String commentID = "45";
//        List<Reply> replies = arangoWallHandler.getReplies(commentID);
//        if(replies!=null){
//            Reply reply = replies.get(0);
//            arangoWallHandler.deleteReply(reply);
//            assertEquals("Size should be decremented by one", replies.size()-1 , arangoWallHandler.getReplies(commentID).size());

     //   }
    }

    @Test
    public void testEditReplies() throws ParseException {
        ArrayList<String> mentionsImagesUrls = new ArrayList<String>();
        mentionsImagesUrls.add("Test");
        String replyID = "1";
        DateFormat format = new SimpleDateFormat("EEE MMM dd yyyy hh:mm a", Locale.ENGLISH);
        Date timestamp = format.parse("Thu Jan 19 2012 01:00 PM");
        Reply reply = new Reply(replyID,"6","1","4",mentionsImagesUrls,2l,"Some edited text",timestamp,mentionsImagesUrls,mentionsImagesUrls);
        arangoWallHandler.editReply(reply);
        Reply testReply = arangoWallHandler.getReply(replyID);
        assertEquals("Texts should be the same", testReply.getText(), "Some edited text");


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

    @Test
    public void testGetComments(){
        List<Comment> newComments = arangoWallHandler.getComments("123");
        assertEquals("Expected to have 1 comment with that post ID", newComments.size(), 1);

    }

    @Test
    public void testGetComment() {
        Comment newComment = getComment("commentID");
        assertEquals("Expected to get a certain comment", newComment.getAuthorId(), "authorID");

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