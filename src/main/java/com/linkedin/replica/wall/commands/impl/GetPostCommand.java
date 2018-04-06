package com.linkedin.replica.wall.commands.impl;

import com.linkedin.replica.wall.cache.handlers.PostsCacheHandler;
import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.Post;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

public class GetPostCommand extends Command{

    private PostsCacheHandler cacheHandler;

    public GetPostCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    @Override
    public Object execute() throws NoSuchMethodException, IllegalAccessException, ParseException, NoSuchFieldException, IOException, InstantiationException {

        Object post;
        WallHandler dbHandler = (WallHandler) this.dbHandler;
         cacheHandler = (PostsCacheHandler) cacheHandler;
        validateArgs(new String[]{"postId"});
        String postId = args.get("postId").toString();
        post = cacheHandler.getPost((String) args.get("postId"),Post.class);
        if (post == null){
            post = dbHandler.getPost(postId);
            cacheHandler.cachePost(postId,post);
        }

        return post;

    }
}
