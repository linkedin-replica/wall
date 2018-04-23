package com.linkedin.replica.wall.database.handlers;

import com.linkedin.replica.wall.models.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface WallHandler extends DatabaseHandler {

    public List<ReturnedPost> getNewsFeed(String userId,int limit);

    public ArrayList<String> getBookmarks(String userId);

    public boolean addBookmark(String userId, String postId);

    public boolean deleteBookmark(String userId, String postId);

    public List<Post> getPosts(String userID);

    public boolean addPost(Post post);

    public boolean editPost(HashMap<String, Object> args);

    public boolean deletePost(String postId);

    public List<Comment> getComments(String postID);

    public boolean addComment(Comment comment);

    public boolean editComment(HashMap<String, Object> args);

    public boolean deleteComment(String commentId);

    public List<Reply> getReplies(String commentId);

    public Reply getReply(String replyId);

    public boolean addReply(Reply reply);

    public boolean editReply(HashMap<String, Object> args);

    public boolean deleteReply(String replyId);

    public boolean addLikeToPost(String likerId, String postId);

    public boolean addLikeToComment(String likerId, String commentId);

    public boolean addLikeToReply(String likerId, String replyId);

    public boolean deleteLikeFromPost(String likerId, String postId);

    public boolean deleteLikeFromComment(String likerId, String commentId);

    public boolean deleteLikeFromReply(String likerId, String replyId);
    
    public Comment getComment(String commentId);

    public Post getPost(String postId);



    public UserProfile getUser(String userId);

}
