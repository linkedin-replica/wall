package com.linkedin.replica.wall.commands.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.Post;
import com.linkedin.replica.wall.models.ReturnedPost;
import com.linkedin.replica.wall.models.UserProfile;

import java.util.HashMap;
import java.util.List;

public class GetNewsfeedCommand extends Command{

    public GetNewsfeedCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    public Object execute() {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"userId","limit"});


        // call dbHandler to get error or success message from dbHandler
        JsonObject request = (JsonObject) args.get("request");
        String userId = request.get("userId").getAsString();
        int limit = request.get("limit").getAsInt();

        List<ReturnedPost> retrievedPosts = dbHandler.getNewsFeed(userId,limit);

        return retrievedPosts;
    }
}
