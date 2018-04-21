package com.linkedin.replica.wall.commands.impl;


import java.util.HashMap;

import com.google.gson.JsonObject;
import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;

public class DeleteLikeFromPostCommand extends Command{

    public DeleteLikeFromPostCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    @Override
    public Object execute() {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"likerId", "postId"});

        // call dbHandler to get error or success message from dbHandler
        JsonObject request = (JsonObject) args.get("request");
        String postId = null;
        String likerId = null ;
        if(request.get("likerId") != null)
            likerId = request.get("likerId").getAsString();
        if(request.get("postId") != null)
            postId = request.get("postId").getAsString();

        boolean response = dbHandler.deleteLikeFromPost(likerId,postId);
        return response;
    }
}

