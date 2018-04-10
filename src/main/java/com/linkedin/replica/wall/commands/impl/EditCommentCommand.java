package com.linkedin.replica.wall.commands.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.Comment;

public class EditCommentCommand extends Command{

    public EditCommentCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    public Object execute() {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"commentId", "authorId", "parentPostId"});


        // call dbHandler to get error or success message from dbHandler
        Comment comment;
        String commentId = args.get("commentId").toString();
        String authorId = args.get("authorId").toString();
        String parentPostId = args.get("parentPostId").toString();

        comment = dbHandler.getComment(commentId);
        comment.setAuthorId(authorId);
        comment.setParentPostId(parentPostId);
        comment.setTimestamp(comment.getTimestamp());
        if(args.containsKey("likesCount")){
            int likesCount = (int) args.get("likesCount");
            comment.setLikesCount(likesCount);
        }
        if(args.containsKey("repliesCount")){
            int repliesCount = (int) args.get("repliesCount");
            comment.setRepliesCount(repliesCount);
        }
        if(args.containsKey("text")){
            String text = args.get("text").toString();
            comment.setText(text);
        }

        String response =  dbHandler.editComment(comment);
        return response;
    }
}

