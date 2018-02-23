package com.linkedin.replica.wall.handlers;

import com.linkedin.replica.wall.models.Bookmark;
import com.linkedin.replica.wall.models.Post;

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
    public List<Post> getComments();

    /**
     * Add a comment
     */
    public String addComment();

    /**
     * Edit a comment
     */
    public String editComment();

    /**
     * Delete a comment
     */
    public String deleteComment();


    /**
     *
     * @return list of replies
     */
    public List<Post> getReplies();

    /**
     * Add a reply
     */
    public String addReply();

    /**
     * Edit a reply
     */
    public String editReply();

    /**
     * Delete a reply
     */
    public String deleteReply();

    /**
     *
     * @return list of likes
     */
    public List<Post> getlikes();

    /**
     * Add a like
     */
    public String addLike();

    /**
     * Delete a like
     */
    public String deleteLike();

}
