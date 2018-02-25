package com.linkedin.replica.wall.handlers.impl;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import com.arangodb.util.MapBuilder;
import com.linkedin.replica.wall.config.DatabaseConnection;
import com.linkedin.replica.wall.handlers.DatabaseHandler;

import com.linkedin.replica.wall.models.Bookmark;
import com.linkedin.replica.wall.models.Comment;
import com.linkedin.replica.wall.models.Like;
import com.linkedin.replica.wall.models.Post;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public  class ArangoWallHandler implements DatabaseHandler {

    ArangoDB arangoDB;
    private Properties properties;
    String dbName;
    String likesCollection;
    String commentCollection;
    String postCollection;

    public ArangoWallHandler() throws IOException, ClassNotFoundException {
        arangoDB = DatabaseConnection.getInstance().getArangodb();
        properties = new Properties();
        properties.load(new FileInputStream("config"));
        dbName = properties.getProperty("arangodb.name");
        likesCollection = properties.getProperty("collections.likes.name");
        commentCollection = properties.getProperty("collections.comments.name");
        postCollection = properties.getProperty("collections.posts.name");

    }

    public List<Bookmark> getBookmarks() {
        return null;
    }

    public void addBookmark() {

    }

    public void deleteBookmark() {

    }

    public BaseDocument createPostDoc(Post post){
        BaseDocument postDocument = new BaseDocument();
        postDocument.setKey(post.getPostID());
        postDocument.addAttribute("authorID", post.getAuthorID());
        postDocument.addAttribute("type", post.getType());
        postDocument.addAttribute("companyID", post.getCompanyID());
        postDocument.addAttribute("privacy", post.getPrivacy());
        postDocument.addAttribute("text", post.getText());
        postDocument.addAttribute("timeStamp", post.getTimeStamp());
        postDocument.addAttribute("isCompanyPost", post.isCompanyPost());
        postDocument.addAttribute("isPrior", post.isPrior());
        postDocument.addAttribute("hashtags", post.getHashtags());
        postDocument.addAttribute("mentions", post.getMentions());
        postDocument.addAttribute("images", post.getImages());
        postDocument.addAttribute("videos", post.getVideos());
        postDocument.addAttribute("urls", post.getUrls());
        postDocument.addAttribute("shares", post.getShares());
        postDocument.addAttribute("likesCount", post.getUrls());
        postDocument.addAttribute("commentsCount", post.getShares());

        return postDocument;
    }

    public List<Post> getPosts(String userID) {

        final ArrayList<Post> posts = new ArrayList<Post>();
        try {
            String query = "FOR l IN " + postCollection + " FILTER l.authorID == " + userID + " RETURN l";
            Map<String, Object> bindVars = new MapBuilder().put("authorID", userID).get();
            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null, BaseDocument.class);
            cursor.forEachRemaining(postDocument -> {
                Post post;
                String postID = postDocument.getKey();
                String authorID = (String) postDocument.getAttribute("authorID");
                String type = (String) postDocument.getAttribute("type");
                String companyID = (String) postDocument.getAttribute("companyID");
                String privacy = (String) postDocument.getAttribute("privacy");
                String text = (String) postDocument.getAttribute("text");
                String timeStamp = (String) postDocument.getAttribute("timeStamp" );
                boolean isCompanyPost = (boolean) postDocument.getAttribute("isCompanypost");
                boolean isPrior = (boolean) postDocument.getAttribute("isPrior" );
                ArrayList<String> hashtags = (ArrayList<String>) postDocument.getAttribute("hashtags");
                ArrayList<String> mentions = (ArrayList<String>) postDocument.getAttribute("mentions");
                ArrayList<String> images = (ArrayList<String>) postDocument.getAttribute("images");
                ArrayList<String> videos = (ArrayList<String>) postDocument.getAttribute("videos");
                ArrayList<String> urls = (ArrayList<String>) postDocument.getAttribute("urls");
                ArrayList<String> shares = (ArrayList<String>) postDocument.getAttribute("shares");
                int likesCount = (Integer) postDocument.getAttribute("likesCount");
                int commentsCount = (Integer) postDocument.getAttribute("commentsCount");



                post = new Post(postID, authorID, type, companyID, privacy, text, timeStamp,isCompanyPost,isPrior,
                        hashtags, mentions,images,videos,urls,shares,likesCount,commentsCount);
                posts.add(post);
                System.out.println("Key: " + postDocument.getKey());
            });
        } catch (ArangoDBException e) {
            System.err.println("Failed to execute query. " + e.getMessage());
        }
        return posts;
    }

    public String addPost(Post post) throws IOException, ClassNotFoundException{
        String response = "";
        BaseDocument postDocument = createPostDoc(post);

        try {
            arangoDB.db(dbName).collection("posts").insertDocument(postDocument);
            response = "Post Created";
        } catch (ArangoDBException e) {
            response = "Failed to add post. " + e.getMessage();
        }
        return response;
    }

    public String editPost(Post post) throws IOException, ClassNotFoundException{

        String response = "";
        BaseDocument postDocument = createPostDoc(post);
        try {
            arangoDB.db(dbName).collection("posts").updateDocument(post.getPostID(), postDocument);

        } catch (ArangoDBException e) {
            System.err.println("Failed to update post. " + e.getMessage());
            response = "Failed to update post. " + e.getMessage();
        }
        return response;
    }

    public String deletePost(Post post) throws IOException, ClassNotFoundException{

        String response="";
        try {
            arangoDB.db(dbName).collection("posts").deleteDocument(post.getPostID());

        } catch (ArangoDBException e) {
            System.err.println("Failed to delete post. " + e.getMessage());
            response = "Failed to delete post";
        }
        return response;
    }



    public List<Comment> getComments(String postID) {

        final ArrayList<Comment> comments = new ArrayList<Comment>();
        try {
            String query = "FOR l IN " + commentCollection + " FILTER l.parentPostId == " + postID + " RETURN l";
            Map<String, Object> bindVars = new MapBuilder().put("parentPostID", postID).get();
            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null, BaseDocument.class);
            cursor.forEachRemaining(commentDocument -> {
                Comment comment;
                String commentID = commentDocument.getKey();
                String authorID = (String) commentDocument.getAttribute("authorID");
                String parentPostID = (String) commentDocument.getAttribute("parentPostID");
                int likesCount = (Integer) commentDocument.getAttribute("likesCount");
                int repliesCount = (Integer) commentDocument.getAttribute("repliesCount");
                ArrayList<String> images = (ArrayList<String>) commentDocument.getAttribute("images");
                ArrayList<String> urls = (ArrayList<String>) commentDocument.getAttribute("urls");
                ArrayList<String> mentions = (ArrayList<String>) commentDocument.getAttribute("mentions");
                String text = (String) commentDocument.getAttribute("text");
                String timeStamp = (String) commentDocument.getAttribute("timeStamp");
                comment = new Comment(commentID, authorID, parentPostID, likesCount, repliesCount, images, urls,mentions,text,timeStamp);
                comments.add(comment);
                System.out.println("Key: " + commentDocument.getKey());
            });
        } catch (ArangoDBException e) {
            System.err.println("Failed to execute query. " + e.getMessage());
        }
        return comments;
    }

    public BaseDocument createCommentDoc(Comment comment){
        BaseDocument commentDocument = new BaseDocument();
        commentDocument.setKey(comment.getCommentId());
        commentDocument.addAttribute("authorID", comment.getAuthorId());
        commentDocument.addAttribute("parentPostID", comment.getParentPostId());
        commentDocument.addAttribute("likesCount", comment.getLikesCount());
        commentDocument.addAttribute("repliesCount", comment.getRepliesCount());
        commentDocument.addAttribute("images", comment.getImages());
        commentDocument.addAttribute("urls", comment.getUrls());
        commentDocument.addAttribute("mentions", comment.getMentions());
        commentDocument.addAttribute("text", comment.getText());
        commentDocument.addAttribute("timeStamp", comment.getTimeStamp());
        return commentDocument;
    }

    public String addComment(Comment comment) throws IOException, ClassNotFoundException {
        String response = "";
        BaseDocument commentDocument = createCommentDoc(comment);


        try {
            arangoDB.db(dbName).collection("comments").insertDocument(commentDocument);
            response = "Comment Created";
        } catch (ArangoDBException e) {
            response = "Failed to add comment. " + e.getMessage();
        }
        return response;

    }



    public String editComment(Comment comment) throws IOException, ClassNotFoundException{
        String response = "";
        BaseDocument commentDocument = createCommentDoc(comment);
        try {
            arangoDB.db(dbName).collection("comments").updateDocument(comment.getCommentId(), commentDocument);

        } catch (ArangoDBException e) {
            System.err.println("Failed to update comment. " + e.getMessage());
            response = "Failed to update comment. " + e.getMessage();
        }
        return response;
    }

    public String deleteComment(Comment comment) throws IOException, ClassNotFoundException{
        String response="";
        try {
            arangoDB.db(dbName).collection("comments").deleteDocument(comment.getCommentId());

        } catch (ArangoDBException e) {
            System.err.println("Failed to delete document. " + e.getMessage());
            response = "Failed to delete comment";
        }
        return response;
    }

    public List<Post> getReplies() {
        return null;
    }

    public void addReply() {

    }

    public void editReply() {

    }

    public void deleteReply() {

    }

    @Override
    public List<Like> getPostLikes(String postId) {
        return null;
    }

    @Override
    public List<Like> getCommentLikes(String commentId) {
        return null;
    }

    @Override
    public List<Like> getReplyLikes(String replyId) {
        return null;
    }

    @Override
    public String addLike(Like like) {
        return null;
    }

    @Override
    public String deleteLike(Like like) {
        return null;
    }

    public List<Post> getlikes() {
        return null;
    }

    public void addLike() {

    }

    public void deleteLike() {

    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        DatabaseHandler arangoWallHandler = new ArangoWallHandler();
        Properties properties = new Properties();
        properties.load(new FileInputStream("config"));
        String dbName = properties.getProperty(properties.getProperty("arangodb.name"));
        ArangoDB arangoDB = DatabaseConnection.getInstance().getArangodb();

       // UserProfile userProfile = new UserProfile("khly@gmail.com", "Mohamed", "Khaled");
        BaseDocument myObject = new BaseDocument();
        myObject.setKey("se7s");
        myObject.addAttribute("email", "khly@gmail.com");
        myObject.addAttribute("firstName", "Mohamed");
        myObject.addAttribute("lastName", "Khaled");
        System.out.println("3ww");
        try {
            arangoDB.db(dbName).collection("Users").insertDocument(myObject);
            System.out.println("Document created");
        } catch (ArangoDBException e) {
            System.err.println("Failed to create document. " + e.getMessage());
        }

        try {
            BaseDocument myUpdatedDocument = arangoDB.db(dbName).collection("Users").getDocument("se7s",
                    BaseDocument.class);
            System.out.println("Key: " + myUpdatedDocument.getKey());
            System.out.println("firstName: " + myUpdatedDocument.getAttribute("firstName"));
            System.out.println("lastName: " + myUpdatedDocument.getAttribute("lastName"));
            System.out.println("email: " + myUpdatedDocument.getAttribute("email"));
        } catch (ArangoDBException e) {
            System.err.println("Failed to get document: myKey; " + e.getMessage());
        }

        System.out.println();

    }


}
