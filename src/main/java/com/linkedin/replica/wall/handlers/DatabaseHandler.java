package com.linkedin.replica.wall.handlers;

import com.linkedin.replica.wall.models.Bookmark;
import com.linkedin.replica.wall.models.Like;
import com.linkedin.replica.wall.models.Post;
import com.linkedin.replica.wall.models.Reply;

import java.io.IOException;
import java.util.List;

public interface DatabaseHandler {

    /**
     * @return list of bookmarks
     */
    public List<Bookmark> getBookmarks();

    /**
     * Add a bookmark
     */
    public void addBookmark();

    /**
     * Delete a bookmark
     */
    public void deleteBookmark();

    /**
     *
     * @return list of posts
     */
    public List<Post> getPosts();

    /**
     * Add a post
     */
    public void addPost();

    /**
     * Edit a post
     */
    public void editPost();

    /**
     * Delete a post
     */
    public void deletePost();

    /**
     *
     * @return list of comments
     */
    public List<Post> getComments();

    /**
     * Add a comment
     */
    public void addComment();

    /**
     * Edit a comment
     */
    public void editComment();

    /**
     * Delete a comment
     */
    public void deleteComment();


    /**
     *
     * @return list of replies
     */
    public List<Reply> getReplies(String commentId);

    /**
     * Add a reply
     */
    public String addReply(Reply reply);

    /**
     * Edit a reply
     */
    public String editReply(Reply reply);

    /**
     * Delete a reply
     */
    public String deleteReply(Reply reply);

    /**
     *
     * @param postId
     * @return list of posts' likes
     */
    public List<Like> getPostLikes(String postId);

    /**
     *
     * @param commentId
     * @return list of comments' likes
     */
    public List<Like> getCommentLikes(String commentId);

    /**
     *
     * @param replyId
     * @return list of replies' likes
     */
    public List<Like> getReplyLikes(String replyId);


    /**
     * Add a like
     * @param like
     */
    public String addLike(Like like);

    /**
     * Delete a like
     * @param like
     */
    public String deleteLike(Like like);

}
