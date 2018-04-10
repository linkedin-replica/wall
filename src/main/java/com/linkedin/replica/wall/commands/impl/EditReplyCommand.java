package com.linkedin.replica.wall.commands.impl;

import java.text.ParseException;
import java.util.*;

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
        validateArgs(new String[]{"replyId", "authorId", "parentPostId", "parentCommentId", "likesCount", "text"});

        // call dbHandler to get error or success message from dbHandler
        Reply reply;
        String replyId = args.get("replyId").toString();
        String authorId = args.get("authorId").toString();
        String parentPostId = args.get("parentPostId").toString();
        String parentCommentId = args.get("parentCommentId").toString();
        int likesCount = (int) args.get("likesCount");
        String text = (String) args.get("text");

        reply = dbHandler.getReply(replyId);
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
