package com.linkedin.replica.wall.handlers.impl;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.entity.DocumentUpdateEntity;
import com.arangodb.util.MapBuilder;

import com.linkedin.replica.wall.config.DatabaseConnection;
import com.linkedin.replica.wall.handlers.DatabaseHandler;
import com.linkedin.replica.wall.models.Bookmark;
import com.linkedin.replica.wall.models.Like;
import com.linkedin.replica.wall.models.Comment;
import com.linkedin.replica.wall.models.Post;
import com.linkedin.replica.wall.models.Reply;
import com.linkedin.replica.wall.models.UserProfile;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ArangoWallHandler implements DatabaseHandler {
    private ArangoDB arangoDB;
    private Properties properties;
    private String dbName;
    String likesCollection;
    String repliesCollection;
    String usersCollection;
    String commentsCollection;
    String postsCollection;


    public ArangoWallHandler() throws IOException, ClassNotFoundException {
        arangoDB = DatabaseConnection.getInstance().getArangodb();
        properties = new Properties();
        properties.load(new FileInputStream("db_config"));
        dbName = properties.getProperty("arangodb.name");
        likesCollection = properties.getProperty("collections.likes.name");
        repliesCollection = properties.getProperty("collections.replies.name");
        usersCollection = properties.getProperty("collections.users.name");
        commentsCollection = properties.getProperty("collections.comments.name");
        postsCollection = properties.getProperty("collections.posts.name");

    }

    /**
     * method to update user's bookmarks list by adding new bookmark.
     *
     * @param bookmark to be added.
     * @return message tells whether the process is successful or failed.
     */
    public String addBookmark(Bookmark bookmark) {
        String userId = bookmark.getUserId();
        String message = "";
        try {
            UserProfile user = arangoDB.db(dbName).collection(usersCollection).getDocument(userId, UserProfile.class);

            ArrayList<Bookmark> bookmarkList = user.getBookmarks();

             bookmarkList.add(bookmark);
             user.setBookmarks(bookmarkList);
            arangoDB.db(dbName).collection(usersCollection).updateDocument(userId, user);

            message = "Success to add bookmark";

        } catch (ArangoDBException e) {
            System.err.println("Failed to add bookmark. " + e.getMessage());
            message = "Failed to add bookmark. " + e.getMessage();
        }
        return message;
    }

    /**
     * method to update user's bookmarks list by deleting new bookmark.
     * @param bookmark to be deleted
     * @return message tells whether the process is successful or failed.
     */
    public String deleteBookmark(Bookmark bookmark) {
        String userId = bookmark.getUserId();
        String message = "";
        try {
            UserProfile user = arangoDB.db(dbName).collection(usersCollection).getDocument(userId, UserProfile.class);
            ArrayList<Bookmark> bookmarkList = user.getBookmarks();
            bookmarkList.remove(bookmark);
            user.setBookmarks(bookmarkList);
            arangoDB.db(dbName).collection(usersCollection).updateDocument(userId, user);
            message = "Success to delete bookmark";

        } catch (ArangoDBException e) {
            System.err.println("Failed to delete bookmark. " + e.getMessage());
            message = "Failed to delete bookmark. " + e.getMessage();
        }
        return message;
    }

    /**
     * method to get user's bookmarks.
     * @param userId
     * @return list of users bookmarks.
     */

    public ArrayList<Bookmark> getBookmarks(String userId) {
         ArrayList<Bookmark> ans = new ArrayList<>();
        String message = "";
        try {
            UserProfile user = arangoDB.db(dbName).collection(usersCollection).getDocument(userId, UserProfile.class);
            ans = user.getBookmarks();
        } catch (ArangoDBException e) {
            System.err.println("Failed to get user's bookmarks " + e.getMessage());
        }
        return ans;

    }

    public List<Post> getPosts(String userID) {
        ArrayList<Post> posts = new ArrayList<Post>();
        try {
            String query = "FOR l IN " + postsCollection + " FILTER l.authorId == @authorId RETURN l";
            Map<String, Object> bindVars = new MapBuilder().put("authorId", userID).get();
            ArangoCursor<Post> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    Post.class);
            cursor.forEachRemaining(postDocument -> {

                posts.add(postDocument);
                System.out.println("Key: " + postDocument.getPostId());
            });
        } catch (ArangoDBException e) {
            System.err.println("Failed to get posts." + e.getMessage());
        }
        return posts;
    }

    public Post getPost(String postId) {
        Post post = null;
        try {
            post = arangoDB.db(dbName).collection(postsCollection).getDocument(postId,
                    Post.class);
        } catch (ArangoDBException e) {
            System.err.println("Failed to get post: postId; " + e.getMessage());
        }
        return post;
    }

    public String addPost(Post post) {
            String response = "";
            try {
                DocumentCreateEntity addDoc =  arangoDB.db(dbName).collection(postsCollection).insertDocument(post);
                System.out.println("Post Created");
                response = "Post Created";
            }catch (ArangoDBException e){
                System.err.println("Failed to add Post " + e.getMessage());
                response = "Failed to add Post " + e.getMessage();
            }

        return response;
    }

    public String editPost(Post post) {
        String response = "";
        try{
            DocumentUpdateEntity editPost =  arangoDB.db(dbName).collection(postsCollection).updateDocument(post.getPostId() , post);
            System.out.println("Post Updated");
            response = "Post Updated";
        } catch (ArangoDBException e){
            System.err.println("Failed to Update Post " + e.getMessage());
            response = "Failed to Update Post " + e.getMessage();
        }

        return response;
    }

    public String deletePost(Post post) {
        String response;
        try {
            arangoDB.db(dbName).collection(postsCollection).deleteDocument(post.getPostId());
            System.out.println("Post Deleted");
            response = "Post Deleted";
        } catch (ArangoDBException e){
            System.err.println("Failed to Delete Post " + e.getMessage());
            response = "Failed to Delete Post " + e.getMessage();
        }

        return response;
    }




    public List<Comment> getComments(String postId) {
        ArrayList<Comment> comments = new ArrayList<Comment>();
        try {
            String query = "FOR l IN " + commentsCollection + " FILTER l.parentPostId == @parentPostId RETURN l";
            Map<String, Object> bindVars = new MapBuilder().put("parentPostId", postId).get();
            ArangoCursor<Comment> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    Comment.class);
            cursor.forEachRemaining(commentDocument -> {
                comments.add(commentDocument);
                System.out.println("Key: " + commentDocument.getCommentId());
            });
        } catch (ArangoDBException e) {
            System.err.println("Failed to get posts' comments." + e.getMessage());
        }
        return comments;
    }

    public Comment getComment(String commentId) {
        Comment comment = null;
        try {
            comment = arangoDB.db(dbName).collection(commentsCollection).getDocument(commentId,
                    Comment.class);
        } catch (ArangoDBException e) {
            System.err.println("Failed to get comment: commentId; " + e.getMessage());
        }
        return comment;
    }

    public String addComment(Comment comment) {
        String response = "";
        if(comment.getParentPostId() != null && getPost(comment.getParentPostId()) != null) {
//            Post post = getPost(comment.getParentPostId());
//            post.setCommentsCount(post.getCommentsCount() + 1);
//            editPost(post);
            try {
                DocumentCreateEntity commentDoc = arangoDB.db(dbName).collection(commentsCollection).insertDocument(comment);
                System.out.println("Comment added");
                response = "Comment added" + "," + commentDoc.getKey();
            } catch (ArangoDBException e) {
                System.err.println("Failed to add a comment. " + e.getMessage());
                response = "Failed to add a comment. " + e.getMessage();
            }

        }else {
            response = "Failed to add a comment missing post found.";
        }

        return response;
    }

    public String editComment(Comment comment) {
        String response = "";
        try {
            DocumentUpdateEntity commentDoc =  arangoDB.db(dbName).collection(commentsCollection).updateDocument(comment.getCommentId(),comment);

        } catch (ArangoDBException e) {
            System.err.println("Failed to update comment. " + e.getMessage());
            response = "Failed to update comment. " + e.getMessage();
        }
        return response;
    }

    public String deleteComment(Comment comment) {
        String response = "";
        try {
            arangoDB.db(dbName).collection(commentsCollection).deleteDocument(comment.getCommentId());
        } catch (ArangoDBException e) {
            System.err.println("Failed to delete a comment. " + e.getMessage());
            response = "Failed to delete a comment. " + e.getMessage();
        }

        if(comment.getParentPostId() != null){
            Post post = getPost(comment.getParentPostId());
            if(post !=null){
                post.setCommentsCount(post.getCommentsCount() - 1);
                editPost(post);
            }
            else {
                response = "Failed to update post's comments count. ";
            }


        }
        return response;
    }

    public List<Reply> getReplies(String commentId) {
        ArrayList<Reply> replies = new ArrayList<Reply>();
        try {
            String query = "FOR r IN " + repliesCollection + " FILTER r.parentCommentId == @parentCommentId RETURN r";
            Map<String, Object> bindVars = new MapBuilder().put("parentCommentId", commentId).get();
            ArangoCursor<Reply> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    Reply.class);
            cursor.forEachRemaining(replyDocument -> {
                replies.add(replyDocument);
                System.out.println("Key: " + replyDocument.getReplyId());
            });
        } catch (ArangoDBException e) {
            System.err.println("Failed to get replies. " + e.getMessage());
        }
        return replies;
    }

    public Reply getReply(String replyId) {
        Reply reply = null;
        try {
            Reply replyDocument = arangoDB.db(dbName).collection(repliesCollection).getDocument(replyId,
                    Reply.class);
            reply = replyDocument;
            if(reply!=null)
                System.out.println("Key: " + replyDocument.getReplyId());

        } catch (ArangoDBException e) {
            System.err.println("Failed to get reply: replyId; " + e.getMessage());
        }
        return reply;
    }

    public String addReply(Reply reply) {
        String response = "";
        try {
            arangoDB.db(dbName).collection(repliesCollection).insertDocument(reply);
            response = "Reply created";
        } catch (ArangoDBException e) {
            System.err.println("Failed to add reply. " + e.getMessage());
            response = "Failed to add reply. " + e.getMessage();
        }
        Comment comment = getComment(reply.getParentCommentId());
        if (comment != null) {
            comment.setRepliesCount(comment.getRepliesCount() + 1);
            editComment(comment);
        } else {
            response = "Failed to update comment's reply count. ";
        }
        //Todo:
        // 1. get post: call getPost()
        // 2. update post object: add 1 to commentsCount
        // 3. update post document: call editPosts()

        return response;

    }


    public String editReply(Reply reply) {
        String response = "";
        try {
            arangoDB.db(dbName).collection(repliesCollection).updateDocument(reply.getReplyId() ,reply);
        } catch (ArangoDBException e) {
            System.err.println("Failed to update reply. " + e.getMessage());
            response = "Failed to update reply. " + e.getMessage();
        }
        return response;
    }

    public String deleteReply(Reply reply) {
        String response = "";
        try {
            arangoDB.db(dbName).collection(repliesCollection).deleteDocument(reply.getReplyId());
        } catch (ArangoDBException e) {
            System.err.println("Failed to delete reply. " + e.getMessage());
            response = "Failed to delete reply. " + e.getMessage();
        }
        Comment comment = getComment(reply.getParentCommentId());
        if (comment != null) {
            comment.setRepliesCount(comment.getRepliesCount() - 1);
            editComment(comment);
        } else {
            response = "Failed to update comment's reply count. ";
        }
        //Todo:
        // 1. get post: call getPost()
        // 2. update post object: add 1 to commentsCount
        // 3. update post document: call editPosts()
        return response;

    }

    public Like getLike(String likeId) {
        Like like = null;
        try {
            like = arangoDB.db(dbName).collection(likesCollection).getDocument(likeId,
                    Like.class);
        } catch (ArangoDBException e) {
            System.err.println("Failed to get like: likeId; " + e.getMessage());
        }
        return like;
    }

    public List<Like> getPostLikes(String postId) {
        ArrayList<Like> likes = new ArrayList<Like>();
        try {
            String query = "FOR l IN " + likesCollection + " FILTER l.likedPostId == @postId RETURN l";
            Map<String, Object> bindVars = new MapBuilder().put("postId", postId).get();
            ArangoCursor<Like> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    Like.class);
            cursor.forEachRemaining(likeDocument -> {
                likes.add(likeDocument);
                System.out.println("Key: " + likeDocument.getLikeId());
            });
        } catch (ArangoDBException e) {
            System.err.println("Failed to get posts' likes." + e.getMessage());
        }
        return likes;

    }

    public List<Like> getCommentLikes(String commentId) {
        ArrayList<Like> likes = new ArrayList<Like>();
        try {
            String query = "FOR l IN " + likesCollection + " FILTER l.likedCommentId == @commentId RETURN l";
            Map<String, Object> bindVars = new MapBuilder().put("commentId", commentId).get();
            ArangoCursor<Like> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    Like.class);
            cursor.forEachRemaining(likeDocument -> {
                likes.add(likeDocument);
                System.out.println("Key: " + likeDocument.getLikeId());
            });
        } catch (ArangoDBException e) {
            System.err.println("Failed to get comments' likes." + e.getMessage());
        }
        return likes;
    }

    public List<Like> getReplyLikes(String replyId) {
        ArrayList<Like> likes = new ArrayList<Like>();
        try {
            String query = "FOR l IN " + likesCollection + " FILTER l.likedReplyId == @replyId RETURN l";
            Map<String, Object> bindVars = new MapBuilder().put("replyId", replyId).get();
            ArangoCursor<Like> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    Like.class);
            cursor.forEachRemaining(likeDocument -> {
                likes.add(likeDocument);
                System.out.println("Key: " + likeDocument.getLikeId());
            });
        } catch (ArangoDBException e) {
            System.err.println("Failed to get replies' likes." + e.getMessage());
        }
        return likes;
    }

    public String addLike(Like like) {
        String response = "";
        try {
            DocumentCreateEntity likeDoc =  arangoDB.db(dbName).collection(likesCollection).insertDocument(like);
            System.out.println("Like added");
            response = "Like added" + "," + likeDoc.getKey();
        } catch (ArangoDBException e) {
            System.err.println("Failed to add a like. " + e.getMessage());
            response = "Failed to add a like. " + e.getMessage();
        }

        if(like.getLikedPostId() != null){
            Post post = getPost(like.getLikedPostId());
            if(post !=null){
                post.setLikesCount(post.getLikesCount() + 1);
                editPost(post);
            }
            else {
                response = "Failed to update post's like count. ";
            }
        } else if (like.getLikedCommentId() != null) {
            Comment comment = getComment(like.getLikedCommentId());
            if (comment != null) {
                comment.setLikesCount(comment.getLikesCount() + 1);
                editComment(comment);
            } else {
                response = "Failed to update comment's like count. ";
            }

        } else if (like.getLikedReplyId() != null) {
            Reply reply = getReply(like.getLikedReplyId());
            if (reply != null) {
                reply.setLikesCount(reply.getLikesCount() + 1);
                editReply(reply);
            } else {
                response = "Failed to update reply's like count. ";
            }

        }
        return response;


    }

    public String deleteLike(Like like) {
        String response = "";
        try {
            arangoDB.db(dbName).collection(likesCollection).deleteDocument(like.getLikeId());
        } catch (ArangoDBException e) {
            System.err.println("Failed to delete a like. " + e.getMessage());
            response = "Failed to delete a like. " + e.getMessage();
        }
        if(like.getLikedPostId() != null){
            Post post = getPost(like.getLikedPostId());
            if(post !=null){
                post.setLikesCount(post.getLikesCount() - 1);
                editPost(post);
            }
            else {
                response = "Failed to update post's like count. ";
            }
        } else if (like.getLikedCommentId() != null) {
            Comment comment = getComment(like.getLikedCommentId());
            if (comment != null) {
                comment.setLikesCount(comment.getLikesCount() - 1);
                editComment(comment);
            } else {
                response = "Failed to update comment's like count. ";
            }

        } else if (like.getLikedReplyId() != null) {
            Reply reply = getReply(like.getLikedReplyId());
            if (reply != null) {
                reply.setLikesCount(reply.getLikesCount() - 1);
                editReply(reply);
            } else {
                response = "Failed to update reply's like count. ";
            }

        }
        return response;
    }

    public void getTopPosts() throws ParseException {
        try {
            String query = "FOR p IN " + postsCollection + " RETURN p";
            ArangoCursor<Post> cursor = arangoDB.db(dbName).query(query, null, null,
                    Post.class);
            cursor.forEachRemaining(postDocument -> {
                System.out.println("Key: " + postDocument.getTimeStamp());
            });
        } catch (ArangoDBException e) {
            System.err.println("Failed to get top posts " + e.getMessage());
        }



        DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd yyyy hh:mm a");
        Date postDate = dateFormat.parse("Mon Mar 19 2018 01:00 PM");
        Date currentDate = new Date();
        float diffInDays = (currentDate.getTime()-postDate.getTime())/(1000*60*60*24);
        System.out.println("date is " + diffInDays);
    }

}
