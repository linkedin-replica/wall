package com.linkedin.replica.wall.commands.impl;

import java.util.HashMap;
import java.util.*;

import com.google.gson.Gson;
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
    public Object execute() {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"authorId", "type", "text", "headLine", "isArticle"});

        // call dbHandler to get error or success message from dbHandler
        Gson gson = new Gson();
        JsonObject request = (JsonObject) args.get("request");
        String authorId = request.get("authorId").getAsString();
        String type = request.get("type").getAsString();
        String text = request.get("text").getAsString();
        String headLine = request.get("headLine").getAsString();
        Long timestamp = System.currentTimeMillis();
        ArrayList<String> images = gson.fromJson(request.get("images").getAsJsonArray(), ArrayList.class);
        ArrayList<String> videos = gson.fromJson(request.get("videos").getAsJsonArray(), ArrayList.class);
        boolean isArticle = request.get("isArticle").getAsBoolean();

        Post post = new Post();
        post.setArticle(isArticle);
        post.setHeadLine(headLine);
        post.setAuthorId(authorId);
        post.setImages(images);
        post.setVideos(videos);
        post.setType(type);
        post.setText(text);
        post.setTimestamp(timestamp);

        boolean response = dbHandler.addPost(post);
        return response;
    }
}
