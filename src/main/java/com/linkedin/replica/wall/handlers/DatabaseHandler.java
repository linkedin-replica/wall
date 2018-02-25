package com.linkedin.replica.wall.handlers;

import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.models.*;

import java.util.List;

public interface DatabaseHandler {

    /**
     * @return list of bookmarks
     */
    public List<Bookmark> getBookmarks();

    /**
     * Add a bookmark
     */
    public String addBookmark(Bookmark bookmark);

    /**
     * Delete a bookmark
     */
    public String deleteBookmark(Bookmark bookmark);

    /**
     *
     * @return list of posts
     */
    public List<Post> getPosts();

    /**
     * Add a post
     */
    public String addPost();

    /**
     * Edit a post
     */
    public String editPost();

    /**
     * Delete a post
     */
    public String deletePost();

    /**
     *
     * @return list of comments
     */
    public List<Comment> getComments(String postID);

    /**
     * Add a comment
     */
    public void addComment(Comment comment);

    /**
     * Edit a comment
     */
    public void editComment(Comment comment);

    /**
     * Delete a comment
     */
    public void deleteComment(Comment comment);


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
     * @return list of likes
     */
    public List<Like> getPostLikes(String postId);

    /**
     *
     * @return list of likes
     */
    public List<Like> getCommentLikes(String commentId);

    /**
     *
     * @return list of likes
     */
    public List<Like> getReplyLikes(String replyId);


    /**
     * Add a like
     */
    public String addLike(Like like);

    /**
     * Delete a like
     */
    public String deleteLike(Like like);

}
