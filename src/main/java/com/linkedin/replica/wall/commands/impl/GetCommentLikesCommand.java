package com.linkedin.replica.wall.commands.impl;

import com.google.gson.JsonObject;
import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;

import java.util.HashMap;
import java.util.List;

public class GetCommentLikesCommand extends Command{

    public GetCommentLikesCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    public Object execute() {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"likedCommentId"});


        // call dbHandler to list of likes from db
        JsonObject request = (JsonObject) args.get("request");
        String likedCommentId = request.get("likedCommentId").getAsString();

        List<Like> likes = dbHandler.getCommentLikes(likedCommentId);
        return likes;
    }



}
