package com.linkedin.replica.wall.commands.impl;

import java.util.HashMap;

import com.google.gson.JsonObject;
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
        validateArgs(new String[]{"commentId", "authorId", "parentPostId", "text"});


        // call dbHandler to get error or success message from dbHandler
        JsonObject request = (JsonObject) args.get("request");
        String commentId = request.get("commentId").getAsString();
        String authorId = request.get("authorId").getAsString();
        String parentPostId = request.get("parentPostId").getAsString();
        int likesCount = request.get("likesCount").getAsInt();
        int repliesCount = request.get("repliesCount").getAsInt();
         String text = request.get("text").getAsString();

        Comment comment = dbHandler.getComment(commentId);
        comment.setAuthorId(authorId);
        comment.setParentPostId(parentPostId);
        comment.setLikesCount(likesCount);
        comment.setRepliesCount(repliesCount);
        comment.setText(text);
        comment.setTimestamp(comment.getTimestamp());
        String response =  dbHandler.editComment(comment);
        return response;
    }
}

