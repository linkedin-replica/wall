package com.linkedin.replica.wall.database.handlers.impl;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
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
import java.util.*;

public class ArangoWallHandler implements WallHandler {
	private ArangoDB arangoDB;
	private String dbName;
	private Configuration config;
	String likesCollection;
	String repliesCollection;
	String usersCollection;
	String commentsCollection;
	String postsCollection;

	public ArangoWallHandler() throws IOException, ClassNotFoundException {
		arangoDB = DatabaseConnection.getInstance().getArangodb();
		config = Configuration.getInstance();
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

    public boolean addLikeToPost(String likerId, String postId) throws ArangoDBException {
        boolean response = false;
        Map<String, Object> bindVars = new MapBuilder().get();
        bindVars.put("postId",postId);
        bindVars.put("likerId",likerId);
        //execute query
        String query = "FOR u IN " + postsCollection +
                " FILTER u._key == @postId " +
                "LET newLikers = PUSH(u.likers, @likerId) UPDATE u WITH{ likers : newLikers } " +
                "IN " + postsCollection;

        arangoDB.db(dbName).query(query, bindVars, null, Post.class);
        response = true;


        return response;


    }

    public boolean deleteLikeFromPost(String likerId,String postId) throws ArangoDBException {
        boolean response = false;
        Map<String, Object> bindVars = new MapBuilder().get();
        bindVars.put("postId",postId);
        bindVars.put("likerId",likerId);
        //execute query
        String query = "FOR u IN " + postsCollection +
                " FILTER u._key == @postId " +
                "LET newLikers = REMOVE_VALUE(u.likers, @likerId) UPDATE u WITH{ likers : newLikers } " +
                "IN " + postsCollection;

        arangoDB.db(dbName).query(query, bindVars, null, Post.class);
        response = true;


        return response;
    }

    public boolean addLikeToComment(String likerId, String commentId) throws ArangoDBException{
        boolean response = false;
        Map<String, Object> bindVars = new MapBuilder().get();
        bindVars.put("commentId",commentId);
        bindVars.put("likerId",likerId);
        //execute query
        String query = "FOR u IN " + commentsCollection +
                " FILTER u._key == @commentId " +
                "LET newLikers = PUSH(u.likers, @likerId) UPDATE u WITH{ likers : newLikers } " +
                "IN " + commentsCollection;

        arangoDB.db(dbName).query(query, bindVars, null, Comment.class);
        response = true;


        return response;

    }

    public boolean deleteLikeFromComment(String likerId,String commentId) throws ArangoDBException {
        boolean response = false;
        Map<String, Object> bindVars = new MapBuilder().get();
        bindVars.put("commentId", commentId);
        bindVars.put("likerId", likerId);
        //execute query
        String query = "FOR u IN " + commentsCollection +
                " FILTER u._key == @commentId " +
                "LET newLikers = REMOVE_VALUE(u.likers, @likerId) UPDATE u WITH{ likers : newLikers } " +
                "IN " + commentsCollection;

        arangoDB.db(dbName).query(query, bindVars, null, Comment.class);
        response = true;
        return response;
    }



    public boolean addLikeToReply(String likerId, String replyId) throws ArangoDBException{
        boolean response = false;
        Map<String, Object> bindVars = new MapBuilder().get();
        bindVars.put("replyId",replyId);
        bindVars.put("likerId",likerId);
        //execute query
        String query = "FOR u IN " + repliesCollection +
                " FILTER u._key == @replyId " +
                "LET newLikers = PUSH(u.likers, @likerId) UPDATE u WITH{ likers : newLikers } " +
                "IN " + repliesCollection;

        arangoDB.db(dbName).query(query, bindVars, null, Reply.class);
        response = true;


        return response;
    }


    public boolean deleteLikeFromReply(String likerId,String replyId)throws ArangoDBException {
        boolean response = false;
        Map<String, Object> bindVars = new MapBuilder().get();
        bindVars.put("replyId",replyId);
        bindVars.put("likerId",likerId);
        //execute query
        String query = "FOR u IN " + repliesCollection +
                " FILTER u._key == @replyId " +
                "LET newLikers = REMOVE_VALUE(u.likers, @likerId) UPDATE u WITH{ likers : newLikers } " +
                "IN " + repliesCollection;

        arangoDB.db(dbName).query(query, bindVars, null, Reply.class);
        response = true;
        return response;
    }
    
	public ArrayList<ReturnedPost> getNewsFeed(String userId, int limit) {
		StringBuilder builder = new StringBuilder();
		builder.append("FOR user in users ");
		builder.append("FILTER user.userId == @userId ");
		builder.append("LET friendPosts =  ( ");
		builder.append("for friend in users ");
		builder.append("filter friend.userId in user.friendsList ");
		builder.append("for p in posts ");
		builder.append("FILTER p.authorId == friend.userId and p.isArticle == false ");
		builder.append("SORT p.timestamp DESC ");
		builder.append("Limit 0, @limit ");
		builder.append("return MERGE_RECURSIVE( ");
		builder.append("{ \"authorName\":  concat(friend.firstName, \" \", friend.lastName), ");
		builder.append("\"authorProfilePictureUrl\" : friend.profilePictureUrl, ");
		builder.append("\"headline\" : friend.headline ");
		builder.append(" }, ");
		builder.append("{\"authorId\" : p.authorId, \"postId\" : p.postId, \"text\" : p.text, ");
		builder.append("\"images\" : p.images, \"videos\" : p.videos, \"commentsCount\" : p.commentsCount, ");
		builder.append("\"timestamp\" : p.timestamp, \"isCompanyPost\" : p.isCompanyPost, \"likesCount\" : LENGTH(p.likers), ");
		builder.append("\"liked\" : p.likers any == user.userId } ");
		builder.append(" ) ");
		builder.append(" ) ");
		builder.append(" LET companiesPosts = ( ");
		builder.append("for company in companies ");
		builder.append("filter company.companyId in user.followedCompanies ");
		builder.append("FOR p in posts ");
		builder.append("FILTER p.authorId == company.companyId and p.isArticle == false and p.timestamp >= @minTimestamp ");
		builder.append("SORT p.weight DESC ");
		builder.append("Limit 0, @limit ");
		builder.append("return MERGE_RECURSIVE( ");
		builder.append("{ \"authorName\": company.companyName, ");
		builder.append("\"authorProfilePictureUrl\" : company.profilePictureUrl, ");
		builder.append("\"headline\" : company.industryType ");
		builder.append("}, ");
		builder.append("{\"authorId\" : p.authorId, \"postId\" : p.postId, \"text\" : p.text, ");
		builder.append("\"images\" : p.images, \"videos\" : p.videos, \"commentsCount\" : p.commentsCount, ");
		builder.append("\"timestamp\" : p.timestamp, \"isCompanyPost\" : p.isCompanyPost, \"likesCount\" : LENGTH(p.likers), ");
		builder.append("\"liked\" : p.likers any == user.userId } ");
		builder.append(" ) ");
		builder.append(" ) ");
		builder.append("return { [ \"results\" ]: APPEND(friendPosts, companiesPosts)} ");

		String query = builder.toString();
		Map<String, Object> bindVars = new MapBuilder().get();
		bindVars.put("userId", userId);
		bindVars.put("limit", limit);
		
		int period = Integer.parseInt(config.getAppConfig("app.newsfeed.mintimestamp"));
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.WEEK_OF_MONTH, -period);
		bindVars.put("minTimestamp", calendar.getTime().getTime());
		ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null, BaseDocument.class);
		ArrayList<ReturnedPost> returnedList = new ArrayList<ReturnedPost>();
		cursor.forEachRemaining(postDocument -> {
			ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String, Object>>) postDocument.getAttribute("results");
			for(int i=0; i<list.size(); ++i){
				HashMap<String, Object> map = list.get(i);
				Iterator<String> iter = map.keySet().iterator();
				ReturnedPost returnedPost = new ReturnedPost();
				returnedList.add(returnedPost);
				while(iter.hasNext()){
					String key = iter.next();
					Object val = map.get(key);
					returnedPost.set(key, val);
				}
			}
		});
		System.out.println(returnedList);
		return returnedList;
	}

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		String rootFolder = "src/main/resources/";
		Configuration.init(rootFolder + "app.config", rootFolder + "arango.test.config",
				rootFolder + "commands.config", rootFolder + "controller.config", rootFolder + "cache.config");

		DatabaseConnection.init();
		ArangoWallHandler handler = new ArangoWallHandler();
		handler.getNewsFeed("1", 10);
	}
}
