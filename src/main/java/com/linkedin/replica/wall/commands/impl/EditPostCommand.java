package com.linkedin.replica.wall.commands.impl;

import java.util.HashMap;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.Media;
import com.linkedin.replica.wall.models.Post;
import com.linkedin.replica.wall.commands.Command;

public class EditPostCommand extends Command{

    public EditPostCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    @Override
    public Object execute() {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"postId", "authorId", "type", "text", "headLine", "isArticle"});

        // call dbHandler to get error or success message from dbHandler
        JsonObject request = (JsonObject) args.get("request");
        Gson gson = new Gson();
        String postId = request.get("postId").getAsString();
        String authorId = request.get("authorId").getAsString();
        String type = request.get("type").getAsString();
        String text = request.get("text").getAsString();
        String headLine = request.get("headLine").getAsString();
        int likesCount = request.get("likesCount").getAsInt();
        int commentsCount = request.get("commentsCount").getAsInt();
        boolean isArticle = request.get("isArticle").getAsBoolean();
        ArrayList<String> images = gson.fromJson(request.get("images").getAsJsonArray(), ArrayList.class);
        ArrayList<String> videos = gson.fromJson(request.get("videos").getAsJsonArray(), ArrayList.class);
        Media media = new Media(images,videos);

        Post post = dbHandler.getPost(postId);
        post.setAuthorId(authorId);
        post.setType(type);
        post.setText(text);
        post.setMedia(media);
        post.setTimestamp(post.getTimestamp());
        post.setLikesCount(likesCount);
        post.setCommentsCount(commentsCount);
        post.setHeadLine(headLine);
        post.setArticle(isArticle);

        String response = dbHandler.editPost(post);
        return response;
    }
}
