package com.linkedin.replica.wall.handlers.impl;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import com.arangodb.util.MapBuilder;
import com.linkedin.replica.wall.config.DatabaseConnection;
import com.linkedin.replica.wall.handlers.WallHandler;
import com.linkedin.replica.wall.models.Bookmark;
import com.linkedin.replica.wall.models.Like;
import com.linkedin.replica.wall.models.Post;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import com.arangodb.ArangoDB;

public class ArangoWallHandler implements WallHandler {

    ArangoDB arangoDB;
    private Properties properties;


    public ArangoWallHandler() throws IOException, ClassNotFoundException {
        arangoDB = DatabaseConnection.getInstance().getArangodb();
        properties = new Properties();
        properties.load(new FileInputStream("config"));

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

    public void addLike(Like like) throws IOException, ClassNotFoundException {
        String dbName = properties.getProperty("arangodb.name");
        String likesCollection = properties.getProperty("collections.likes.name");

        BaseDocument likeDocument = new BaseDocument();
        likeDocument.setKey(like.getLikeId());
        likeDocument.addAttribute("likerId", like.getLikerId());
        likeDocument.addAttribute("username", like.getUserName());
        likeDocument.addAttribute("headline", like.getHeadLine());
        likeDocument.addAttribute("imageUrl", like.getImageUrl());
        likeDocument.addAttribute("likedPostId", like.getLikedPostId());
        likeDocument.addAttribute("LikedCommentId", like.getLikedCommentId());
        likeDocument.addAttribute("likedReplyId", like.getLikedReplyId());

        try {
            arangoDB.db(dbName).collection(likesCollection).insertDocument(likeDocument);
            System.out.println("Document created");
        } catch (ArangoDBException e) {
            System.err.println("Failed to create document. " + e.getMessage());
        }

        if(like.getLikedPostId() != null){
            //Todo:
            // 1. get post: call getPosts()
            // 2. update post object: add 1 to likes
            // 3. update post document: call editPost()

        }
        else if(like.getLikedCommentId() != null){
            //Todo:
            // 1. get comment: call getComments()
            // 2. update comment object: add 1 to likes
            // 3. update comment document: call editComment()

        }
        else if(like.getLikedReplyId() != null){
            //Todo:
            // 1. get reply: call getReply()
            // 2. update reply object: add 1 to reply
            // 3. update reply document: call editReply()

        }


    }

    public void deleteLike(Like like) throws IOException, ClassNotFoundException {
        String dbName = properties.getProperty("arangodb.name");
        String likesCollection = properties.getProperty("collections.likes.name");

        try {
            arangoDB.db(dbName).collection(likesCollection).deleteDocument(like.getLikeId());
        } catch (ArangoDBException e) {
            System.err.println("Failed to delete document. " + e.getMessage());
        }
        if(like.getLikedPostId() != null){
            //Todo:
            // 1. get post: call getPosts()
            // 2. update post object: subtract 1 from likes
            // 3. update post document: call editPost()

        }
        else if(like.getLikedCommentId() != null){
            //Todo:
            // 1. get comment: call getComments()
            // 2. update comment object: subtract 1 from likes
            // 3. update comment document: call editComment()

        }
        else if(like.getLikedReplyId() != null){
            //Todo:
            // 1. get reply: call getReply()
            // 2. update reply object: subtract 1 from reply
            // 3. update reply document: call editReply()

        }

    }
}
