package com.linkedin.replica.wall.handlers.impl;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.DocumentCreateEntity;
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
            Bookmark b = user.getBookmarks().get(0);

            System.out.println(bookmarkList.contains(bookmark));
            bookmarkList.remove(bookmark);
            System.out.println(bookmarkList.contains(bookmark));

            user.setBookmarks(bookmarkList);
            System.out.println(bookmarkList.contains(bookmark));

            System.out.println(bookmarkList.size() + " second");
            arangoDB.db(dbName).collection(usersCollection).updateDocument(userId, user);

            message = "Success to deletes bookmark";

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


    public BaseDocument createPostDoc(Post post) {
        BaseDocument postDocument = new BaseDocument();
        postDocument.setKey(post.getPostID());
        postDocument.addAttribute("authorID", post.getAuthorID());
        postDocument.addAttribute("type", post.getType());
        postDocument.addAttribute("companyID", post.getCompanyID());
        postDocument.addAttribute("privacy", post.getPrivacy());
        postDocument.addAttribute("text", post.getText());
        postDocument.addAttribute("timeStamp", post.getTimeStamp());
        postDocument.addAttribute("isCompanyPost", post.isCompanyPost());
        postDocument.addAttribute("isPrior", post.isPrior());
        postDocument.addAttribute("hashtags", post.getHashtags());
        postDocument.addAttribute("mentions", post.getMentions());
        postDocument.addAttribute("images", post.getImages());
        postDocument.addAttribute("videos", post.getVideos());
        postDocument.addAttribute("urls", post.getUrls());
        postDocument.addAttribute("shares", post.getShares());
        postDocument.addAttribute("likesCount", post.getUrls());
        postDocument.addAttribute("commentsCount", post.getShares());

        return postDocument;
    }

    public List<Post> getPosts(String userID) {

        final ArrayList<Post> posts = new ArrayList<Post>();
        try {
            String query = "FOR l IN " + postsCollection + " FILTER l.authorID == " + userID + " RETURN l";
            Map<String, Object> bindVars = new MapBuilder().put("authorID", userID).get();
            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null, BaseDocument.class);
            cursor.forEachRemaining(postDocument -> {
                Post post;
                String postID = postDocument.getKey();
                String authorID = (String) postDocument.getAttribute("authorID");
                String type = (String) postDocument.getAttribute("type");
                String companyID = (String) postDocument.getAttribute("companyID");
                String privacy = (String) postDocument.getAttribute("privacy");
                String text = (String) postDocument.getAttribute("text");
                String timeStamp = (String) postDocument.getAttribute("timeStamp");
                boolean isCompanyPost = (boolean) postDocument.getAttribute("isCompanypost");
                boolean isPrior = (boolean) postDocument.getAttribute("isPrior");
                ArrayList<String> hashtags = (ArrayList<String>) postDocument.getAttribute("hashtags");
                ArrayList<String> mentions = (ArrayList<String>) postDocument.getAttribute("mentions");
                ArrayList<String> images = (ArrayList<String>) postDocument.getAttribute("images");
                ArrayList<String> videos = (ArrayList<String>) postDocument.getAttribute("videos");
                ArrayList<String> urls = (ArrayList<String>) postDocument.getAttribute("urls");
                ArrayList<String> shares = (ArrayList<String>) postDocument.getAttribute("shares");
                int likesCount = (Integer) postDocument.getAttribute("likesCount");
                int commentsCount = (Integer) postDocument.getAttribute("commentsCount");


                post = new Post(postID, authorID, type, companyID, privacy, text, timeStamp, isCompanyPost, isPrior,
                        hashtags, mentions, images, videos, urls, shares, likesCount, commentsCount);
                posts.add(post);
                System.out.println("Key: " + postDocument.getKey());
            });
        } catch (ArangoDBException e) {
            System.err.println("Failed to execute query. " + e.getMessage());
        }
        return posts;
    }

    public Post getPost(String postId) {
        Post post = null;
        try {
            post = arangoDB.db(dbName).collection(postsCollection).getDocument(postId,
                    Post.class);
            // System.out.println("Key: " + commentDocument.getCommentId());
        } catch (ArangoDBException e) {
            System.err.println("Failed to get post: postId; " + e.getMessage());
        }
        return post;
    }

    public String addPost(Post post) {
        String response = "";
        BaseDocument postDocument = createPostDoc(post);

        try {
            arangoDB.db(dbName).collection("posts").insertDocument(postDocument);
            response = "Post Created";
        } catch (ArangoDBException e) {
            response = "Failed to add post. " + e.getMessage();
        }
        return response;
    }

    public String editPost(Post post) {

        String response = "";
        BaseDocument postDocument = createPostDoc(post);
        try {
            arangoDB.db(dbName).collection("posts").updateDocument(post.getPostID(), postDocument);

        } catch (ArangoDBException e) {
            System.err.println("Failed to update post. " + e.getMessage());
            response = "Failed to update post. " + e.getMessage();
        }
        return response;
    }

    public String deletePost(Post post) {

        String response = "";
        try {
            arangoDB.db(dbName).collection("posts").deleteDocument(post.getPostID());

        } catch (ArangoDBException e) {
            System.err.println("Failed to delete post. " + e.getMessage());
            response = "Failed to delete post";
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
                comment = new Comment(commentID, authorID, parentPostID, likesCount, repliesCount, images, urls, mentions, text, timeStamp);
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
            comment = arangoDB.db(dbName).collection(commentsCollection).getDocument(commentID,
                    Comment.class);
           // System.out.println("Key: " + commentDocument.getCommentId());
        } catch (ArangoDBException e) {
            System.err.println("Failed to get comment: commentId; " + e.getMessage());
        }
        return comment;
    }

    public BaseDocument createCommentDoc(Comment comment) {
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
        String response = "";
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
            reply = arangoDB.db(dbName).collection(repliesCollection).getDocument(replyId,
                    Reply.class);
//            System.out.println("Key: " + replyDocument.getReplyId());
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
            arangoDB.db(dbName).collection(repliesCollection).updateDocument(reply.getReplyId(), replyDocument);
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
}
