package com.linkedin.replica.wall.commands.impl;

import java.util.*;

import com.google.gson.JsonObject;
import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.Bookmark;
import com.linkedin.replica.wall.models.Comment;
import com.linkedin.replica.wall.models.Reply;
import com.linkedin.replica.wall.models.ReturnedComment;

public class GetCommentsCommand extends Command{

    public GetCommentsCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    public Object execute() {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"parentPostId"});


        // call dbHandler to list of comments from db
        JsonObject request = (JsonObject) args.get("request");
        String parentPostId = request.get("parentPostId").getAsString();
        String authorId = request.get("authorId").getAsString();
        int limit = request.get("limit").getAsInt();
        List<ReturnedComment> comments = dbHandler.getComments(parentPostId, authorId, limit);
        return comments;
    }

}