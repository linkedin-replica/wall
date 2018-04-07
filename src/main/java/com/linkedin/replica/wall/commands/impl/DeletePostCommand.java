package com.linkedin.replica.wall.commands.impl;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.linkedin.replica.wall.cache.handlers.PostsCacheHandler;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.Post;
import com.linkedin.replica.wall.commands.Command;

public class DeletePostCommand extends Command{

    public DeletePostCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    @Override
    public Object execute() throws ParseException {
        WallHandler dbHandler = (WallHandler) this.dbHandler;
        PostsCacheHandler postsCacheHandler = (PostsCacheHandler)this.cacheHandler;
        validateArgs(new String[]{"postId"});
        Post post;
        String postId = args.get("postId").toString();
        post = dbHandler.getPost(postId);
        String response = dbHandler.deletePost(post);
        postsCacheHandler.deletePost(postId);
        return response;
    }
}
