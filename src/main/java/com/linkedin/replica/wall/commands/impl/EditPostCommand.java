package com.linkedin.replica.wall.commands.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.Post;
import com.linkedin.replica.wall.commands.Command;

public class EditPostCommand extends Command{

    public EditPostCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    @Override
    public Object execute() throws ParseException {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"postId", "authorId"});

        // call dbHandler to get error or success message from dbHandler
        Post post;
        Gson googleJson = new Gson();

        String postId = args.get("postId").toString();
        String authorId = args.get("authorId").toString();
        post = dbHandler.getPost(postId);
        post.setAuthorId(authorId);
        post.setTimestamp(post.getTimestamp());

        if(args.containsKey("type")){
            String type = args.get("type").toString();
            post.setType(type);
        }
        if(args.containsKey("text")){
            String text = args.get("text").toString();
            post.setText(text);
        }
        if(args.containsKey("headLine")){
            String headLine = args.get("headLine").toString();
            post.setHeadLine(headLine);
        }
        if(args.containsKey("likesCount")){
            int likesCount = (int) args.get("likesCount");
            post.setLikesCount(likesCount);
        }
        if(args.containsKey("images")){
            ArrayList<String> images = googleJson.fromJson((JsonArray) args.get("images"), ArrayList.class);
            post.setImages(images);
        }
        if(args.containsKey("videos")){
            ArrayList<String> videos = googleJson.fromJson((JsonArray) args.get("videos"), ArrayList.class);
            post.setVideos(videos);
        }
        if(args.containsKey("commentsCount")){
            int commentsCount = (int) args.get("commentsCount");
            post.setCommentsCount(commentsCount);
        }
        if(args.containsKey("isArticle")){
            boolean isArticle = (boolean) args.get("isArticle");
            post.setArticle(isArticle);
        }
        String response = dbHandler.editPost(post);
        return response;
    }
}
