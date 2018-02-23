package com.linkedin.replica.wall.handlers;

import com.linkedin.replica.wall.models.Bookmark;
import com.linkedin.replica.wall.models.Like;
import com.linkedin.replica.wall.models.Post;

import java.io.IOException;
import java.util.List;

public interface WallHandler {

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
     * @return list of likes on a post
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public List<Like> getPostLikes() throws IOException, ClassNotFoundException;

    /**
     *
     * @return list of likes on a comment
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public List<Like> getCommentLikes() throws IOException, ClassNotFoundException;

    /**
     *
     * @return list of likes on a reply
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public List<Like> getReplyLikes() throws IOException, ClassNotFoundException;


    /**
     * Add a like
     * @param like
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void addLike(Like like) throws IOException, ClassNotFoundException;

    /**
     * Delete a like
     * @param like
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void deleteLike(Like like) throws IOException, ClassNotFoundException;

}
