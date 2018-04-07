package com.linkedin.replica.wall.commands.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.Comment;
import com.linkedin.replica.wall.commands.Command;

public class AddCommentCommand extends Command{


    public AddCommentCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    public Object execute() throws ParseException {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"authorId", "parentPostId", "likesCount", "repliesCount", "images", "urls", "mentions", "text", "timestamp"});


        // call dbHandler to get error or success message from dbHandler
        Comment comment;

        Gson googleJson = new Gson();
        DateFormat format = new SimpleDateFormat("EEE MMM dd yyyy hh:mm a", Locale.ENGLISH);
        String authorId = args.get("authorId").toString();
        String parentPostId = args.get("parentPostId").toString();
        int likesCount = (int) args.get("likesCount");
        int repliesCount = (int) args.get("repliesCount");
        ArrayList<String> images = googleJson.fromJson((JsonArray) args.get("images"), ArrayList.class);
        ArrayList<String> urls = googleJson.fromJson((JsonArray) args.get("urls"), ArrayList.class);
        ArrayList<String> mentions = googleJson.fromJson((JsonArray) args.get("mentions"), ArrayList.class);
        String text = args.get("text").toString();
        Date timestamp = format.parse(args.get("timestamp").toString());
        comment = new Comment(authorId, parentPostId, likesCount, repliesCount, images, urls,mentions,text,timestamp);
        String response =  dbHandler.addComment(comment);
        return response;
    }
}

