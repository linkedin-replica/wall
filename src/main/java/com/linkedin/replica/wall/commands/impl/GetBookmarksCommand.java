package com.linkedin.replica.wall.commands.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.Bookmark;
import com.linkedin.replica.wall.models.Post;

public class GetBookmarksCommand extends Command{


    public GetBookmarksCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    public Object execute() {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"userId"});

        // call dbHandler to list of bookmarks from db
        String userId = args.get("userId").toString();
        String postId = args.get("postId").toString();
        Bookmark bookmark = new Bookmark(userId, postId);

        List<Bookmark> bookmarks = dbHandler.getBookmarks(userId);
        return bookmarks;
    }

}
