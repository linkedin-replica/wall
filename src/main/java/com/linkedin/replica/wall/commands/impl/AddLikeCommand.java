package com.linkedin.replica.wall.commands.impl;


import java.util.HashMap;
import java.util.LinkedHashMap;

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
        Like like;
        String likerId = args.get("likerId").toString();
        String firstName = args.get("firstName").toString();
        String lastName = args.get("lastName").toString();
        String imageUrl = args.get("imageUrl").toString();
        String likedPostId = (String) args.get("likedPostId");
        String likedCommentId = (String) args.get("likedCommentId");
        String likedReplyId = (String) args.get("likedReplyId");

        like = new Like();
        like.setFirstName(firstName);
        like.setImageUrl(imageUrl);
        like.setLastName(lastName);
        like.setLikedCommentId(likedCommentId);
        like.setLikedPostId(likedPostId);
        like.setLikedReplyId(likedReplyId);
        like.setLikerId(likerId);

        String response = dbHandler.addLike(like);
        return response;
    }
}

