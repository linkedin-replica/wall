package com.linkedin.replica.wall.commands.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.Reply;

public class AddReplyCommand extends Command{

    public AddReplyCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    @Override
    public Object execute() throws ParseException {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"authorId", "parentPostId", "parentCommentId", "likesCount", "text"});

        // call dbHandler to get error or success message from dbHandler
        Reply reply;
        String authorId = args.get("authorId").toString();
        String parentPostId = args.get("parentPostId").toString();
        String parentCommentId = args.get("parentCommentId").toString();
        int likesCount = (int) args.get("likesCount");
        String text = args.get("text").toString();
        Long timestamp = System.currentTimeMillis();

        reply = new Reply();
        reply.setAuthorId(authorId);
        reply.setParentPostId(parentPostId);
        reply.setParentCommentId(parentCommentId);
        reply.setLikesCount(likesCount);
        reply.setText(text);
        reply.setTimestamp(timestamp);

        String response = dbHandler.addReply(reply);
        return response;
    }
}

