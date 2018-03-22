package com.linkedin.replica.wall.commands.impl;

import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.Bookmark;
import com.linkedin.replica.wall.models.Like;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class GetCommentLikesCommand extends Command{

    public GetCommentLikesCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    public Object execute() {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"likedCommentId"});


        // call dbHandler to list of likes from db
        String likedCommentId = args.get("likedCommentId").toString();

        List<Like> likes = dbHandler.getCommentLikes(likedCommentId);
        return likes;
    }



}
