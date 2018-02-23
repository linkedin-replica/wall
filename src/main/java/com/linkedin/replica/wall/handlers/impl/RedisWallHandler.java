package com.linkedin.replica.wall.handlers.impl;

import com.linkedin.replica.wall.handlers.DatabaseHandler;
import com.linkedin.replica.wall.models.Bookmark;
import com.linkedin.replica.wall.models.Like;
import com.linkedin.replica.wall.models.Post;

import java.util.List;

public class RedisWallHandler implements DatabaseHandler {
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
