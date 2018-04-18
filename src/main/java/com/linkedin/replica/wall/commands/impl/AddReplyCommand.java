package com.linkedin.replica.wall.commands.impl;

import java.util.*;

import com.google.gson.JsonObject;
import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.Reply;

public class AddReplyCommand extends Command{

    public AddReplyCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    @Override
    public Object execute() {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"authorId", "parentPostId", "parentCommentId", "text"});

        // call dbHandler to get error or success message from dbHandler
<<<<<<< HEAD
        Reply reply;
        Gson googleJson = new Gson();
        DateFormat format = new SimpleDateFormat("EEE MMM dd yyyy hh:mm a", Locale.ENGLISH);
        String authorId = args.get("authorId").toString();
        String parentPostId = args.get("parentPostId").toString();
        String parentCommentId = args.get("parentCommentId").toString();
        ArrayList<String> mentions = googleJson.fromJson((JsonArray) args.get("mentions"), ArrayList.class);
        int likesCount = (int) args.get("likesCount");
        String text = args.get("text").toString();
        Date timestamp = format.parse(args.get("timestamp").toString());
        ArrayList<String> images = googleJson.fromJson((JsonArray) args.get("images"), ArrayList.class);
        ArrayList<String> urls = googleJson.fromJson((JsonArray) args.get("urls"), ArrayList.class);


        reply = new Reply(authorId, parentPostId, parentCommentId, mentions, likesCount, text, timestamp, images, urls);
        boolean response = dbHandler.addReply(reply);
=======
        JsonObject request = (JsonObject) args.get("request");
        String authorId = request.get("authorId").getAsString();
        String parentPostId = request.get("parentPostId").getAsString();
        String parentCommentId = request.get("parentCommentId").getAsString();
        String text = request.get("text").getAsString();
        Long timestamp = System.currentTimeMillis();

        Reply reply = new Reply();
        reply.setAuthorId(authorId);
        reply.setParentPostId(parentPostId);
        reply.setParentCommentId(parentCommentId);
        reply.setText(text);
        reply.setTimestamp(timestamp);

        String response = dbHandler.addReply(reply);
>>>>>>> 647a2048d854c73271aa19cef8dd07be6418d2dc
        return response;
    }
}

