
package com.linkedin.replica.wall.handlers.impl;

import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import com.linkedin.replica.wall.config.DatabaseConnection;
import com.linkedin.replica.wall.handlers.WallHandler;
import com.linkedin.replica.wall.models.Bookmark;
import com.linkedin.replica.wall.models.Comment;
import com.linkedin.replica.wall.models.Post;

import java.io.IOException;
import java.util.List;

public class RedisWallHandler implements WallHandler {
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

    /**
     *
     * @return list of comments
     */
    public List<Comment> getComments(String postID){
        return null;
    }

    /**
     * Add a comment
     */
    public void addComment(Comment comment) throws IOException, ClassNotFoundException{

    }
    /**
     * Edit a comment
     */
    public void editComment(Comment comment) throws IOException, ClassNotFoundException{

    }

    /**
     * Delete a comment
     */
    public void deleteComment(Comment comment)throws IOException, ClassNotFoundException{

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
