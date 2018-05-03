package com.linkedin.replica.wall.commands.impl;

import java.util.HashMap;
import java.util.List;

import com.google.gson.JsonObject;
import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.ReturnedPost;

public class GetBookmarksCommand extends Command{


    public GetBookmarksCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    public Object execute() {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"userId", "limit"});


        // call dbHandler to list of posts from db
        JsonObject request = (JsonObject) args.get("request"); 
        String userId = request.get("userId").getAsString();
        int limit = request.get("limit").getAsInt();

        List<ReturnedPost> bookmarks = dbHandler.getBookmarks(userId, limit);
        return bookmarks;
    }

}
