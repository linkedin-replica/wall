package com.linkedin.replica.wall.handlers.impl;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import com.arangodb.util.MapBuilder;
import com.linkedin.replica.wall.config.DatabaseConnection;
import com.linkedin.replica.wall.handlers.DatabaseHandler;
import com.linkedin.replica.wall.models.Bookmark;
import com.linkedin.replica.wall.models.Post;
import com.linkedin.replica.wall.models.Reply;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class ArangoWallHandler implements DatabaseHandler {

    ArangoDB arangoDB;
    private Properties properties;
    String dbName;
    String likesCollection;
    String repliesCollection;


    public ArangoWallHandler() throws IOException, ClassNotFoundException {
        arangoDB = DatabaseConnection.getInstance().getArangodb();
        properties = new Properties();
        properties.load(new FileInputStream("config"));
        dbName = properties.getProperty("arangodb.name");
        likesCollection = properties.getProperty("collections.likes.name");
        repliesCollection = properties.getProperty("collections.replies.name");

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

    public void addComment() {

    }

    public void editComment() {

    }

    public void deleteComment() {

    }

    public List<Reply> getReplies(String commentId) {
        ArrayList<Reply> replies = new ArrayList<Reply>();
        try {
            String query = "FOR r IN " + repliesCollection + " FILTER r.parentCommentId == " + commentId + " RETURN r";
            Map<String, Object> bindVars = new MapBuilder().put("name", "Homer").get();
            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    BaseDocument.class);
            cursor.forEachRemaining(replyDocument -> {
                Reply reply;
                String replyId = replyDocument.getKey();
                String authorId = (String) replyDocument.getAttribute("authorId");
                String parentPostId = (String) replyDocument.getAttribute("parentPostId");
                String parentCommentId = (String) replyDocument.getAttribute("parentCommentId");
                ArrayList<String> mentions = (ArrayList<String>) replyDocument.getAttribute("mentions");
                Long likesCount = (Long) replyDocument.getAttribute("likesCount");
                String text = (String) replyDocument.getAttribute("text");
                Date timestamp = (Date) replyDocument.getAttribute("timestamp");
                ArrayList<String> images = (ArrayList<String>) replyDocument.getAttribute("images");
                ArrayList<String> urls = (ArrayList<String>) replyDocument.getAttribute("urls");


                reply = new Reply(replyId, authorId, parentPostId, parentCommentId, mentions, likesCount, text, timestamp, images, urls);
                replies.add(reply);
                System.out.println("Key: " + replyDocument.getKey());
            });
        } catch (ArangoDBException e) {
            System.err.println("Failed to get replies. " + e.getMessage());
        }
        return replies;
    }


    public String addReply(Reply reply) {
        String response = "";
        BaseDocument replyDocument = createReplyDocument(reply);
        try {
            arangoDB.db(dbName).collection(repliesCollection).insertDocument(replyDocument);
            System.out.println("Reply created");
            response = "Reply created";
        } catch (ArangoDBException e) {
            System.err.println("Failed to add reply. " + e.getMessage());
            response = "Failed to add reply. " + e.getMessage();
        }
        //Todo:
        // 1. get comment: call getComments()
        // 2. update comment object: add 1 to repliesCount
        // 3. update comment document: call editComments()
        // Do the same for posts

        return response;

    }

    public BaseDocument createReplyDocument(Reply reply) {
        BaseDocument replyDocument = new BaseDocument();
        replyDocument.setKey(reply.getReplyId());
        replyDocument.addAttribute("authorId", reply.getAuthorId());
        replyDocument.addAttribute("parentPostId", reply.getParentPostId());
        replyDocument.addAttribute("parentCommentId", reply.getParentCommentId());
        replyDocument.addAttribute("mentions", reply.getMentions());
        replyDocument.addAttribute("likesCount", reply.getLikesCount());
        replyDocument.addAttribute("text", reply.getText());
        replyDocument.addAttribute("timestamp", reply.getTimestamp());
        replyDocument.addAttribute("images", reply.getImages());
        replyDocument.addAttribute("urls", reply.getUrls());
        return replyDocument;

    }

    public String editReply(Reply reply) {
        String response = "";
        BaseDocument replyDocument = createReplyDocument(reply);
        try {
            arangoDB.db(dbName).collection(repliesCollection).updateDocument(reply.getReplyId() ,replyDocument);
        } catch (ArangoDBException e) {
            System.err.println("Failed to update reply. " + e.getMessage());
            response = "Failed to update reply. " + e.getMessage();
        }
        return response;


    }

    public String deleteReply(Reply reply) {
        String response = "";
        try {
            arangoDB.db(dbName).collection(repliesCollection).deleteDocument(reply.getReplyId());
        } catch (ArangoDBException e) {
            System.err.println("Failed to delete reply. " + e.getMessage());
            response = "Failed to delete reply. " + e.getMessage();
        }
            //Todo:
            // 1. get comment: call getComments()
            // 2. update comment object: subtract 1 from repliesCount
            // 3. update comment document: call editComments()
            // Do the same for posts
        return response;

    }

    public List<Post> getlikes() {
        return null;
    }

    public void addLike() {

    }

    public void deleteLike() {

    }
}
