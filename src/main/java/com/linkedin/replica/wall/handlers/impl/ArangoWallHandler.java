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
import java.util.*;

public  class ArangoWallHandler implements DatabaseHandler {
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

    public List<Bookmark> getBookmarks() {
        return null;
    }

    public String addBookmark(Bookmark bookmark) {
        String userCollection = properties.getProperty(properties.getProperty("collections.users.name"));
        String userId = bookmark.getUserId();

        String getUserQuery = "FOR t IN @userCollection FILTER t.userId == @userId RETURN t";
        String message = "";
        try {
            BaseDocument user = arangoDB.db(dbName).collection(userCollection).
                    getDocument(getUserQuery, BaseDocument.class);
            List<Object> l = (List<Object>) user.getAttribute("bookmarks");
            if (l == null)
                l = new ArrayList<Object>();
            l.add(bookmark);
            arangoDB.db(dbName).collection(userCollection).updateDocument("bookmarks", l);
            message = "Success to add bookmark";

        } catch (ArangoDBException e) {
            System.err.println("Failed to delete bookmark. " + e.getMessage());
            message = "Failed to add bookmark. " + e.getMessage();
        }
        return message;
    }


    public String deleteBookmark(Bookmark bookmark) {
        String userCollection = properties.getProperty(properties.getProperty("collections.users.name"));
        String userId = bookmark.getUserId();
        String getUserQuery = "FOR t IN @userCollection FILTER t.userId == @userId RETURN t";
        String message = "";
        try {
            BaseDocument user = arangoDB.db(dbName).collection(userCollection).
                    getDocument(getUserQuery, BaseDocument.class);

            List<Object> l = (List<Object>) user.getAttribute("bookmarks");
            if (l == null)
                l = new ArrayList<Object>();
            l.remove(bookmark);
            arangoDB.db(dbName).collection(userCollection).updateDocument("bookmarks", l);
            message = "Success to add bookmark";

        } catch (ArangoDBException e) {
            System.err.println("Failed to delete bookmark. " + e.getMessage());
            message = "Failed to delete bookmark";
        }
        return message;
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



    public List<Comment> getComments(String postID) {
        final ArrayList<Comment> comments = new ArrayList<Comment>();
        try {
            String query = "FOR l IN " + commentsCollection + " FILTER l.parentPostId == " + postID + " RETURN l";
            Map<String, Object> bindVars = new MapBuilder().put("parentPostID", postID).get();
            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null, BaseDocument.class);
            cursor.forEachRemaining(commentDocument -> {
                Comment comment;
                String commentID = commentDocument.getKey();
                String authorID = (String) commentDocument.getAttribute("authorID");
                String parentPostID = (String) commentDocument.getAttribute("parentPostID");
                int likesCount = (Integer) commentDocument.getAttribute("likesCount");
                int repliesCount = (Integer) commentDocument.getAttribute("repliesCount");
                ArrayList<String> images = (ArrayList<String>) commentDocument.getAttribute("images");
                ArrayList<String> urls = (ArrayList<String>) commentDocument.getAttribute("urls");
                ArrayList<String> mentions = (ArrayList<String>) commentDocument.getAttribute("mentions");
                String text = (String) commentDocument.getAttribute("text");
                String timeStamp = (String) commentDocument.getAttribute("timeStamp");
                comment = new Comment(commentID, authorID, parentPostID, likesCount, repliesCount, images, urls,mentions,text,timeStamp);
                comments.add(comment);
                System.out.println("Key: " + commentDocument.getKey());
            });
        } catch (ArangoDBException e) {
            System.err.println("Failed to execute query. " + e.getMessage());
        }
        return comments;
    }

    public Comment getComment(String commentID) {
        Comment comment = null;
        try {
            BaseDocument commentDocument = arangoDB.db(dbName).collection(commentsCollection).getDocument(commentID,
                    BaseDocument.class);
            System.out.println("Key: " + commentDocument.getKey());
            String authorID = (String) commentDocument.getAttribute("authorID");
            String parentPostID = (String) commentDocument.getAttribute("parentPostID");
            int likesCount = (Integer) commentDocument.getAttribute("likesCount");
            int repliesCount = (Integer) commentDocument.getAttribute("repliesCount");
            ArrayList<String> images = (ArrayList<String>) commentDocument.getAttribute("images");
            ArrayList<String> urls = (ArrayList<String>) commentDocument.getAttribute("urls");
            ArrayList<String> mentions = (ArrayList<String>) commentDocument.getAttribute("mentions");
            String text = (String) commentDocument.getAttribute("text");
            String timeStamp = (String) commentDocument.getAttribute("timeStamp");
            comment = new Comment(commentID, authorID, parentPostID, likesCount, repliesCount, images, urls,mentions,text,timeStamp);

        } catch (ArangoDBException e) {
            System.err.println("Failed to get comment: commentId; " + e.getMessage());
        }
        return comment;
    }

    public BaseDocument createCommentDoc(Comment comment){
        BaseDocument commentDocument = new BaseDocument();
        commentDocument.setKey(comment.getCommentId());
        commentDocument.addAttribute("authorID", comment.getAuthorId());
        commentDocument.addAttribute("parentPostID", comment.getParentPostId());
        commentDocument.addAttribute("likesCount", comment.getLikesCount());
        commentDocument.addAttribute("repliesCount", comment.getRepliesCount());
        commentDocument.addAttribute("images", comment.getImages());
        commentDocument.addAttribute("urls", comment.getUrls());
        commentDocument.addAttribute("mentions", comment.getMentions());
        commentDocument.addAttribute("text", comment.getText());
        commentDocument.addAttribute("timeStamp", comment.getTimeStamp());
        return commentDocument;
    }

    public String addComment(Comment comment) {
        String response = "";
        BaseDocument commentDocument = createCommentDoc(comment);
        try {
            arangoDB.db(dbName).collection("comments").insertDocument(commentDocument);
            response = "Comment Created";
        } catch (ArangoDBException e) {
            response = "Failed to add comment. " + e.getMessage();
        }
        return response;

    }

    public String editComment(Comment comment) {
        String response = "";
        BaseDocument commentDocument = createCommentDoc(comment);
        try {
            arangoDB.db(dbName).collection(commentsCollection).updateDocument(comment.getCommentId(), commentDocument);

        } catch (ArangoDBException e) {
            System.err.println("Failed to update comment. " + e.getMessage());
            response = "Failed to update comment. " + e.getMessage();
        }
        return response;
    }

    public String deleteComment(Comment comment) {
        String response="";
        try {
            arangoDB.db(dbName).collection(commentsCollection).deleteDocument(comment.getCommentId());

        } catch (ArangoDBException e) {
            System.err.println("Failed to delete document. " + e.getMessage());
            response = "Failed to delete comment";
        }
        return response;
    }

    public List<Reply> getReplies(String commentId) {
        ArrayList<Reply> replies = new ArrayList<Reply>();
        try {
            String query = "FOR r IN " + repliesCollection + " FILTER r.parentCommentId == " + commentId + " RETURN r";
            Map<String, Object> bindVars = new MapBuilder().put("parentCommentId", commentId).get();
            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    BaseDocument.class);
            cursor.forEachRemaining(replyDocument -> {
                Reply reply;
                String replyId = replyDocument.getKey();
                String authorId = (String) replyDocument.getAttribute("authorId");
                String parentPostId = (String) replyDocument.getAttribute("parentPostId");
                String parentCommentId = (String) replyDocument.getAttribute("parentCommentId");
                ArrayList<String> mentions = (ArrayList<String>) replyDocument.getAttribute("mentions");
                Long likesCount = (Long) replyDocument.getAttribute("likesCount");
                String text = (String) replyDocument.getAttribute("text");
                Date timestamp = (Date) replyDocument.getAttribute("timestamp");
                ArrayList<String> images = (ArrayList<String>) replyDocument.getAttribute("images");
                ArrayList<String> urls = (ArrayList<String>) replyDocument.getAttribute("urls");


                reply = new Reply(replyId, authorId, parentPostId, parentCommentId, mentions, likesCount, text, timestamp, images, urls);
                replies.add(reply);
                System.out.println("Key: " + replyDocument.getKey());
            });
        } catch (ArangoDBException e) {
            System.err.println("Failed to get replies. " + e.getMessage());
        }
        return replies;
    }

    public Reply getReply(String replyId) {
        Reply reply = null;
        try {
            BaseDocument replyDocument = arangoDB.db(dbName).collection(repliesCollection).getDocument(replyId,
                    BaseDocument.class);
            System.out.println("Key: " + replyDocument.getKey());
            replyId = replyDocument.getKey();
            String authorId = (String) replyDocument.getAttribute("authorId");
            String parentPostId = (String) replyDocument.getAttribute("parentPostId");
            String parentCommentId = (String) replyDocument.getAttribute("parentCommentId");
            ArrayList<String> mentions = (ArrayList<String>) replyDocument.getAttribute("mentions");
            Long likesCount = (Long) replyDocument.getAttribute("likesCount");
            String text = (String) replyDocument.getAttribute("text");
            Date timestamp = (Date) replyDocument.getAttribute("timestamp");
            ArrayList<String> images = (ArrayList<String>) replyDocument.getAttribute("images");
            ArrayList<String> urls = (ArrayList<String>) replyDocument.getAttribute("urls");

            reply = new Reply(replyId, authorId, parentPostId, parentCommentId, mentions, likesCount, text, timestamp, images, urls);

        } catch (ArangoDBException e) {
            System.err.println("Failed to get reply: replyId; " + e.getMessage());
        }
        return reply;
    }

    public String addReply(Reply reply) {
        String response = "";
        BaseDocument replyDocument = createReplyDocument(reply);
        try {
            arangoDB.db(dbName).collection(repliesCollection).insertDocument(replyDocument);
            System.out.println("Reply created");
            response = "Reply created";
        } catch (ArangoDBException e) {
            System.err.println("Failed to add reply. " + e.getMessage());
            response = "Failed to add reply. " + e.getMessage();
        }
        Comment comment = getComment(reply.getParentCommentId());
        if(comment !=null){
            comment.setRepliesCount(comment.getRepliesCount() + 1);
            editComment(comment);
        }
        else {
            response = "Failed to update comment's reply count. ";
        }
        //Todo:
        // 1. get post: call getPost()
        // 2. update post object: add 1 to commentsCount
        // 3. update post document: call editPosts()

        return response;

    }

    public BaseDocument createReplyDocument(Reply reply) {
        BaseDocument replyDocument = new BaseDocument();
        replyDocument.setKey(reply.getReplyId());
        replyDocument.addAttribute("authorId", reply.getAuthorId());
        replyDocument.addAttribute("parentPostId", reply.getParentPostId());
        replyDocument.addAttribute("parentCommentId", reply.getParentCommentId());
        replyDocument.addAttribute("mentions", reply.getMentions());
        replyDocument.addAttribute("likesCount", reply.getLikesCount());
        replyDocument.addAttribute("text", reply.getText());
        replyDocument.addAttribute("timestamp", reply.getTimestamp());
        replyDocument.addAttribute("images", reply.getImages());
        replyDocument.addAttribute("urls", reply.getUrls());
        return replyDocument;

    }

    public String editReply(Reply reply) {
        String response = "";
        BaseDocument replyDocument = createReplyDocument(reply);
        try {
            arangoDB.db(dbName).collection(repliesCollection).updateDocument(reply.getReplyId() ,replyDocument);
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
        if(comment !=null){
            comment.setRepliesCount(comment.getRepliesCount() - 1);
            editComment(comment);
        }
        else {
            response = "Failed to update comment's reply count. ";
        }
        //Todo:
        // 1. get post: call getPost()
        // 2. update post object: add 1 to commentsCount
        // 3. update post document: call editPosts()
        return response;

    }

    public List<Like> getPostLikes(String postId) {
        ArrayList<Like> likes = new ArrayList<Like>();
        try {
            String query = "FOR l IN " + likesCollection + " FILTER l.likedPostId == " + postId + " RETURN l";
            Map<String, Object> bindVars = new MapBuilder().put("likedPostId", postId).get();
            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    BaseDocument.class);
            cursor.forEachRemaining(likeDocument -> {
                Like like;
                String likeId = likeDocument.getKey();
                String likerId = (String) likeDocument.getAttribute("likerId");
                String userName = (String) likeDocument.getAttribute("username");
                String headLine = (String) likeDocument.getAttribute("headline");
                String imageUrl = (String) likeDocument.getAttribute("imageUrl");
                String likedPostId = (String) likeDocument.getAttribute("likedPostId");
                String likedCommentId = (String) likeDocument.getAttribute("likedCommentId");
                String likedReplyId = (String) likeDocument.getAttribute("likedReplyId");
                like = new Like(likeId, likerId, likedPostId, likedCommentId, likedReplyId, userName, headLine,imageUrl);
                likes.add(like);
                System.out.println("Key: " + likeDocument.getKey());
            });
        } catch (ArangoDBException e) {
            System.err.println("Failed to get posts' likes." + e.getMessage());
        }
        return likes;

    }

    public List<Like> getCommentLikes(String commentId) {
        ArrayList<Like> likes = new ArrayList<Like>();
        try {
            String query = "FOR l IN " + likesCollection + " FILTER l.likedCommentId == " + commentId + " RETURN l";
            Map<String, Object> bindVars = new MapBuilder().put("likedCommentId", commentId).get();
            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    BaseDocument.class);
            cursor.forEachRemaining(likeDocument -> {
                Like like;
                String likeId = likeDocument.getKey();
                String likerId = (String) likeDocument.getAttribute("likerId");
                String userName = (String) likeDocument.getAttribute("username");
                String headLine = (String) likeDocument.getAttribute("headline");
                String imageUrl = (String) likeDocument.getAttribute("imageUrl");
                String likedPostId = (String) likeDocument.getAttribute("likedPostId");
                String likedCommentId = (String) likeDocument.getAttribute("likedCommentId");
                String likedReplyId = (String) likeDocument.getAttribute("likedReplyId");
                like = new Like(likeId, likerId, likedPostId, likedCommentId, likedReplyId, userName, headLine,imageUrl);
                likes.add(like);
                System.out.println("Key: " + likeDocument.getKey());
            });
        } catch (ArangoDBException e) {
            System.err.println("Failed to get comments' likes." + e.getMessage());
        }
        return likes;
    }

    public List<Like> getReplyLikes(String replyId) {
        ArrayList<Like> likes = new ArrayList<Like>();
        try {
            String query = "FOR l IN " + likesCollection + " FILTER l.likedReplyId == " + replyId + " RETURN l";
            Map<String, Object> bindVars = new MapBuilder().put("likedReplyId", replyId).get();
            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    BaseDocument.class);
            cursor.forEachRemaining(likeDocument -> {
                Like like;
                String likeId = likeDocument.getKey();
                String likerId = (String) likeDocument.getAttribute("likerId");
                String userName = (String) likeDocument.getAttribute("username");
                String headLine = (String) likeDocument.getAttribute("headline");
                String imageUrl = (String) likeDocument.getAttribute("imageUrl");
                String likedPostId = (String) likeDocument.getAttribute("likedPostId");
                String likedCommentId = (String) likeDocument.getAttribute("likedCommentId");
                String likedReplyId = (String) likeDocument.getAttribute("likedReplyId");
                like = new Like(likeId, likerId, likedPostId, likedCommentId, likedReplyId, userName, headLine,imageUrl);
                likes.add(like);
                System.out.println("Key: " + likeDocument.getKey());
            });
        } catch (ArangoDBException e) {
            System.err.println("Failed to get replies' likes." + e.getMessage());
        }
        return likes;
    }

    public String addLike(Like like) {
        String response = "";
        BaseDocument likeDocument = new BaseDocument();
        likeDocument.setKey(like.getLikeId());
        likeDocument.addAttribute("likerId", like.getLikerId());
        likeDocument.addAttribute("username", like.getUserName());
        likeDocument.addAttribute("headline", like.getHeadLine());
        likeDocument.addAttribute("imageUrl", like.getImageUrl());
        likeDocument.addAttribute("likedPostId", like.getLikedPostId());
        likeDocument.addAttribute("LikedCommentId", like.getLikedCommentId());
        likeDocument.addAttribute("likedReplyId", like.getLikedReplyId());

        try {
            arangoDB.db(dbName).collection(likesCollection).insertDocument(likeDocument);
            System.out.println("Like added");
            response = "Like added";
        } catch (ArangoDBException e) {
            System.err.println("Failed to add a like. " + e.getMessage());
            response = "Failed to add a like. " + e.getMessage();
        }

        if(like.getLikedPostId() != null){
            //Todo:
            // 1. get post: call getPosts()
            // 2. update post object: add 1 to likes
            // 3. update post document: call editPost()

        }
        else if(like.getLikedCommentId() != null){
            Comment comment = getComment(like.getLikedCommentId());
            if(comment !=null){
                comment.setLikesCount(comment.getLikesCount() + 1);
                editComment(comment);
            }
            else {
                response = "Failed to update comment's like count. ";
            }

        }
        else if(like.getLikedReplyId() != null){
            Reply reply = getReply(like.getLikedReplyId());
            if(reply !=null){
                reply.setLikesCount(reply.getLikesCount() + 1);
                editReply(reply);
            }
            else {
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
            //Todo:
            // 1. get post: call getPosts()
            // 2. update post object: subtract 1 from likes
            // 3. update post document: call editPost()

        }
        else if(like.getLikedCommentId() != null){
            Comment comment = getComment(like.getLikedCommentId());
            if(comment !=null){
                comment.setLikesCount(comment.getLikesCount() - 1);
                editComment(comment);
            }
            else {
                response = "Failed to update comment's like count. ";
            }

        }
        else if(like.getLikedReplyId() != null){
            Reply reply = getReply(like.getLikedReplyId());
            if(reply !=null){
                reply.setLikesCount(reply.getLikesCount() - 1);
                editReply(reply);
            }
            else {
                response = "Failed to update reply's like count. ";
            }

        }
        return response;
    }

//    public static void main(String[] args) throws IOException, ClassNotFoundException {
//        DatabaseHandler arangoWallHandler = new ArangoWallHandler();
//        Properties properties = new Properties();
//        properties.load(new FileInputStream("config"));
//        String dbName = properties.getProperty(properties.getProperty("arangodb.name"));
//        ArangoDB arangoDB = DatabaseConnection.getInstance().getArangodb();
////       // UserProfile userProfile = new UserProfile("khly@gmail.com", "Mohamed", "Khaled");
//        UserProfile userProfile = new UserProfile("khly@gmail.com", "Mohamed", "Khaled");
//        BaseDocument myObject = new BaseDocument();
//        myObject.setKey("se7s");
//        myObject.addAttribute("email", "khly@gmail.com");
//        myObject.addAttribute("firstName", "Mohamed");
//        myObject.addAttribute("lastName", "Khaled");
//        System.out.println("3ww");
//        try {
//            arangoDB.db(dbName).collection("Users").insertDocument(myObject);
//            System.out.println("Document created");
//        } catch (ArangoDBException e) {
//            System.err.println("Failed to create document. " + e.getMessage());
//        }
//
//        try {
//            BaseDocument myUpdatedDocument = arangoDB.db(dbName).collection("Users").getDocument("se7s",
//                    BaseDocument.class);
//            System.out.println("Key: " + myUpdatedDocument.getKey());
//            System.out.println("firstName: " + myUpdatedDocument.getAttribute("firstName"));
//            System.out.println("lastName: " + myUpdatedDocument.getAttribute("lastName"));
//            System.out.println("email: " + myUpdatedDocument.getAttribute("email"));
//        } catch (ArangoDBException e) {
//            System.err.println("Failed to get document: myKey; " + e.getMessage());
//        }
//
//        System.out.println();
//
//    }
}
