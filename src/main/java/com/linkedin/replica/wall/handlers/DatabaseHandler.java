package com.linkedin.replica.wall.handlers;

import com.linkedin.replica.wall.models.Bookmark;
import com.linkedin.replica.wall.models.Like;
import com.linkedin.replica.wall.models.Post;
import com.linkedin.replica.wall.models.Comment;
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
    public List<Post> getPosts(String userID);

    /**
     * Add a post
     */
    public String addPost(Post post) throws IOException, ClassNotFoundException;

    /**
     * Edit a post
     */
    public String editPost(Post post)throws IOException, ClassNotFoundException;

    /**
     * Delete a post
     */
    public String deletePost(Post post)throws IOException, ClassNotFoundException;

    /**
     *
     * @return list of comments
     */
    public List<Comment> getComments(String postID);

    /**
     * Add a comment
     */
    public String addComment(Comment comment) throws IOException, ClassNotFoundException;

    /**
     * Edit a comment
     */
    public String editComment(Comment comment)throws IOException, ClassNotFoundException;

    /**
     * Delete a comment
     */
    public String deleteComment(Comment comment)throws IOException, ClassNotFoundException;


    /**
     *
     * @return list of replies
     */
    public List<Post> getReplies();

    /**
     * Add a reply
     */
    public void addReply();

    /**
     * Edit a reply
     */
    public void editReply();

    /**
     * Delete a reply
     */
    public void deleteReply();

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