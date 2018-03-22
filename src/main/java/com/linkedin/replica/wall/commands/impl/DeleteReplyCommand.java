package com.linkedin.replica.wall.commands.impl;

import java.util.LinkedHashMap;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.arangodb.velocypack.VPackParser;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.Reply;

public class DeleteReplyCommand extends Command{

    public DeleteReplyCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    @Override
    public Object execute() throws ParseException {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"replyId", "authorId", "parentPostId", "parentCommentId", "mentions", "likesCount", "text", "images", "urls"});

        // call dbHandler to get error or success message from dbHandler
        Reply reply;
        Gson googleJson = new Gson();
        DateFormat format = new SimpleDateFormat("EEE MMM dd yyyy hh:mm a", Locale.ENGLISH);
        String replyId = args.get("replyId").toString();
        String authorId = args.get("authorId").toString();
        String parentPostId = args.get("parentPostId").toString();
        String parentCommentId = args.get("parentCommentId").toString();
        ArrayList<String> mentions = googleJson.fromJson((JsonArray) args.get("mentions"), ArrayList.class);
        int likesCount = (int) args.get("likesCount");
        String text = args.get("text").toString();
        Date timestamp = format.parse(args.get("timestamp").toString());
        ArrayList<String> images = googleJson.fromJson((JsonArray) args.get("images"), ArrayList.class);
        ArrayList<String> urls = googleJson.fromJson((JsonArray) args.get("urls"), ArrayList.class);

        reply = new Reply(replyId, authorId, parentPostId, parentCommentId, mentions, likesCount, text, timestamp, images, urls);
        String response = dbHandler.deleteReply(reply);
        return response;
    }
}

