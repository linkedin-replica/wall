package com.linkedin.replica.wall.commands.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;

import com.google.gson.JsonObject;
import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.Bookmark;

public class DeleteBookmarkCommand extends Command{

    public DeleteBookmarkCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }

    public Object execute() {
        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"userId", "postId"});


        // call dbHandler to get error or success message from dbHandler
        JsonObject request = (JsonObject) args.get("request");
        String userId = request.get("userId").getAsString();
        String postId = request.get("postId").getAsString();
        boolean response = dbHandler.deleteBookmark(userId, postId);
        return response;
    }
}

