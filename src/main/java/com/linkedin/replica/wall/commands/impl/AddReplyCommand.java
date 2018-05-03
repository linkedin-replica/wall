package com.linkedin.replica.wall.commands.impl;

import java.util.*;

import com.google.gson.JsonObject;
import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.Reply;

public class AddReplyCommand extends Command{

    public AddReplyCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    @Override
    public Object execute() {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"userId", "parentPostId", "parentCommentId", "text"});

        // call dbHandler to get error or success message from dbHandler
        JsonObject request = (JsonObject) args.get("request");
        String authorId = request.get("userId").getAsString();
        String parentPostId = request.get("parentPostId").getAsString();
        String parentCommentId = request.get("parentCommentId").getAsString();
        String text = request.get("text").getAsString();
        Long timestamp = System.currentTimeMillis();

        Reply reply = new Reply();
        reply.setReplyId(UUID.randomUUID().toString());
        reply.setAuthorId(authorId);
        reply.setParentPostId(parentPostId);
        reply.setParentCommentId(parentCommentId);
        reply.setText(text);
        reply.setTimestamp(timestamp);

        boolean response = dbHandler.addReply(reply);
        return response;
    }
}

