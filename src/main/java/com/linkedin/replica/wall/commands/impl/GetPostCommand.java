package com.linkedin.replica.wall.commands.impl;

import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.Post;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

public class GetPostCommand extends Command{

    public GetPostCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    @Override
    public Object execute() throws NoSuchMethodException, IllegalAccessException, ParseException {
        WallHandler dbHandler = (WallHandler) this.dbHandler;
        validateArgs(new String[]{"postId"});
        String postId = args.get("postId").toString();
        Post post = dbHandler.getPost(postId);
        return post;

    }
}
