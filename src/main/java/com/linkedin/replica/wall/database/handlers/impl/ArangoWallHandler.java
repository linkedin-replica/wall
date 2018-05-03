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

	public ArangoWallHandler() {
		arangoDB = DatabaseConnection.getInstance().getArangodb();
		config = Configuration.getInstance();
		dbName = config.getArangoConfig("arangodb.name");
		likesCollection = config.getArangoConfig("collections.likes.name");
		repliesCollection = config.getArangoConfig("collections.replies.name");
		usersCollection = config.getArangoConfig("collections.users.name");
		commentsCollection = config.getArangoConfig("collections.comments.name");
		postsCollection = config.getArangoConfig("collections.posts.name");

	}

    /**
     * method to update user's bookmarks list by adding new bookmark.
     *
     * @param postId to be added.
     * @param userId
     * @return message tells whether the process is successful or failed.
     */
    public boolean addBookmark(String userId, String postId) throws ArangoDBException {
        String query = " FOR user in " + usersCollection
                + " FILTER user._key == @userId\t"
                + "UPDATE user WITH { bookmarkedPosts : PUSH(user.bookmarkedPosts, @postId) } IN " + usersCollection;
        Map<String, Object> bindVars = new MapBuilder().put("userId", userId).get();
        bindVars.put("postId", postId);
        arangoDB.db(dbName).query(query, bindVars, null, BaseDocument.class);
        return true;
    }

    /**
     * method to update user's bookmarks list by deleting new bookmark.
     * @param postId to be deleted
     * @param userId
     * @return message tells whether the process is successful or failed.
     */
    public boolean deleteBookmark(String userId, String postId) throws ArangoDBException {
        String query = " FOR user in " + usersCollection
                + " FILTER user._key == @userId\t"
                + "UPDATE user WITH { bookmarkedPosts : REMOVE_VALUE(user.bookmarkedPosts, @postId) } IN " + usersCollection;
        Map<String, Object> bindVars = new MapBuilder().put("userId", userId).get();
        bindVars.put("postId", postId);
        arangoDB.db(dbName).query(query, bindVars, null, BaseDocument.class);
        return true;
    }

    /**
     * method to get user's bookmarks.
     * @param userId
     * @return list of users bookmarks.
     */

    public ArrayList<ReturnedPost> getBookmarks(String userId, int limit) throws ArangoDBException {
		String query = config.getQueryConfigProp("get.bookmarks.query");
		Map<String, Object> bindVars = new MapBuilder().get();
		bindVars.put("userId", userId);
		bindVars.put("limit", limit);

		ArangoCursor<ArrayList> cursor = arangoDB.db(dbName).query(query, bindVars, null, ArrayList.class);
		ArrayList<ReturnedPost> returnedList = new ArrayList<ReturnedPost>();
		cursor.forEachRemaining(list -> {
				HashMap<String, Object> map = (HashMap<String, Object> ) list.get(0);
				Iterator<String> iter = map.keySet().iterator();
				ReturnedPost returnedPost = new ReturnedPost();
				returnedList.add(returnedPost);
				while(iter.hasNext()){
					String key = iter.next();
					Object val = map.get(key);
					returnedPost.set(key, val);
			}
		});
		return returnedList;
    }

    /**
     * function to get posts of specific user.
     * @param companyId
     * @param limit
     * @return
     */
    public List<ReturnedPost> getPosts(String companyId, int limit) throws ArangoDBException {
		String query = config.getQueryConfigProp("get.companies.posts.query");
		Map<String, Object> bindVars = new MapBuilder().get();
		bindVars.put("companyId", companyId);
		bindVars.put("limit", limit);
		
		ArrayList<ReturnedPost> returnedList = new ArrayList<ReturnedPost>();
        ArangoCursor<ReturnedPost> cursor = arangoDB.db(dbName).query(query, bindVars, null,
        		ReturnedPost.class);
        cursor.forEachRemaining(postDocument -> {
        	returnedList.add(postDocument);
        });
        return returnedList;
    }

    /**
     * function to get specific post in database.
     * @param postId
     * @return
     */
    public ReturnedPost getArticle(String postId, String userId) throws ArangoDBException{
        String query = config.getQueryConfigProp("get.articles.query");
        Map<String, Object> bindVars = new MapBuilder().get();
        bindVars.put("postId", postId);
        bindVars.put("userId", userId);

        ArrayList<ReturnedPost> returnedList = new ArrayList<ReturnedPost>();
        ArangoCursor<ReturnedPost> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                ReturnedPost.class);
        
        if(cursor.hasNext())
        	return cursor.next();
        else 
        	return null;
//        cursor.forEachRemaining(articleDocument -> {
//            returnedList.add(articleDocument);
//        });
//        return returnedList.get(0);
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
     * @param postId
     * @return
     */
    public boolean deletePost(String postId) throws ArangoDBException{
        String query = "FOR post IN " + postsCollection + " FILTER post._key == @postId\t"
                + "REMOVE post IN " + postsCollection;
        Map<String, Object> bindVars = new MapBuilder().put("postId", postId).get();
        arangoDB.db(dbName).query(query, bindVars, null,
                Post.class);
        return true;
    }


    /**
     * function to get list of comments on specific post.
     * @param postId
     * @return
     */
    public List<ReturnedComment> getComments(String postId, String userId, int limit ) throws ArangoDBException {
        String query = config.getQueryConfigProp("get.comments.query");
        Map<String, Object> bindVars = new MapBuilder().get();
        bindVars.put("postId", postId);
        bindVars.put("userId", userId);
        bindVars.put("limit", limit);
        ArrayList<ReturnedComment> returnedList = new ArrayList<ReturnedComment>();
        ArangoCursor<ReturnedComment> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                ReturnedComment.class);
        cursor.forEachRemaining(commentDocument -> {
            returnedList.add(commentDocument);
        });
        return returnedList;
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
        String query = "Insert @comment IN " + commentsCollection;
        query +=" FOR post in " + postsCollection
                + " FILTER post._key == @parentPostId\t"
                + "UPDATE post WITH { commentsCount : post.commentsCount + 1 } IN " + postsCollection;
        Map<String, Object> bindVars = new MapBuilder().put("parentPostId", comment.getParentPostId()).get();
        bindVars.put("comment", comment);
        arangoDB.db(dbName).query(query, bindVars, null, BaseDocument.class);
        return true;
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
     * @param commentId
     * @return
     */

    public boolean deleteComment(String commentId) throws ArangoDBException {
        String query = "FOR comment IN " + commentsCollection + " FILTER comment._key == @commentId\t"
                + "REMOVE comment IN " + commentsCollection;
        query += " FOR post in " + postsCollection
                + " FILTER post._key == comment.parentPostId\t"
                + "UPDATE post WITH { commentsCount : post.commentsCount - 1 } IN " + postsCollection;
        Map<String, Object> bindVars = new MapBuilder().put("commentId", commentId).get();
        arangoDB.db(dbName).query(query, bindVars, null, BaseDocument.class);
        return  true;
    }

    public List<ReturnedReply> getReplies(String commentId, String userId, int limit) throws ArangoDBException {
        String query = config.getQueryConfigProp("get.replies.query");
        Map<String, Object> bindVars = new MapBuilder().get();
        bindVars.put("commentId", commentId);
        bindVars.put("userId", userId);
        bindVars.put("limit", limit);
        ArrayList<ReturnedReply> returnedList = new ArrayList<ReturnedReply>();
        ArangoCursor<ReturnedReply> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                ReturnedReply.class);
        cursor.forEachRemaining(commentDocument -> {
            returnedList.add(commentDocument);
        });
        return returnedList;
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
        String query = "Insert @reply IN " + repliesCollection;
        query += " FOR comment in " + commentsCollection
                + " FILTER comment._key == @parentCommentId\t"
                + "UPDATE comment WITH { repliesCount : comment.repliesCount + 1 } IN " + commentsCollection;
        query += " FOR post in " + postsCollection
                + " FILTER post._key == @parentPostId\t"
                + "UPDATE post WITH { commentsCount : post.commentsCount + 1 } IN " + postsCollection;
        Map<String, Object> bindVars = new MapBuilder().put("parentPostId", reply.getParentPostId()).get();
        bindVars.put("parentCommentId", reply.getParentCommentId());
        bindVars.put("reply", reply);
        arangoDB.db(dbName).query(query, bindVars, null, BaseDocument.class);
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
     * @param replyId
     * @return
     */
    public boolean deleteReply(String replyId) throws ArangoDBException{
            String query = "FOR reply IN " + repliesCollection + " FILTER reply._key == @replyId\t"
                    + "REMOVE reply IN " + repliesCollection;
            // update comment query
            query += " FOR comment in " + commentsCollection
                    + " FILTER comment._key == reply.parentCommentId\t"
                    + "UPDATE comment WITH { repliesCount : comment.repliesCount - 1 } IN " + commentsCollection;
        // update post query
        query += " FOR post in " + postsCollection
                    + " FILTER post._key == reply.parentPostId\t"
                    + "UPDATE post WITH { commentsCount : post.commentsCount - 1 } IN " + postsCollection;
        Map<String, Object> bindVars = new MapBuilder().put("replyId", replyId).get();
            arangoDB.db(dbName).query(query, bindVars, null, BaseDocument.class);
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
		String query = config.getQueryConfigProp("get.newsfeed.query");
		Map<String, Object> bindVars = new MapBuilder().get();
		bindVars.put("userId", userId);
		bindVars.put("limit", limit);
		
		int period = Integer.parseInt(config.getAppConfig("app.newsfeed.mintimestamp"));
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.WEEK_OF_MONTH, -period);
        System.out.println(userId);
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
		return returnedList;
	}

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		String rootFolder = "src/main/resources/";
		Configuration.init(rootFolder + "app.config", rootFolder + "arango.test.config",
				rootFolder + "commands.config", rootFolder + "controller.config", rootFolder + "cache.config",
				rootFolder + "query.config");

		DatabaseConnection.init();
		ArangoWallHandler handler = new ArangoWallHandler();
		System.out.println(handler.getNewsFeed("1", 10));
//		System.out.println(handler.getPosts("12", 10));
//		System.out.println(handler.getArticle("4", "1"));
//		System.out.println(handler.getComments("1", "1", 10));
//		System.out.println(handler.getReplies("1", "1", 10));
//		System.out.println(handler.getBookmarks("1", 10));
		DatabaseConnection.getInstance().closeConnections();

//		Properties properties = new Properties();
//		properties.load(new FileInputStream("src/main/resources/query.config"));
//		System.out.println(properties.getProperty("get.newsfeed.query"));
//		System.out.println(properties.getProperty("get.companies.post.query"));
	}
}
