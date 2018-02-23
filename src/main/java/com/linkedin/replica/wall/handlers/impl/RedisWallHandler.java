package com.linkedin.replica.wall.handlers.impl;

import com.linkedin.replica.wall.handlers.DatabaseHandler;
import com.linkedin.replica.wall.models.*;

import java.util.List;

public class RedisWallHandler implements DatabaseHandler {
    public List<Bookmark> getBookmarks() {
        return null;
    }

    public String addBookmark(Bookmark bookmark) {
        return null;

    }

    public String deleteBookmark(Bookmark bookmark) {
        return null;

    }


    public List<Post> getPosts() {
        return null;
    }

    public String addPost() {
        return null;

    }

    public String editPost() {
        return null;

    }

    public String deletePost() {
        return null;

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
    public void addComment(Comment comment) {

    }
    /**
     * Edit a comment
     */
    public void editComment(Comment comment) {

    }

    /**
     * Delete a comment
     */
    public void deleteComment(Comment comment){

    }

    public List<Reply> getReplies(String commentId) {
        return null;
    }

    public String addReply(Reply reply) {
        return null;
    }

    public String editReply(Reply reply) {
        return null;
    }

    public String deleteReply(Reply reply) {
        return null;
    }

    public List<Like> getPostLikes(String postId) {
        return null;
    }

    public List<Like> getCommentLikes(String commentId) {
        return null;
    }

    public List<Like> getReplyLikes(String replyId) {
        return null;
    }

    public String addLike(Like like) {

        return null;
    }

    public String deleteLike(Like like) {

        return null;
    }
}
