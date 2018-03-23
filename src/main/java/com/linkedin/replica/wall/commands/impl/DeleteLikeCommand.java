package com.linkedin.replica.wall.commands.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;

import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.Like;

public class DeleteLikeCommand extends Command{

    public DeleteLikeCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    @Override
    public Object execute() {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"likerId", "userName", "headLine", "imageUrl", "likedPostId", "likedCommentId", "likedReplyId"});

        // call dbHandler to get error or success message from dbHandler
        Like like;
        String likerId = args.get("likerId").toString();
        String userName = args.get("userName").toString();
        String headLine = args.get("headLine").toString();
        String imageUrl = args.get("imageUrl").toString();
        String likedPostId = args.get("likedPostId").toString();
        String likedCommentId = args.get("likedCommentId").toString();
        String likedReplyId = args.get("likedReplyId").toString();
        like = new Like(likerId, likedPostId, likedCommentId, likedReplyId, userName, headLine,imageUrl);

        String response = dbHandler.deleteLike(like);
        return response;
    }
}
