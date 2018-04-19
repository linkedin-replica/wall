package com.linkedin.replica.wall.database.handlers;

import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.models.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface WallHandler extends DatabaseHandler {

    public List<Post> getFriendsPosts(UserProfile user,int limit, int offset);

    public ArrayList<Bookmark> getBookmarks(String userId);

    public String addBookmark(Bookmark bookmark);

    public String deleteBookmark(Bookmark bookmark);

    public List<Post> getPosts(String userID);

    public String addPost(Post post);

    public String editPost(HashMap<String, Object> args);

    public String deletePost(Post post);

    public List<Comment> getComments(String postID);

    public String addComment(Comment comment);

    public String editComment(HashMap<String, Object> args);

    public String deleteComment(Comment comment);

    public List<Reply> getReplies(String commentId);

    public Reply getReply(String replyId);

    public String addReply(Reply reply);

    public String editReply(HashMap<String, Object> args);

    public String deleteReply(Reply reply);

    public String addLikeToPost(String likerId, String postId);

    public String addLikeToComment(String likerId, String commentId);

    public String addLikeToReply(String likerId, String replyId);

    public String deleteLikeFromPost(String likerId, String postId);

    public String deleteLikeFromComment(String likerId, String commentId);

    public String deleteLikeFromReply(String likerId, String replyId);

    public void getTopPosts() throws ParseException;

    public Comment getComment(String commentId);

    public Post getPost(String postId);



    public UserProfile getUser(String userId);

}
