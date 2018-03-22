package com.linkedin.replica.wall.commands.impl;

import java.util.*;

import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.Bookmark;
import com.linkedin.replica.wall.models.Comment;
import com.linkedin.replica.wall.models.Reply;

public class GetCommentsCommand extends Command{

    public GetCommentsCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    public Object execute() {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"parentPostId"});


        // call dbHandler to list of comments from db
        String parentPostId = args.get("parentPostId").toString();

        List<Comment> comments = dbHandler.getComments(parentPostId);
        return comments;
    }

}