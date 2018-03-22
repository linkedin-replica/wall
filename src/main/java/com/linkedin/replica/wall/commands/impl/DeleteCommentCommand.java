package com.linkedin.replica.wall.commands.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.Comment;

public class DeleteCommentCommand extends Command{

    public DeleteCommentCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }

    public Object execute() {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"commentId", "authorId", "parentPostId", "likesCount", "repliesCount", "images", "urls", "mentions", "text"});


        // call dbHandler to get error or success message from dbHandler
        Comment comment;
        String commentId = args.get("commentId").toString();
        String authorId = args.get("authorId").toString();
        String parentPostId = args.get("parentPostId").toString();
        Integer likesCount = Integer.parseInt(args.get("likesCount").toString());
        Integer repliesCount = Integer.parseInt(args.get("repliesCount").toString());
        ArrayList<String> images = new ArrayList<String>(Arrays.asList(args.get("images").toString().split(",")));
        ArrayList<String> urls = new ArrayList<String>(Arrays.asList(args.get("urls").toString().split(",")));
        ArrayList<String> mentions = new ArrayList<String>(Arrays.asList(args.get("mentions").toString().split(",")));
        String text = args.get("text").toString();
        String timeStamp = args.get("timeStamp").toString();
        comment = new Comment(commentId, authorId, parentPostId, likesCount, repliesCount, images, urls,mentions,text,timeStamp);
        String response =  dbHandler.deleteComment(comment);
        return response;
    }
}