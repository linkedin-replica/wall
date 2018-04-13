package com.linkedin.replica.wall.commands.impl;

import java.text.ParseException;
import java.util.*;

import com.google.gson.JsonObject;
import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.Reply;

public class EditReplyCommand extends Command{

    public EditReplyCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    @Override
    public Object execute() throws ParseException {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"replyId", "authorId", "parentPostId", "parentCommentId", "text"});

        // call dbHandler to get error or success message from dbHandler
        JsonObject request = (JsonObject) args.get("request");
        String replyId = request.get("replyId").getAsString();
        String authorId = request.get("authorId").getAsString();
        String parentPostId = request.get("parentPostId").getAsString();
        String parentCommentId = request.get("parentCommentId").getAsString();
        int likesCount = request.get("likesCount").getAsInt();
        String text = request.get("text").getAsString();

        Reply reply = dbHandler.getReply(replyId);
        reply.setAuthorId(authorId);
        reply.setParentPostId(parentPostId);
        reply.setParentCommentId(parentCommentId);
        reply.setLikesCount(likesCount);
        reply.setTimestamp(reply.getTimestamp());
        reply.setText(text);
        String response = dbHandler.editReply(reply);
        return response;
    }
}
