package com.linkedin.replica.wall.commands.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import com.google.gson.JsonObject;
import com.linkedin.replica.wall.cache.handlers.PostsCacheHandler;
import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.ReturnedPost;

public class GetPostsCommand extends Command{

    public GetPostsCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    public Object execute() throws IOException, IllegalAccessException, NoSuchFieldException {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;
        //get cache handler
        PostsCacheHandler postsCacheHandler = (PostsCacheHandler)this.cacheHandler;
        // validate that all required arguments that are passed
        validateArgs(new String[]{"companyId", "limit"});
        // call dbHandler to list of posts from db
        JsonObject request = (JsonObject) args.get("request"); 
        String companyId = request.get("companyId").getAsString();
        int limit = request.get("limit").getAsInt();
        List<ReturnedPost> posts = (List<ReturnedPost>) postsCacheHandler.getCompanyPosts(companyId,limit,ReturnedPost.class);
        if( posts == null){
            posts = dbHandler.getPosts(companyId, limit);
            postsCacheHandler.cacheCompanyPosts(companyId,posts);
       }
        return posts;
    }

}
