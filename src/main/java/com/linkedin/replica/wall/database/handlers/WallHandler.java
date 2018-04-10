package com.linkedin.replica.wall.database.handlers;

import com.linkedin.replica.wall.models.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public interface WallHandler extends DatabaseHandler {

    public List<Post> getFriendsPosts(UserProfile user,int limit, int offset);

    public ArrayList<Bookmark> getBookmarks(String userId);

    public boolean addBookmark(Bookmark bookmark);

    public boolean deleteBookmark(Bookmark bookmark);

    public List<Post> getPosts(String userID);

    public boolean addPost(Post post);

    public boolean editPost(Post post);

    public boolean deletePost(String post);

    public List<Comment> getComments(String postID);

    public boolean addComment(Comment comment);

    public boolean editComment(Comment comment);

    public boolean deleteComment(Comment comment);

    public List<Reply> getReplies(String commentId);

    public Reply getReply(String replyId);

    public boolean addReply(Reply reply);

    public boolean editReply(Reply reply);

    public boolean deleteReply(Reply reply);

    public List<Like> getPostLikes(String postId);

    public List<Like> getCommentLikes(String commentId);

    public List<Like> getReplyLikes(String replyId);

    public boolean addLike(Like like);

    public boolean deleteLike(Like like);

    public void getTopPosts() throws ParseException;

    public Comment getComment(String commentId);

    public Post getPost(String postId);

    public Like getLike(String likeId);

    public UserProfile getUser(String userId);

}
