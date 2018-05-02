package com.linkedin.replica.wall.commands.impl;


import com.google.gson.JsonObject;
import com.linkedin.replica.wall.cache.handlers.PostsCacheHandler;
import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.ReturnedPost;

import java.io.IOException;
import java.util.HashMap;

public class GetArticleCommand extends Command {


	public GetArticleCommand(HashMap<String, Object> args, DatabaseHandler dbHandler) {
		super(args, dbHandler);
	}


	@Override
	public Object execute() throws IllegalAccessException, NoSuchFieldException, IOException, InstantiationException {

		WallHandler dbHandler = (WallHandler) this.dbHandler;
		PostsCacheHandler postsCacheHandler = (PostsCacheHandler) this.cacheHandler;
		validateArgs(new String[]{"postId", "userId"});
		JsonObject request = (JsonObject) args.get("request");
		String postId = request.get("postId").getAsString();
		String userId = request.get("userId").getAsString();
		Object post = postsCacheHandler.getPost(postId, ReturnedPost.class);
		if (post == null) {
			dbHandler.getArticle(postId, userId);
			postsCacheHandler.cachePost(postId, post);
		}
		return post;

	}
}
