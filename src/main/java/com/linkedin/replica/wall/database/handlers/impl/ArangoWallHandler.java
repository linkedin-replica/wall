package com.linkedin.replica.wall.database.handlers.impl;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.entity.DocumentUpdateEntity;
import com.arangodb.util.MapBuilder;

import com.linkedin.replica.wall.config.Configuration;
import com.linkedin.replica.wall.database.DatabaseConnection;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.*;


import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ArangoWallHandler implements WallHandler {
    private ArangoDB arangoDB;
    private String dbName;
    String likesCollection;
    String repliesCollection;
    String usersCollection;
    String commentsCollection;
    String postsCollection;


    public ArangoWallHandler() throws IOException, ClassNotFoundException {
        arangoDB = DatabaseConnection.getInstance().getArangodb();
        Configuration config = Configuration.getInstance();
        dbName = config.getArangoConfig("arangodb.name");
        likesCollection = config.getArangoConfig("collections.likes.name");
        repliesCollection = config.getArangoConfig("collections.replies.name");
        usersCollection = config.getArangoConfig("collections.users.name");
        commentsCollection = config.getArangoConfig("collections.comments.name");
        postsCollection = config.getArangoConfig("collections.posts.name");

    }

    public List<Post> getFriendsPosts(UserProfile user,int limit, int offset){
        ArrayList<Post> returnedPosts = new ArrayList<Post>();
        for(int i=0; i<user.getFriendsList().size(); i++){
            returnedPosts.addAll(getPostsWithLimit(user.getFriendsList().get(i),limit,offset));
        }


        Collections.sort(returnedPosts);
        return returnedPosts;
    }

    public List<Post> getPostsWithLimit(String userID,int limit, int offset) {
        ArrayList<Post> posts = new ArrayList<Post>();
        try {
            String query = "FOR l IN " + postsCollection + " FILTER l.authorId == @authorId SORT l.timestamp DESC\n" +
                    "  LIMIT "+offset+" , "+limit+" RETURN l";
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

    /**
     * method to update user's bookmarks list by adding new bookmark.
     *
     * @param bookmark to be added.
     * @return message tells whether the process is successful or failed.
     */
    public String addBookmark(Bookmark bookmark) {
        String userId = bookmark.getUserId();
        String postId = bookmark.getPostId();
        String message = "";
        if(postId != null && getPost(postId) != null) {
            try {
                UserProfile user = arangoDB.db(dbName).collection(usersCollection).getDocument(userId, UserProfile.class);

                ArrayList<Bookmark> bookmarkList = user.getBookmarks();

                bookmarkList.add(bookmark);
                user.setBookmarks(bookmarkList);
                arangoDB.db(dbName).collection(usersCollection).updateDocument(userId, user);

                message = "Success to add bookmark";

            } catch (ArangoDBException e) {
                message = "Failed to add bookmark. " + e.getMessage();
            }
        }else
            message = "Failed to add bookmark No post found";
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

    /**
     * function to get posts of specific user.
     * @param userID
     * @return
     */
    public List<Post> getPosts(String userID) {
        ArrayList<Post> posts = new ArrayList<Post>();
        try {
            String query = "FOR l IN " + postsCollection + " FILTER l.authorId == @authorId RETURN l";
            Map<String, Object> bindVars = new MapBuilder().put("authorId", userID).get();
            ArangoCursor<Post> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    Post.class);
            cursor.forEachRemaining(postDocument -> {

                posts.add(postDocument);
            });
        } catch (ArangoDBException e) {
            System.err.println("Failed to get posts." + e.getMessage());
        }
        return posts;
    }

    /**
     * function to get specific post in database.
     * @param postId
     * @return
     */
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

    public UserProfile getUser(String userId) {
        UserProfile user = null;
        try {
            user = arangoDB.db(dbName).collection(usersCollection).getDocument(userId,
                    UserProfile.class);
        } catch (ArangoDBException e) {
            System.err.println("Failed to get user: userId; " + e.getMessage());
        }
        return user;
    }

    /**
     * function to add post in database.
     * @param post
     * @return
     */
    public String addPost(Post post) {
            String response = "";
            try {
                DocumentCreateEntity addDoc =  arangoDB.db(dbName).collection(postsCollection).insertDocument(post);
                response = "Post Created";
            }catch (ArangoDBException e){
                response = "Failed to add Post " + e.getMessage();
            }

        return response;
    }

    /**
     *
     * @param args
     * @return
     */
    public String editPost(HashMap<String, Object> args) {

        String response = "";
        boolean isString = false;
        Class postClass = Post.class;
        Field[] postFields = postClass.getDeclaredFields();
        try {
            String query = "FOR p IN " + postsCollection + " FILTER p._key == @key UPDATE p with {";
            for (String key : args.keySet()) {
                if(!key.equals("postId") && !key.equals("authorId")){
                    query += key + ":";
                    for (int i = 0; i<postFields.length; i++) {
                        if (key.equals(postFields[i].getName()) && String.class.isAssignableFrom(postFields[i].getType())){
                            query += "\"" + args.get(key) + "\" ";
                            isString = true;
                            break;
                        }
                    }
                    if(!isString){
                        query += args.get(key);
                        isString = false;
                    }
                    query+=",";
                }
            }
            query = query.substring(0,query.length()-1);
            query += "} IN " + postsCollection;
            Map<String, Object> bindVars = new MapBuilder().put("key",args.get("postId").toString()).get();
            arangoDB.db(dbName).query(query, bindVars, null, Post.class);
            response = "Post Updated";
        } catch (ArangoDBException e){
            response = "Failed to Update Post " + e.getMessage();
        }
        return response;
    }

    /**
     * function to delete specific post from database.
     * @param post
     * @return
     */
    public String deletePost(Post post) {
        String response;
        try {
            arangoDB.db(dbName).collection(postsCollection).deleteDocument(post.getPostId());
            response = "Post Deleted";
        } catch (ArangoDBException e){
            response = "Failed to Delete Post " + e.getMessage();
        }

        return response;
    }


    /**
     * function to get list of comments on specific post.
     * @param postId
     * @return
     */
    public List<Comment> getComments(String postId) {
        ArrayList<Comment> comments = new ArrayList<Comment>();
        try {
            String query = "FOR l IN " + commentsCollection + " FILTER l.parentPostId == @parentPostId RETURN l";
            Map<String, Object> bindVars = new MapBuilder().put("parentPostId", postId).get();
            ArangoCursor<Comment> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    Comment.class);
            cursor.forEachRemaining(commentDocument -> {
                comments.add(commentDocument);
            });
        } catch (ArangoDBException e) {
            throw e;
        }
        return comments;
    }

    /**
     * function to get specifc comment.
     * @param commentId
     * @return
     */
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

    /**
     * function to add comment in database and update post collection.
     * @param comment
     * @return
     */
    public String addComment(Comment comment) {
        String response = "";
        String postId = comment.getParentPostId();
        if(postId != null && getPost(postId) != null) {

            try {
                DocumentCreateEntity commentDoc = arangoDB.db(dbName).collection(commentsCollection).insertDocument(comment);
                System.out.println("Comment added");
                response = "Comment added" + "," + commentDoc.getKey();
            } catch (ArangoDBException e) {
                System.err.println("Failed to add a comment. " + e.getMessage());
                response = "Failed to add a comment. " + e.getMessage();
            }
            Post post = getPost(comment.getParentPostId());
            post.setCommentsCount(post.getCommentsCount() + 1);
            HashMap<String, Object> editArgs = new HashMap<String, Object>();
            editArgs.put("postId", post.getPostId());
            editArgs.put("commentsCount", post.getCommentsCount());
            editPost(editArgs);

        }else {
            response = "Failed to add a comment missing post found.";
        }

        return response;
    }

    /**
     *
     * @param args
     * @return
     */
    public String editComment(HashMap<String, Object> args) {
        String response = "";
        boolean isString = false;
        Class commentClass = Comment.class;
        Field[] commentFields = commentClass.getDeclaredFields();
        try {
            String query = "FOR c IN " + commentsCollection + " FILTER c._key == @key UPDATE c with {";
            for (String key : args.keySet()) {
                if(!key.equals("commentId")){
                    query += key + ":";
                    for (int i = 0; i<commentFields.length; i++) {
                        if (key.equals(commentFields[i].getName()) && String.class.isAssignableFrom(commentFields[i].getType())){
                            query += "\"" + args.get(key) + "\" ";
                            isString = true;
                            break;
                        }
                    }
                    if(!isString){
                        query += args.get(key);
                        isString = false;
                    }
                    query+=",";
                }
            }
            query = query.substring(0,query.length()-1);
            query += "} IN " + commentsCollection;
            Map<String, Object> bindVars = new MapBuilder().put("key",args.get("commentId").toString()).get();
            arangoDB.db(dbName).query(query, bindVars, null, Comment.class);
            response = "Comment Updated";
        } catch (ArangoDBException e){
            response = "Failed to update comment. " + e.getMessage();
        }
        return response;
    }

    /**
     * function to delete specific comment in the database.
     * @param comment
     * @return
     */
    public String deleteComment(Comment comment) {
        String response = "";
        try {
            arangoDB.db(dbName).collection(commentsCollection).deleteDocument(comment.getCommentId());
            response = "Comment deleted";
        } catch (ArangoDBException e) {
            response = "Failed to delete a comment. " + e.getMessage();
        }

        if(comment.getParentPostId() != null){
            Post post = getPost(comment.getParentPostId());
            if(post !=null){
                post.setCommentsCount(post.getCommentsCount() - 1);
                HashMap<String, Object> editArgs = new HashMap<String, Object>();
                editArgs.put("postId", post.getPostId());
                editArgs.put("commentsCount", post.getCommentsCount());
                editPost(editArgs);
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
            });
        } catch (ArangoDBException e) {
            throw e;
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

    /**
     * function to add reply on comment.
     * @param reply
     * @return
     */
    public String addReply(Reply reply) {
        String response = "";
        String postId = reply.getParentPostId();
        String commentId = reply.getParentCommentId();
        if((postId != null && getPost(postId) != null) && (commentId != null && getComment(commentId ) != null)) {
            try {
                arangoDB.db(dbName).collection(repliesCollection).insertDocument(reply);
                response = "Reply created";
            } catch (ArangoDBException e) {
                System.err.println("Failed to add reply. " + e.getMessage());
                response = "Failed to add reply. " + e.getMessage();
            }
            Comment comment = getComment(reply.getParentCommentId());
            comment.setRepliesCount(comment.getRepliesCount() + 1);
            HashMap<String, Object> editCommentArgs = new HashMap<String, Object>();
            editCommentArgs.put("commentId", comment.getCommentId());
            editCommentArgs.put("repliesCount", comment.getRepliesCount());
            editComment(editCommentArgs);

            Post post = getPost(reply.getParentPostId());
            post.setCommentsCount(post.getCommentsCount() + 1);
            HashMap<String, Object> editPostArgs = new HashMap<String, Object>();
            editPostArgs.put("postId", post.getPostId());
            editPostArgs.put("commentsCount", post.getCommentsCount());
            editPost(editPostArgs);
        }
        return response;

    }

    /**
     *
     * @param args
     * @return
     */
    public String editReply(HashMap<String, Object> args) {

        String response = "";
        boolean isString = false;
        Class replyClass = Reply.class;
        Field[] replyFields = replyClass.getDeclaredFields();
        try {
            String query = "FOR r IN " + repliesCollection + " FILTER r._key == @key UPDATE r with {";
            for (String key : args.keySet()) {
                if(!key.equals("replyId")){
                    query += key + ":";
                    for (int i = 0; i<replyFields.length; i++) {
                        if (key.equals(replyFields[i].getName()) && String.class.isAssignableFrom(replyFields[i].getType())){
                            query += "\"" + args.get(key) + "\" ";
                            isString = true;
                            break;
                        }
                    }
                    if(!isString){
                        query += args.get(key);
                        isString = false;
                    }
                    query+=",";
                }
            }
            query = query.substring(0,query.length()-1);
            query += "} IN " + repliesCollection;
            Map<String, Object> bindVars = new MapBuilder().put("key",args.get("replyId").toString()).get();
            arangoDB.db(dbName).query(query, bindVars, null, Reply.class);
            response = "Reply updated";
        } catch (ArangoDBException e){
            response = "Failed to update reply. " + e.getMessage();
        }

        return response;
    }

    /**
     * function to delete specific reply.
     * @param reply
     * @return
     */
    public String deleteReply(Reply reply) {
        String response = "";
        try {
            arangoDB.db(dbName).collection(repliesCollection).deleteDocument(reply.getReplyId());
            response = "Reply deleted";
        } catch (ArangoDBException e) {
            response = "Failed to delete reply. " + e.getMessage();
        }
        Comment comment = getComment(reply.getParentCommentId());
        if (comment != null) {
            comment.setRepliesCount(comment.getRepliesCount() - 1);
            HashMap<String, Object> editCommentArgs = new HashMap<String, Object>();
            editCommentArgs.put("commentId", comment.getCommentId());
            editCommentArgs.put("repliesCount", comment.getRepliesCount());
            editComment(editCommentArgs);
        } else {
            response = "Failed to update comment's reply count. ";
        }
        Post post = getPost(reply.getParentPostId());
        if(post != null){
            post.setCommentsCount(post.getCommentsCount() + 1);
            HashMap<String, Object> editPostArgs = new HashMap<String, Object>();
            editPostArgs.put("postId", post.getPostId());
            editPostArgs.put("commentsCount", post.getCommentsCount());
            editPost(editPostArgs);
        }else
        {
            response = "Failed to update post's comment count";
        }
        return response;

    }

    /**
     * get specific like from like collection.
     * @param likeId
     * @return
     */
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

    /**
     * get likes on specific likes.
     * @param postId
     * @return
     */
    public List<Like> getPostLikes(String postId) {
        ArrayList<Like> likes = new ArrayList<Like>();
        try {
            String query = "FOR l IN " + likesCollection + " FILTER l.likedPostId == @postId RETURN l";
            Map<String, Object> bindVars = new MapBuilder().put("postId", postId).get();
            ArangoCursor<Like> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    Like.class);
            cursor.forEachRemaining(likeDocument -> {
                likes.add(likeDocument);
            });
        } catch (ArangoDBException e) {
            throw e;
        }
        return likes;

    }

    /**
     * function to get likes on comment.
     * @param commentId
     * @return
     */
    public List<Like> getCommentLikes(String commentId) {
        ArrayList<Like> likes = new ArrayList<Like>();
        try {
            String query = "FOR l IN " + likesCollection + " FILTER l.likedCommentId == @commentId RETURN l";
            Map<String, Object> bindVars = new MapBuilder().put("commentId", commentId).get();
            ArangoCursor<Like> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    Like.class);
            cursor.forEachRemaining(likeDocument -> {
                likes.add(likeDocument);
            });
        } catch (ArangoDBException e) {
            throw e;
        }
        return likes;
    }

    /**
     * function to get likes on specific reply.
     * @param replyId
     * @return
     */
    public List<Like> getReplyLikes(String replyId) {
        ArrayList<Like> likes = new ArrayList<Like>();
        try {
            String query = "FOR l IN " + likesCollection + " FILTER l.likedReplyId == @replyId RETURN l";
            Map<String, Object> bindVars = new MapBuilder().put("replyId", replyId).get();
            ArangoCursor<Like> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    Like.class);
            cursor.forEachRemaining(likeDocument -> {
                likes.add(likeDocument);
            });
        } catch (ArangoDBException e) {
            throw e;
        }
        return likes;
    }

    /**
     * function to add like on post/ like/ reply.
     * @param like
     * @return
     */
    public String addLike(Like like) {
        String response = "";
        String commentId = like.getLikedCommentId();
        String replyId = like.getLikedReplyId();
        String postId = like.getLikedPostId();
        if((postId!= null && getPost(postId) != null) || (commentId != null && getComment(commentId) != null) || (replyId!= null && getReply(replyId) != null)) {
            try {
                DocumentCreateEntity likeDoc = arangoDB.db(dbName).collection(likesCollection).insertDocument(like);
                response = "Like added";
            } catch (ArangoDBException e) {
                response = "Failed to add a like. " + e.getMessage();
            }

            if (like.getLikedPostId() != null) {
                Post post = getPost(like.getLikedPostId());
                if (post != null) {
                   // post.setLikesCount(post.getLikesCount() + 1);
                    HashMap<String, Object> editPostArgs = new HashMap<String, Object>();
                    editPostArgs.put("postId", post.getPostId());
                   // editPostArgs.put("likesCount", post.getLikesCount());
                    editPost(editPostArgs);
                } else {
                    response = "Failed to update post's like count. ";
                }
            } else if (like.getLikedCommentId() != null) {
                Comment comment = getComment(like.getLikedCommentId());
                if (comment != null) {
                    comment.setLikesCount(comment.getLikesCount() + 1);
                    HashMap<String, Object> editCommentArgs = new HashMap<String, Object>();
                    editCommentArgs.put("commentId", comment.getCommentId());
                    editCommentArgs.put("likesCount", comment.getLikesCount());
                    editComment(editCommentArgs);
                } else {
                    response = "Failed to update comment's like count. ";
                }

            } else if (like.getLikedReplyId() != null) {
                Reply reply = getReply(like.getLikedReplyId());
                if (reply != null) {
                    reply.setLikesCount(reply.getLikesCount() + 1);
                    HashMap<String, Object> editReplyArgs = new HashMap<String, Object>();
                    editReplyArgs.put("replyId", reply.getReplyId());
                    editReplyArgs.put("likesCount", reply.getLikesCount());
                    editReply(editReplyArgs);

                } else {
                    response = "Failed to update reply's like count. ";
                }

            }
        }
        return response;


    }

    /**
     * function to unlike post/ comment/ reply.
     * @param like
     * @return
     */
    public String deleteLike(Like like) {
        String response = "";
        try {
            arangoDB.db(dbName).collection(likesCollection).deleteDocument(like.getLikeId());
            response = "Like deleted";
        } catch (ArangoDBException e) {
            response = "Failed to delete a like. " + e.getMessage();
        }
        if(like.getLikedPostId() != null){
            Post post = getPost(like.getLikedPostId());
            if(post !=null){
               // post.setLikesCount(post.getLikesCount() - 1);
                HashMap<String, Object> editPostArgs = new HashMap<String, Object>();
                editPostArgs.put("postId", post.getPostId());
                //editPostArgs.put("likesCount", post.getLikesCount());
                editPost(editPostArgs);
            }
            else {
                response = "Failed to update post's like count. ";
            }
        } else if (like.getLikedCommentId() != null) {
            Comment comment = getComment(like.getLikedCommentId());
            if (comment != null) {
                comment.setLikesCount(comment.getLikesCount() - 1);
                HashMap<String, Object> editCommentArgs = new HashMap<String, Object>();
                editCommentArgs.put("commentId", comment.getCommentId());
                editCommentArgs.put("likesCount", comment.getLikesCount());
                editComment(editCommentArgs);
            } else {
                response = "Failed to update comment's like count. ";
            }

        } else if (like.getLikedReplyId() != null) {
            Reply reply = getReply(like.getLikedReplyId());
            if (reply != null) {
                reply.setLikesCount(reply.getLikesCount() - 1);
                HashMap<String, Object> editReplyArgs = new HashMap<String, Object>();
                editReplyArgs.put("replyId", reply.getReplyId());
                editReplyArgs.put("likesCount", reply.getLikesCount());
                editReply(editReplyArgs);
            } else {
                response = "Failed to update reply's like count. ";
            }

        }
        return response;
    }

    /**
     * function to get the top posts.
     * @throws ParseException
     */
    public void getTopPosts() throws ParseException {
        try {
            String query = "FOR p IN " + postsCollection + " RETURN p";
            ArangoCursor<Post> cursor = arangoDB.db(dbName).query(query, null, null,
                    Post.class);
            cursor.forEachRemaining(postDocument -> {
            });
        } catch (ArangoDBException e) {
            System.err.println("Failed to get top posts " + e.getMessage());
        }



        DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd yyyy hh:mm a");
        Date postDate = dateFormat.parse("Mon Mar 19 2018 01:00 PM");
        Date currentDate = new Date();
        float diffInDays = (currentDate.getTime()-postDate.getTime())/(1000*60*60*24);
    }

}
