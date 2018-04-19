package com.linkedin.replica.wall.commands.impl;

import java.io.IOException;
import java.util.HashMap;

import com.linkedin.replica.wall.cache.handlers.PostsCacheHandler;
import com.google.gson.JsonObject;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.commands.Command;

public class EditPostCommand extends Command{

    public EditPostCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    @Override
    public Object execute() throws IOException {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;
        PostsCacheHandler cacheHandler = (PostsCacheHandler) this.cacheHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"postId", "authorId", "type", "headLine", "isArticle"});

        // call dbHandler to get error or success message from dbHandler
        HashMap<String, Object> request = new HashMap<>();
        JsonObject requestArgs = (JsonObject) args.get("request");
        String postId = requestArgs.get("postId").getAsString();

        for(String key: requestArgs.keySet()) {
            switch (key) {
                case "isArticle": request.put(key, requestArgs.get(key).getAsBoolean());break;
                case "likesCount":
                case "commentsCount": request.put(key, requestArgs.get(key).getAsInt());break;
                case "images":
                case "videos": request.put(key, requestArgs.get(key).getAsJsonArray());break;
                case "postId":
                case "authorId":
                case "type":
                case "text":
                case "headLine": request.put(key, requestArgs.get(key).getAsString());break;
                default: break;
            }
        }

        String response = dbHandler.editPost(request);
        cacheHandler.editPost(postId,request);
        return response;
    }
}
