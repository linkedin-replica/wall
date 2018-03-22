package com.linkedin.replica.wall.database.handlers;

import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.models.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public interface WallHandler extends DatabaseHandler {

    public ArrayList<Bookmark> getBookmarks(String userId);

    public String addBookmark(Bookmark bookmark);

    public String deleteBookmark(Bookmark bookmark);

    public List<Post> getPosts(String userID);

    public String addPost(Post post);

    public String editPost(Post post);

    public String deletePost(Post post);

    public List<Comment> getComments(String postID);

    public String addComment(Comment comment);

    public String editComment(Comment comment);

    public String deleteComment(Comment comment);

    public List<Reply> getReplies(String commentId);

    public Reply getReply(String replyId);

    public String addReply(Reply reply);

    public String editReply(Reply reply);

    public String deleteReply(Reply reply);

    public List<Like> getPostLikes(String postId);

    public List<Like> getCommentLikes(String commentId);

    public List<Like> getReplyLikes(String replyId);

    public String addLike(Like like);

    public String deleteLike(Like like);

    public void getTopPosts() throws ParseException;

    public Comment getComment(String commentId);

    public Post getPost(String postId);

}
