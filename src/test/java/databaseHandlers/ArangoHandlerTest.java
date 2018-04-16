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
import com.linkedin.replica.wall.models.*;

import java.util.List;


import com.arangodb.entity.DocumentCreateEntity;

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
        System.out.println("Posts");
        dbSeed.insertPosts();
        System.out.println("Comments");

        dbSeed.insertComments();
        System.out.println("Replies");

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
       System.out.println("In getPosts " + postId);
       Post post = insertedPost;
       try {
           System.out.println("Arango ");
           post = arangoDB.db(dbName).collection(postsCollection).getDocument(postId, Post.class);
           System.out.println("Posssst " + post.toString());

       } catch (Exception e) {
           System.out.println("In catch");
           System.err.println("Failed to get post: postId; ");
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
       ArrayList<String> urls = new ArrayList<String>();
       urls.add("urls");
       ArrayList<String> hashtags = new ArrayList<String>();
       hashtags.add("hashtags");
       ArrayList<String> mentions = new ArrayList<String>();
       mentions.add("mentions");
       ArrayList<String> shares = new ArrayList<String>();
       mentions.add("shares");

       Post post = new Post();
       System.out.println("Pooooooost1" + post.toString());
       post.setArticle(false);
       post.setHeadLine("headLine");
       post.setAuthorId(insertedUser.getUserId());
       post.setCommentsCount(12);
       post.setLikesCount(22);
       post.setImages(images);
       post.setVideos(videos);
       post.setType("type");
       post.setText("Text");
       post.setTimestamp(System.currentTimeMillis());

       System.out.println("Poooooooooooooost " + post.toString());

       arangoWallHandler.addPost(post);
       System.out.println("Poooooooooooooost2 " + post.toString());
       System.out.println("Poooooooooooooost4 " +  post.getPostId());


       Post newPost = getPosts(post.getPostId());
       System.out.println("Poooooooooooooost3 " + newPost.toString());

       assertEquals("Expected to have a certain post in database", "headLine", newPost.getHeadLine());

   }

    /**
     * test of editing post arango handler.
     * @throws ParseException
     */
   @Test
   public void testEditPost() throws ParseException {

       Post post = insertedPost;
       post.setLikesCount(13);
       HashMap<String, Object> editArgs = new HashMap<String, Object>();
       editArgs.put("postId", post.getPostId());
       editArgs.put("likesCount", post.getLikesCount());
       arangoWallHandler.editPost(editArgs);
       Post newPost = getPosts(insertedPost.getPostId());
       assertEquals("Expected to have a certain post in database", 13, newPost.getLikesCount());

   }

    /**
     * function to test delete post arango function.
     * @throws ParseException
     */
   @Test
   public void testDeletePost() throws ParseException {

       arangoWallHandler.deletePost(insertedPost);
       Post newPost = getPosts(insertedPost.getPostId());
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
        urls.add("urls");
        ArrayList<String> hashtags = new ArrayList<String>();
        hashtags.add("hashtags");
        ArrayList<String> mentions = new ArrayList<String>();
        mentions.add("mentions");
        ArrayList<String> shares = new ArrayList<String>();
        mentions.add("shares");

        Post post = new Post();
        post.setArticle(false);
        post.setHeadLine("headLine");
        post.setAuthorId(insertedUser.getUserId());
        post.setCommentsCount(12);
        post.setLikesCount(22);
        post.setImages(images);
        post.setVideos(videos);
        post.setType("type");
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
        Reply reply = new Reply();
        reply.setAuthorId(insertedUser.getUserId());
        reply.setParentPostId(insertedPost.getPostId());
        reply.setParentCommentId(insertedComment.getCommentId());
        reply.setLikesCount(2000);
        reply.setText("You are so cute");
        arangoWallHandler.addReply(reply);
        Reply replyDocument = arangoDB.db(dbName).collection(repliesCollection).getDocument(reply.getReplyId(),Reply.class);
        assertEquals("Reply text should be", "You are so cute", replyDocument.getText());
    }

    @Test
    public void testDeleteReply() throws ParseException {
        arangoWallHandler.deleteReply(insertedReply);
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
        Like like = new Like();
        like.setFirstName(insertedUser.getFirstName());
        like.setImageUrl("urlX");
        like.setLastName(insertedUser.getLastName());
        like.setLikedCommentId(null);
        like.setLikedPostId(insertedPost.getPostId());
        like.setLikedReplyId(null);
        like.setLikerId(insertedUser.getUserId());
        arangoWallHandler.addLike(like);
        Long newLikesCollectionSize = arangoDB.db(dbName).collection(likesCollection).count().getCount();
        Long expectedCollectionSize = likesCollectionSize + 1;
        Like retrievedLike = arangoDB.db(dbName).collection(likesCollection).getDocument(like.getLikeId(),Like.class);
        assertEquals("The size of the likesCollection should have increased by one", expectedCollectionSize, newLikesCollectionSize);
        assertEquals("The likerId should match the one in the like inserted", insertedUser.getUserId(), retrievedLike.getLikerId());
        assertEquals("The likedPostId should match the one in the like inserted", insertedPost.getPostId(), retrievedLike.getLikedPostId());
        assertEquals("The likedCommentId should match the one in the like inserted", null, retrievedLike.getLikedCommentId());
        assertEquals("The likedReplyId should match the one in the like inserted", null, retrievedLike.getLikedReplyId());
        assertEquals("The firstName should match the one in the like inserted", insertedUser.getFirstName(), retrievedLike.getFirstName());
        assertEquals("The lastName should match the one in the like inserted", insertedUser.getLastName(), retrievedLike.getLastName());
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
        assertEquals("userID should be the same in the inserted bookmark", bookmark.getUserId(), retrievedBookmark.getUserId());
        assertEquals("postID should be the same in the inserted bookmark", bookmark.getPostId(), retrievedBookmark.getPostId());
    }

    /**
     * test DeleteBookmark function
     */
    @Test
    public void testDeleteBookmark(){
        UserProfile user = insertedUser;
        String userId = user.getUserId();
        int bookmarkNo = user.getBookmarks().size();
        Bookmark bookmark = insertedUser.getBookmarks().get(0);
        arangoWallHandler.deleteBookmark(bookmark);
        UserProfile retrievedUser = arangoDB.db(dbName).collection(usersCollection).getDocument(userId, UserProfile.class);
        ArrayList<Bookmark> updatedBookmarks = retrievedUser.getBookmarks();
        assertEquals("size of bookmarks should decreased by one", bookmarkNo - 1, updatedBookmarks.size());
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
            System.err.println("Failed to get comment: commentId; " + e.getMessage());
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
        comment.setLikesCount(12);
        comment.setRepliesCount(22);
        comment.setText("comment Text");
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
        editCommentArgs.put("parentPostId", updatedComment.getParentPostId());
        arangoWallHandler.editComment(editCommentArgs);
        Comment newComment = getComment(insertedComment.getCommentId());
        assertEquals("Expected to edit a certain comment in database",dbSeed.getInsertedPosts().get(1).getPostId(), newComment.getParentPostId());
    }

    /**
     * testing deleting comment arango handler function.
     */
    @Test
    public void testDeleteComment(){
        arangoWallHandler.deleteComment(insertedComment);
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
      @Test
    public void testNewsfeed(){
        ArrayList<String> images = new ArrayList<String>();
        images.add("images");
        ArrayList<String> videos = new ArrayList<String>();
        videos.add("videos");
        Post post1 = new Post();
        post1.setArticle(false);
        post1.setHeadLine("headLine");
        post1.setAuthorId(dbSeed.getInsertedUsers().get(0).getUserId());
        post1.setCommentsCount(12);
        post1.setLikesCount(22);
        post1.setImages(images);
        post1.setVideos(videos);
        post1.setType("type");
        post1.setText("post 1");

        Post post2 = new Post();
        post2.setArticle(false);
        post2.setHeadLine("headLine");
        post2.setAuthorId(dbSeed.getInsertedUsers().get(0).getUserId());
        post2.setCommentsCount(12);
        post2.setLikesCount(22);
        post2.setImages(images);
        post2.setVideos(videos);
        post2.setType("type");
        post2.setText("post 2");

        Post post3 = new Post();
        post3.setArticle(false);
        post3.setHeadLine("headLine");
        post3.setAuthorId(dbSeed.getInsertedUsers().get(1).getUserId());
        post3.setCommentsCount(12);
        post3.setLikesCount(22);
        post3.setImages(images);
        post3.setVideos(videos);
        post3.setType("type");
        post3.setText("post 3");

        Post post4 = new Post();
        post4.setArticle(false);
        post4.setHeadLine("headLine");
        post4.setAuthorId(dbSeed.getInsertedUsers().get(1).getUserId());
        post4.setCommentsCount(12);
        post4.setLikesCount(22);
        post4.setImages(images);
        post4.setVideos(videos);        post4.setType("type");
        post4.setText("post 4");

        addPost(post1);
        addPost(post2);
        addPost(post3);
        addPost(post4);
        UserProfile user = dbSeed.getInsertedUsers().get(dbSeed.getInsertedUsers().size()-1);
        user.getFriendsList().add(dbSeed.getInsertedUsers().get(0).getUserId());
        user.getFriendsList().add(dbSeed.getInsertedUsers().get(1).getUserId());
        List<Post> newsfeed = arangoWallHandler.getFriendsPosts(user,10,0);
        assertEquals("Expected to have the list ordered", newsfeed.get(0).getText(), "post 2");
        assertEquals("Expected to have a 4 posts returned", newsfeed.size(), 4);
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