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
        validateArgs(new String[]{"commentId", "authorId", "parentPostId", "likesCount", "repliesCount", "images", "urls", "mentions", "text"});


        // call dbHandler to get error or success message from dbHandler
        Comment comment;
        Gson googleJson = new Gson();
        String commentId = args.get("commentId").toString();
        String authorId = args.get("authorId").toString();
        String parentPostId = args.get("parentPostId").toString();
        int likesCount = (int) args.get("likesCount");
        int repliesCount = (int) args.get("repliesCount");
        ArrayList<String> images = googleJson.fromJson((JsonArray) args.get("images"), ArrayList.class);
        ArrayList<String> urls = googleJson.fromJson((JsonArray) args.get("urls"), ArrayList.class);
        ArrayList<String> mentions = googleJson.fromJson((JsonArray) args.get("mentions"), ArrayList.class);
        String text = args.get("text").toString();
        String timestamp = args.get("timestamp").toString();
        comment = new Comment(commentId, authorId, parentPostId, likesCount, repliesCount, images, urls,mentions,text,timestamp);
        String response =  dbHandler.editComment(comment);
        return response;
    }
}
