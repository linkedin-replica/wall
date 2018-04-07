package com.linkedin.replica.wall.commands.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.Comment;
import com.linkedin.replica.wall.models.Post;
import com.linkedin.replica.wall.models.UserProfile;

import java.util.ArrayList;
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
        validateArgs(new String[]{"user","limit","offset"});


        // call dbHandler to get error or success message from dbHandler
        UserProfile user;

        Gson googleJson = new Gson();
        user = (UserProfile) args.get("user");
        int limit = (int) args.get("limit");
        int offset = (int) args.get("offset");


        ;
        List<Post> retrievedPosts = dbHandler.getFriendsPosts(user,limit,offset);

        return retrievedPosts;
    }
}
