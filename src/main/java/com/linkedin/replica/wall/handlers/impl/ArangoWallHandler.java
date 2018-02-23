package com.linkedin.replica.wall.handlers.impl;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import com.arangodb.util.MapBuilder;
import com.linkedin.replica.wall.config.DatabaseConnection;
import com.linkedin.replica.wall.handlers.WallHandler;
import com.linkedin.replica.wall.models.Bookmark;
import com.linkedin.replica.wall.models.Comment;
import com.linkedin.replica.wall.models.Post;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public  class ArangoWallHandler implements WallHandler {

    ArangoDB arangoDB;
    private Properties properties;
    String dbName;
    String likesCollection;
    String commentCollection;

    public ArangoWallHandler() throws IOException, ClassNotFoundException {
        arangoDB = DatabaseConnection.getInstance().getArangodb();
        properties = new Properties();
        properties.load(new FileInputStream("config"));
        dbName = properties.getProperty("arangodb.name");
        likesCollection = properties.getProperty("collections.likes.name");
        commentCollection = properties.getProperty("collections.comments.name");

    }

    public List<Bookmark> getBookmarks() {
        return null;
    }

    public void addBookmark() {

    }

    public void deleteBookmark() {

    }

    public List<Post> getPosts() {
        return null;
    }

    public void addPost() {

    }

    public void editPost() {

    }

    public void deletePost() {

    }

    public List<Post> getComments() {
        return null;
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
                String [] images = (String []) commentDocument.getAttribute("images");
                String [] urls = (String []) commentDocument.getAttribute("urls");
                String [] mentions = (String []) commentDocument.getAttribute("mentions");
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

    public void addComment(Comment comment) throws IOException, ClassNotFoundException {

        BaseDocument commentDocument = createCommentDoc(comment);


        try {
            arangoDB.db(dbName).collection("comments").insertDocument(commentDocument);
            System.out.println("Document created");
        } catch (ArangoDBException e) {
            System.err.println("Failed to create document. " + e.getMessage());
        }

    }



    public void editComment(Comment comment) throws IOException, ClassNotFoundException{
        BaseDocument commentDocument = createCommentDoc(comment);
        try {
            arangoDB.db(dbName).collection("comments").updateDocument(comment.getCommentId(), commentDocument);
        } catch (ArangoDBException e) {
            System.err.println("Failed to update document. " + e.getMessage());
        }
    }

    public void deleteComment(Comment comment) throws IOException, ClassNotFoundException{
        try {
            arangoDB.db(dbName).collection("comments").deleteDocument(comment.getCommentId());
        } catch (ArangoDBException e) {
            System.err.println("Failed to delete document. " + e.getMessage());
        }
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

    public List<Post> getlikes() {
        return null;
    }

    public void addLike() {

    }

    public void deleteLike() {

    }


}
