package com.linkedin.replica.wall.commands.impl;

import com.google.gson.Gson;
import com.linkedin.replica.wall.cache.handlers.PostsCacheHandler;
import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.Post;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

public class GetArticleCommand extends Command {

	public GetArticleCommand(HashMap<String, Object> args, DatabaseHandler dbHandler) {
		super(args, dbHandler);
	}

	@Override
	public Object execute() throws IllegalAccessException, NoSuchFieldException, IOException, InstantiationException {

		WallHandler dbHandler = (WallHandler) this.dbHandler;
		PostsCacheHandler postsCacheHandler = (PostsCacheHandler) this.cacheHandler;
		validateArgs(new String[] { "postId", "userId" });
		String postId = args.get("postId").toString();
		String userId = args.get("userId").toString();
		Object post = postsCacheHandler.getPost(postId, Post.class);
		if (post == null) {
			post = dbHandler.getArticle(postId, userId);
			postsCacheHandler.cachePost(postId, post);
		}
		if (post != null)
			return post;
		else
			return new Object();
	}
}