package com.linkedin.replica.wall.commands.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gson.JsonObject;
import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.Comment;
import com.linkedin.replica.wall.models.Post;

public class GetPostsCommand extends Command{

    public GetPostsCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    public Object execute() {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"authorId"});


        // call dbHandler to list of posts from db
        JsonObject request = (JsonObject) args.get("request");
        String authorID = request.get("authorId").getAsString();

        List<Post> posts = dbHandler.getPosts(authorID);
        return posts;
    }

}
