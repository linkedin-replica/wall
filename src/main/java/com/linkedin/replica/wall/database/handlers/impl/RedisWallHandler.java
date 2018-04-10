package com.linkedin.replica.wall.database.handlers.impl;

import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.*;
import com.linkedin.replica.wall.models.Bookmark;
import com.linkedin.replica.wall.models.Post;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class RedisWallHandler implements WallHandler {

    /**
     * get posts of user's friends
     * @param user,limit,offset
     * @return
     */
    public List<Post> getFriendsPosts(UserProfile user,int limit, int offset){
        return null;
    }
    /**
     * add bookmark to user's bookmarks
     * @param bookmark
     * @return
     */
    public String addBookmark(Bookmark bookmark) {
        return null;

    }

    /**
     * delete bookmark from user's bookmarks
     * @param bookmark
     * @return
     */
    public String deleteBookmark(Bookmark bookmark) {
        return null;

    }

    /**
     * get a list of users bookmarks.
     * @param userId
     * @return
     */
    public ArrayList<Bookmark> getBookmarks(String userId){return null ;}


    public List<Post> getPosts(String userId) {
        return null;
    }

    public String addPost(Post post) {
        return null;
    }

    public String editPost(Post post) {
        return null;
    }

    public String deletePost(String post) {
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
    public String addComment(Comment comment) {
    return null;
    }
    /**
     * Edit a comment
     */
    public String editComment(Comment comment) {
    return null;
    }

    /**
     * Delete a comment
     */
    public String deleteComment(Comment comment){
        return null;
    }

    public List<Reply> getReplies(String commentId) {
        return null;
    }

    @Override
    public Reply getReply(String replyId) {
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

    @Override
    public void getTopPosts()throws ParseException {

    }

    @Override
    public Comment getComment(String commentId) {
        return null;
    }

    @Override
    public Post getPost(String postId) {
        return null;
    }

    @Override
    public Like getLike(String likeId) {
        return null;
    }

    @Override
    public UserProfile getUser(String userId) {
        return null;
    }
}
