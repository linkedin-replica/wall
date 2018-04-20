package com.linkedin.replica.wall.commands.impl;


import java.util.HashMap;
import java.util.LinkedHashMap;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.Like;

public class AddLikeCommand extends Command{

    public AddLikeCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    @Override
    public Object execute() {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"likerId", "firstName", "lastName", "imageUrl", "likedPostId", "likedCommentId", "likedReplyId"});

        // call dbHandler to get error or success message from dbHandler
        JsonObject request = (JsonObject) args.get("request");
        String likedPostId = null;
        String likedCommentId = null ;
        String likedReplyId = null;
        if(request.get("likedPostId") != null)
            likedPostId = request.get("likedPostId").getAsString();
        else if(request.get("likedCommentId") != null)
            likedCommentId = request.get("likedCommentId").getAsString();
        else if(request.get("likedReplyId").getAsString() != null)
            likedReplyId = request.get("likedReplyId").getAsString();
        String likerId = request.get("likerId").getAsString();
        String firstName = request.get("firstName").getAsString();
        String lastName = request.get("lastName").getAsString();
        String imageUrl = request.get("imageUrl").getAsString();

        Like like = new Like();
        like.setFirstName(firstName);
        like.setImageUrl(imageUrl);
        like.setLastName(lastName);
        like.setLikedCommentId(likedCommentId);
        like.setLikedPostId(likedPostId);
        like.setLikedReplyId(likedReplyId);
        like.setLikerId(likerId);

        boolean response = dbHandler.addLike(like);
        return response;
    }
}

