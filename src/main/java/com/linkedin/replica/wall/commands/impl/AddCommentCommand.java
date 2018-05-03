package com.linkedin.replica.wall.commands.impl;

import java.util.HashMap;
import java.util.UUID;

import com.google.gson.JsonObject;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.Comment;
import com.linkedin.replica.wall.commands.Command;

public class AddCommentCommand extends Command{


    public AddCommentCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    public Object execute() {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"userId", "parentPostId", "text"});


        // call dbHandler to get error or success message from dbHandler
        JsonObject request = (JsonObject) args.get("request");
        String authorId = request.get("userId").getAsString();
        String parentPostId = request.get("parentPostId").getAsString();
        String text = request.get("text").getAsString();
        Long timestamp = System.currentTimeMillis();

        Comment comment = new Comment();
        comment.setCommentId(UUID.randomUUID().toString());
        comment.setAuthorId(authorId);
        comment.setParentPostId(parentPostId);
        comment.setText(text);
        comment.setTimestamp(timestamp);
        boolean response =  dbHandler.addComment(comment);
        return response;
    }
}

