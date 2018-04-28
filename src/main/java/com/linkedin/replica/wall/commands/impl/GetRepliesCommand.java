package com.linkedin.replica.wall.commands.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gson.JsonObject;
import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.Post;
import com.linkedin.replica.wall.models.Reply;
import com.linkedin.replica.wall.models.ReturnedReply;

public class GetRepliesCommand extends Command{

    public GetRepliesCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    public Object execute() {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"parentCommentId", "authorId", "limit"});


        // call dbHandler to list of replies from db
        JsonObject request = (JsonObject) args.get("request");
        String parentCommentId = request.get("parentCommentId").getAsString();
        String authorId = request.get("authorId").getAsString();
        int limit = request.get("limit").getAsInt();

        List<ReturnedReply> replies = dbHandler.getReplies(parentCommentId, authorId, limit);
        return replies;
    }


}
