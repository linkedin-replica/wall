package com.linkedin.replica.wall.commands.impl;

import java.util.*;
import com.linkedin.replica.wall.cache.handlers.PostsCacheHandler;
import com.google.gson.JsonObject;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.Post;
import com.linkedin.replica.wall.commands.Command;
public class DeletePostCommand extends Command{

    public DeletePostCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    @Override
    public Object execute() {
        WallHandler dbHandler = (WallHandler) this.dbHandler;
        PostsCacheHandler postsCacheHandler = (PostsCacheHandler)this.cacheHandler;
        validateArgs(new String[]{"postId"});

        // call dbHandler to get error or success message from dbHandler
        JsonObject request = (JsonObject) args.get("request");
        String postId = request.get("postId").getAsString();
        boolean response = dbHandler.deletePost(postId);
        postsCacheHandler.deletePost(postId);
        return response;
    }
}
