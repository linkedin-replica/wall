package com.linkedin.replica.wall.commands.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gson.JsonObject;
import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.Like;

public class GetPostLikesCommand extends Command{

    public GetPostLikesCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    public Object execute() {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"likedPostId"});


        // call dbHandler to list of likes from db
        JsonObject request = (JsonObject) args.get("request");
        String likedPostId = request.get("likedPostId").getAsString();

        List<Like> likes = dbHandler.getPostLikes(likedPostId);
        return likes;
    }

}
