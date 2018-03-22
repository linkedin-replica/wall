package com.linkedin.replica.wall.commands.impl;

import java.util.LinkedHashMap;
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
        validateArgs(new String[]{"replyId", "authorId", "parentPostId", "parentCommentId", "mentions", "likesCount", "text", "images", "urls"});

        // call dbHandler to get error or success message from dbHandler
        Reply reply;
        DateFormat format = new SimpleDateFormat("EEE MMM dd yyyy hh:mm a", Locale.ENGLISH);
        String replyId = args.get("replyId").toString();
        String authorId = args.get("authorId").toString();
        String parentPostId = args.get("parentPostId").toString();
        String parentCommentId = args.get("parentCommentId").toString();
        ArrayList<String> mentions = new ArrayList<String>(Arrays.asList(args.get("mentions").toString().split(",")));
        Long likesCount = Long.parseLong(args.get("likesCount").toString());
        String text = (String) args.get("text");
        Date timestamp = format.parse(args.get("timestamp").toString());
        ArrayList<String> images = new ArrayList<String>(Arrays.asList(args.get("images").toString().split(",")));
        ArrayList<String> urls = new ArrayList<String>(Arrays.asList(args.get("urls").toString().split(",")));

        reply = new Reply(replyId, authorId, parentPostId, parentCommentId, mentions, likesCount, text, timestamp, images, urls);
        String response = dbHandler.addReply(reply);
        return response;
    }
}

