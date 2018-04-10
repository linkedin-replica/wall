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
import com.linkedin.replica.wall.models.Media;
import com.linkedin.replica.wall.models.Post;
import com.linkedin.replica.wall.commands.Command;

public class AddPostCommand extends Command{

    public AddPostCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    @Override
    public Object execute() throws ParseException {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"authorId", "type", "text", "likesCount", "images", "videos", "commentsCount", "headLine", "isArticle"});

        // call dbHandler to get error or success message from dbHandler
        DateFormat format = new SimpleDateFormat("EEE MMM dd yyyy hh:mm a", Locale.ENGLISH);
        Post post;
        Gson googleJson = new Gson();
        String authorId = args.get("authorId").toString();
        String type = args.get("type").toString();
        String text = args.get("text").toString();
        String headLine = args.get("headLine").toString();
        Long timestamp = System.currentTimeMillis();
        int likesCount = (int) args.get("likesCount");
        ArrayList<String> images = googleJson.fromJson((JsonArray) args.get("images"), ArrayList.class);
        ArrayList<String> videos = googleJson.fromJson((JsonArray) args.get("videos"), ArrayList.class);
        Media media = new Media(images,videos);
        int commentsCount = (int) args.get("commentsCount");
        boolean isArticle = (boolean) args.get("isArticle");

        post = new Post();
        post.setArticle(isArticle);
        post.setHeadLine(headLine);
        post.setAuthorId(authorId);
        post.setCommentsCount(commentsCount);
        post.setLikesCount(likesCount);
        post.setMedia(media);
        post.setType(type);
        post.setText(text);
        post.setTimestamp(timestamp);

        String response = dbHandler.addPost(post);
        return response;
    }
}
