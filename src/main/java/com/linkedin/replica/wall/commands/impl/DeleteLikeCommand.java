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
        validateArgs(new String[]{"likeId"});

        // call dbHandler to get error or success message from dbHandler
        Like like;
        String likeId = args.get("likeId").toString();
        like = dbHandler.getLike(likeId);
        boolean response = dbHandler.deleteLike(like);
        return response;
    }
}
