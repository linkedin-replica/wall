package com.linkedin.replica.wall.commands.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.*;

import com.google.gson.Gson;
import com.linkedin.replica.wall.cache.handlers.PostsCacheHandler;
import com.google.gson.JsonObject;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.Post;
import com.linkedin.replica.wall.commands.Command;

public class AddPostCommand extends Command{

    public AddPostCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }

    @Override
    public Object execute() throws IllegalAccessException, IOException, InstantiationException {


        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;
        PostsCacheHandler cacheHandler = (PostsCacheHandler) this.cacheHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"authorId", "text", "images", "videos", "isCompanyPost", "isArticle"});

        // call dbHandler to get error or success message from dbHandler
        Gson gson = new Gson();
        JsonObject request = (JsonObject) args.get("request");
        String authorId = request.get("authorId").getAsString();
        String text = request.get("text").getAsString();
        String title;
        if(request.get("title") == null)
            title = null;
        else
            title = request.get("title").getAsString();

        Long timestamp = System.currentTimeMillis();
        ArrayList<String> images = gson.fromJson(request.get("images").getAsJsonArray(), ArrayList.class);
        ArrayList<String> videos = gson.fromJson(request.get("videos").getAsJsonArray(), ArrayList.class);
        boolean isArticle = request.get("isArticle").getAsBoolean();
        boolean isCompanyPost = request.get("isCompanyPost").getAsBoolean();
        Post post = new Post();
        post.setPostId(UUID.randomUUID().toString());
        post.setArticle(isArticle);
        post.setAuthorId(authorId);
        post.setImages(images);
        post.setVideos(videos);
        post.setText(text);
        post.setTimestamp(timestamp);
        post.setCompanyPost(isCompanyPost);
        post.setTitle(title);

        boolean response = dbHandler.addPost(post);
        cacheHandler.cachePost(post.getPostId(),post);
        return response;
    }
}
