package com.linkedin.replica.wall.commands.impl;

import com.google.gson.JsonObject;
import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;

import java.util.HashMap;

public class DeleteLikeFromReplyCommand extends Command{

    public DeleteLikeFromReplyCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }

    @Override
    public Object execute() {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"userId", "replyId"});

        // call dbHandler to get error or success message from dbHandler
        JsonObject request = (JsonObject) args.get("request");
        String replyId = null;
        String likerId = null ;
        if(request.get("userId") != null)
            likerId = request.get("userId").getAsString();
        if(request.get("replyId") != null)
            replyId = request.get("replyId").getAsString();

        boolean response = dbHandler.deleteLikeFromReply(likerId,replyId);
        return response;
    }

}
