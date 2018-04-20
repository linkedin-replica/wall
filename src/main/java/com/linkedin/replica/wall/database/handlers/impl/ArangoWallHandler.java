package com.linkedin.replica.wall.database.handlers.impl;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.util.MapBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.linkedin.replica.wall.config.Configuration;
import com.linkedin.replica.wall.database.DatabaseConnection;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.exceptions.WallException;
import com.linkedin.replica.wall.models.*;
import java.io.IOException;
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
            });
        } catch (ArangoDBException e) {
            e.printStackTrace();
        }
        return posts;
    }

    /**
     * method to update user's bookmarks list by adding new bookmark.
     *
     * @param bookmark to be added.
     * @return message tells whether the process is successful or failed.
     */
    public boolean addBookmark(Bookmark bookmark) throws ArangoDBException {
        String userId = bookmark.getUserId();
        String postId = bookmark.getPostId();
        boolean message = false;
        if(postId != null && getPost(postId) != null) {

                UserProfile user = arangoDB.db(dbName).collection(usersCollection).getDocument(userId, UserProfile.class);

                ArrayList<Bookmark> bookmarkList = user.getBookmarks();

                bookmarkList.add(bookmark);
                arangoDB.db(dbName).collection(usersCollection).updateDocument(userId, user);

                message = true;

        }else
           throw  new WallException("Failed to add bookmark No post found");
        return message;
    }

    /**
     * method to update user's bookmarks list by deleting new bookmark.
     * @param bookmark to be deleted
     * @return message tells whether the process is successful or failed.
     */
    public boolean deleteBookmark(Bookmark bookmark) throws ArangoDBException {

        String userId = bookmark.getUserId();
        boolean message = false;

        UserProfile user = arangoDB.db(dbName).collection(usersCollection).getDocument(userId, UserProfile.class);
        ArrayList<Bookmark> bookmarkList = user.getBookmarks();
        bookmarkList.remove(bookmark);
        arangoDB.db(dbName).collection(usersCollection).updateDocument(userId, user);
        message = true ;

        return message;
    }

    /**
     * method to get user's bookmarks.
     * @param userId
     * @return list of users bookmarks.
     */

    public ArrayList<Bookmark> getBookmarks(String userId) throws ArangoDBException {
        ArrayList<Bookmark> ans = new ArrayList<>();
        String message = "";
        UserProfile user = arangoDB.db(dbName).collection(usersCollection).getDocument(userId, UserProfile.class);
        ans = user.getBookmarks();

        return ans;

    }

    /**
     * function to get posts of specific user.
     * @param userID
     * @return
     */
    public List<Post> getPosts(String userID) throws ArangoDBException {
        ArrayList<Post> posts = new ArrayList<Post>();

        String query = "FOR l IN " + postsCollection + " FILTER l.authorId == @authorId RETURN l";
        Map<String, Object> bindVars = new MapBuilder().put("authorId", userID).get();
        ArangoCursor<Post> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                Post.class);
        cursor.forEachRemaining(postDocument -> {

            posts.add(postDocument);
        });
        return posts;
    }

    /**
     * function to get specific post in database.
     * @param postId
     * @return
     */
    public Post getPost(String postId) throws ArangoDBException{
        Post post = null;
        post = arangoDB.db(dbName).collection(postsCollection).getDocument(postId,
                    Post.class);

        return post;
    }

    public UserProfile getUser(String userId) throws ArangoDBException {
        UserProfile user = null;
        user = arangoDB.db(dbName).collection(usersCollection).getDocument(userId,
                    UserProfile.class);
        return user;
    }

    /**
     * function to add post in database.
     * @param post
     * @return
     */
    public boolean addPost(Post post) throws ArangoDBException{
        boolean response = false;
        arangoDB.db(dbName).collection(postsCollection).insertDocument(post);
        response = true;
        return response;
    }

    /**
     *
     * @param args
     * @return
     */

    public boolean editPost(HashMap<String, Object> args) throws ArangoDBException{

        Gson gson = new Gson();
        Map<String, Object> bindVars = new HashMap<>();
        int counter = 0;
        boolean response = false;

            String query = "FOR p IN " + postsCollection + " FILTER p._key == @key UPDATE p with {";
            bindVars.put("key",args.get("postId").toString());
            for (String key : args.keySet()) {
                if(!key.equals("postId")){
                    query += key + ":";
                    query+="@field"+counter+ " ,";
                    Object arg = args.get(key);
                    if (arg instanceof JsonArray)
                        bindVars.put("field"+counter, gson.fromJson((JsonElement) arg, List.class));
                    else
                        bindVars.put("field"+counter,args.get(key));
                    counter ++;
                }
            }
            query = query.substring(0,query.length()-1);
            query += "} IN " + postsCollection;
            arangoDB.db(dbName).query(query, bindVars, null, Post.class);
            response = true;

            return response;
    }

    /**
     * function to delete specific post from database.
     * @param post
     * @return
     */
    public boolean deletePost(Post post) throws ArangoDBException{
        String query = "FOR post IN " + postsCollection + " FILTER post._key == @postId\t"
                + "REMOVE post IN " + postsCollection;
        Map<String, Object> bindVars = new MapBuilder().put("postId", post.getPostId()).get();
        arangoDB.db(dbName).query(query, bindVars, null,
                Post.class);
        return true;
    }


    /**
     * function to get list of comments on specific post.
     * @param postId
     * @return
     */
    public List<Comment> getComments(String postId) throws ArangoDBException {
        ArrayList<Comment> comments = new ArrayList<Comment>();
        String query = "FOR l IN " + commentsCollection + " FILTER l.parentPostId == @parentPostId RETURN l";
        Map<String, Object> bindVars = new MapBuilder().put("parentPostId", postId).get();
        ArangoCursor<Comment> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                Comment.class);
        cursor.forEachRemaining(commentDocument -> {
            comments.add(commentDocument);
        });

        return comments;
    }

    /**
     * function to get specifc comment.
     * @param commentId
     * @return
     */
    public Comment getComment(String commentId) throws ArangoDBException{
        Comment comment = null;
        comment = arangoDB.db(dbName).collection(commentsCollection).getDocument(commentId,
                    Comment.class);

        return comment;
    }

    /**
     * function to add comment in database and update post collection.
     * @param comment
     * @return
     */
    public boolean addComment(Comment comment) throws ArangoDBException{
        String postId = comment.getParentPostId();
        if(postId != null && getPost(postId) != null) {
            arangoDB.db(dbName).collection(commentsCollection).insertDocument(comment);
            String query = "FOR post in " + postsCollection
                    + " FILTER post._key == @parentPostId\t"
                    + "UPDATE post WITH { commentsCount : post.commentsCount + 1 } IN " + postsCollection;
            Map<String, Object> bindVars = new MapBuilder().put("parentPostId", comment.getParentPostId()).get();
            arangoDB.db(dbName).query(query, bindVars, null, Post.class);
            return true;
        } else {
            throw new WallException("Failed to add a comment missing post found.");
        }
    }

    /**
     *
     * @param args
     * @return
     */

    public boolean editComment(HashMap<String, Object> args) throws ArangoDBException {
        Map<String, Object> bindVars = new HashMap<>();
         int counter = 0;
         boolean response = false;
         String query = "FOR c IN " + commentsCollection + " FILTER c._key == @key UPDATE c with {";
         bindVars.put("key",args.get("commentId").toString());
         for (String key : args.keySet()) {
             if(!key.equals("commentId")){
                 query += key + ":";
                 query+="@field"+counter+ " ,";
                 bindVars.put("field"+counter,args.get(key));
                 counter ++;
                }
            }
            query = query.substring(0,query.length()-1);
            query += "} IN " + commentsCollection;
            arangoDB.db(dbName).query(query, bindVars, null, Comment.class);
            response = true;
        return response;
    }

    /**
     * function to delete specific comment in the database.
     * @param comment
     * @return
     */

    public boolean deleteComment(Comment comment) throws ArangoDBException {
        String postId = comment.getParentPostId();
        if(postId != null && getPost(postId) != null) {
            String query = "FOR comment IN " + commentsCollection + " FILTER comment._key == @commentId\t"
                    + "REMOVE comment IN " + commentsCollection;

            Map<String, Object> bindVars = new MapBuilder().put("commentId", comment.getCommentId()).get();
            arangoDB.db(dbName).query(query, bindVars, null, Comment.class);

            query = "FOR post in " + postsCollection
                    + " FILTER post._key == @parentPostId\t"
                    + "UPDATE post WITH { commentsCount : post.commentsCount - 1 } IN " + postsCollection;
            bindVars = new MapBuilder().put("parentPostId", comment.getParentPostId()).get();
            arangoDB.db(dbName).query(query, bindVars, null, Post.class);
        } else {
            throw new WallException("Failed to add a comment missing post found.");
        }
        return  true;
    }

    public List<Reply> getReplies(String commentId) throws ArangoDBException {
        ArrayList<Reply> replies = new ArrayList<Reply>();
        String query = "FOR r IN " + repliesCollection + " FILTER r.parentCommentId == @parentCommentId RETURN r";
        Map<String, Object> bindVars = new MapBuilder().put("parentCommentId", commentId).get();
        ArangoCursor<Reply> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                Reply.class);
        cursor.forEachRemaining(replyDocument -> {
            replies.add(replyDocument);
        });

        return replies;
    }

    public Reply getReply(String replyId) throws ArangoDBException {
        Reply reply = arangoDB.db(dbName).collection(repliesCollection).getDocument(replyId,
                Reply.class);
        return reply;
    }

    /**
     * function to add reply on comment.
     * @param reply
     * @return
     */
    public boolean addReply(Reply reply) throws ArangoDBException {
        String commentId = reply.getParentCommentId();
        String postId = reply.getParentPostId();
        if((postId != null && getPost(postId) != null) && (commentId != null && getComment(commentId ) != null)) {
            arangoDB.db(dbName).collection(repliesCollection).insertDocument(reply);
            String query = "FOR comment in " + commentsCollection
                    + " FILTER comment._key == @parentCommentId\t"
                    + "UPDATE comment WITH { repliesCount : comment.repliesCount + 1 } IN " + commentsCollection;
            Map<String, Object> bindVars = new MapBuilder().put("parentCommentId", reply.getParentCommentId()).get();
            arangoDB.db(dbName).query(query, bindVars, null, Comment.class);

            query = "FOR post in " + postsCollection
                    + " FILTER post._key == @parentPostId\t"
                    + "UPDATE post WITH { commentsCount : post.commentsCount + 1 } IN " + postsCollection;
            bindVars = new MapBuilder().put("parentPostId", reply.getParentPostId()).get();
            arangoDB.db(dbName).query(query, bindVars, null, Post.class);
        } else {
            throw new WallException("missing information");
        }
        return  true;

    }

    /**
     *
     * @param args
     * @return
     */

    public boolean editReply(HashMap<String, Object> args) throws ArangoDBException {

        Map<String, Object> bindVars = new HashMap<>();
        int counter = 0;
        boolean response = false;
        String query = "FOR r IN " + repliesCollection + " FILTER r._key == @key UPDATE r with {";
        bindVars.put("key",args.get("replyId").toString());
        for (String key : args.keySet()) {
            if(!key.equals("replyId")){
                query += key + ":";
                query+="@field"+counter+ " ,";
                bindVars.put("field"+counter,args.get(key));
                counter ++;
            }
        }
        query = query.substring(0,query.length()-1);
        query += "} IN " + repliesCollection;
        arangoDB.db(dbName).query(query, bindVars, null, Reply.class);
        response = true;
        return response;
    }

    /**
     * function to delete specific reply.
     * @param reply
     * @return
     */
    public boolean deleteReply(Reply reply) throws ArangoDBException{
        String commentId = reply.getParentCommentId();
        String postId = reply.getParentPostId();
        if((postId != null && getPost(postId) != null) && (commentId != null && getComment(commentId ) != null)) {
            String query = "FOR reply IN " + repliesCollection + " FILTER reply._key == @replyId\t"
                    + "REMOVE reply IN " + repliesCollection;

            Map<String, Object> bindVars = new MapBuilder().put("replyId", reply.getReplyId()).get();
            arangoDB.db(dbName).query(query, bindVars, null, Reply.class);
            // update comment query
            query = "FOR comment in " + commentsCollection
                    + " FILTER comment._key == @parentCommentId\t"
                    + "UPDATE comment WITH { repliesCount : comment.repliesCount - 1 } IN " + commentsCollection;
            bindVars = new MapBuilder().put("parentCommentId", reply.getParentCommentId()).get();
            arangoDB.db(dbName).query(query, bindVars, null, Comment.class);

            query = "FOR post in " + postsCollection
                    + " FILTER post._key == @parentPostId\t"
                    + "UPDATE post WITH { commentsCount : post.commentsCount - 1 } IN " + postsCollection;
            bindVars = new MapBuilder().put("parentPostId", reply.getParentPostId()).get();
            arangoDB.db(dbName).query(query, bindVars, null, Post.class);
        } else {
            throw new WallException("missing information");
        }
        return  true;
    }

    /**
     * get specific like from like collection.
     * @param likeId
     * @return
     */
    public Like getLike(String likeId) throws ArangoDBException{
        Like like = null;

        like = arangoDB.db(dbName).collection(likesCollection).getDocument(likeId,
                    Like.class);

        return like;
    }

    /**
     * get likes on specific likes.
     * @param postId
     * @return
     */
    public List<Like> getPostLikes(String postId) throws ArangoDBException {
        ArrayList<Like> likes = new ArrayList<Like>();
        String query = "FOR l IN " + likesCollection + " FILTER l.likedPostId == @postId RETURN l";
        Map<String, Object> bindVars = new MapBuilder().put("postId", postId).get();
        ArangoCursor<Like> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                Like.class);
        cursor.forEachRemaining(likeDocument -> {
            likes.add(likeDocument);
        });

        return likes;

    }

    /**
     * function to get likes on comment.
     * @param commentId
     * @return
     */
    public List<Like> getCommentLikes(String commentId) throws ArangoDBException {
        ArrayList<Like> likes = new ArrayList<Like>();

        String query = "FOR l IN " + likesCollection + " FILTER l.likedCommentId == @commentId RETURN l";
        Map<String, Object> bindVars = new MapBuilder().put("commentId", commentId).get();
        ArangoCursor<Like> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                Like.class);
        cursor.forEachRemaining(likeDocument -> {
            likes.add(likeDocument);
        });

        return likes;
    }

    /**
     * function to get likes on specific reply.
     * @param replyId
     * @return
     */
    public List<Like> getReplyLikes(String replyId) throws ArangoDBException {
        ArrayList<Like> likes = new ArrayList<Like>();
        String query = "FOR l IN " + likesCollection + " FILTER l.likedReplyId == @replyId RETURN l";
        Map<String, Object> bindVars = new MapBuilder().put("replyId", replyId).get();
        ArangoCursor<Like> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                Like.class);
        cursor.forEachRemaining(likeDocument -> {
            likes.add(likeDocument);
        });

        return likes;
    }

    /**
     * function to add like on post/ like/ reply.
     * @param like
     * @return
     */
    public boolean addLike(Like like) throws ArangoDBException{
        boolean response = false;
        String commentId = like.getLikedCommentId();
        String replyId = like.getLikedReplyId();
        String postId = like.getLikedPostId();
        if((postId!= null && getPost(postId) != null) || (commentId != null && getComment(commentId) != null) || (replyId!= null && getReply(replyId) != null)) {

            DocumentCreateEntity likeDoc = arangoDB.db(dbName).collection(likesCollection).insertDocument(like);
            response = true;


            if (like.getLikedPostId() != null) {
                Post post = getPost(like.getLikedPostId());
                if (post != null) {
                    post.setLikesCount(post.getLikesCount() + 1);
                    HashMap<String, Object> editPostArgs = new HashMap<String, Object>();
                    editPostArgs.put("postId", post.getPostId());
                    editPostArgs.put("likesCount", post.getLikesCount());
                    editPost(editPostArgs);
                } else {
                    throw new WallException("Failed to update post's like count. ");
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
                    throw new WallException("Failed to update comment's like count. ");
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
                    throw new WallException("Failed to update reply's like count. ");
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
    public boolean deleteLike(Like like) throws ArangoDBException{
        boolean response = false;

        arangoDB.db(dbName).collection(likesCollection).deleteDocument(like.getLikeId());
        response = true;

        if(like.getLikedPostId() != null){
            Post post = getPost(like.getLikedPostId());
            if(post !=null){
                post.setLikesCount(post.getLikesCount() - 1);
                HashMap<String, Object> editPostArgs = new HashMap<String, Object>();
                editPostArgs.put("postId", post.getPostId());
                editPostArgs.put("likesCount", post.getLikesCount());
                editPost(editPostArgs);
            }
            else {
                throw new WallException("Failed to update post's like count. ");
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
                throw new WallException("Failed to update comment's like count. ");
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
                throw new WallException("Failed to update reply's like count. ");
            }

        }
        return response;
    }

    /**
     * function to get the top posts.
     * @throws ParseException
     */
    public void getTopPosts() throws ParseException, ArangoDBException {

        String query = "FOR p IN " + postsCollection + " RETURN p";
        ArangoCursor<Post> cursor = arangoDB.db(dbName).query(query, null, null,
                Post.class);
        cursor.forEachRemaining(postDocument -> {
        });

        DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd yyyy hh:mm a");
        Date postDate = dateFormat.parse("Mon Mar 19 2018 01:00 PM");
        Date currentDate = new Date();
        float diffInDays = (currentDate.getTime()-postDate.getTime())/(1000*60*60*24);
    }

}
