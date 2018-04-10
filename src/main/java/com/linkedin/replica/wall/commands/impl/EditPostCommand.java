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

public class EditPostCommand extends Command{

    public EditPostCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    @Override
    public Object execute() throws ParseException {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;

        // validate that all required arguments that are passed
        validateArgs(new String[]{"postId", "authorId", "type", "text", "likesCount", "commentsCount", "headLine", "isArticle"});

        // call dbHandler to get error or success message from dbHandler
        Post post;
        Gson googleJson = new Gson();
        String postId = args.get("postId").toString();
        String authorId = args.get("authorId").toString();
        String type = args.get("type").toString();
        String text = args.get("text").toString();
        String headLine = args.get("headLine").toString();
        int likesCount = (int) args.get("likesCount");
        int commentsCount = (int) args.get("commentsCount");
        boolean isArticle = (boolean) args.get("isArticle");
        ArrayList<String> images = googleJson.fromJson((JsonArray) args.get("images"), ArrayList.class);
        ArrayList<String> videos = googleJson.fromJson((JsonArray) args.get("videos"), ArrayList.class);
        Media media = new Media(images,videos);

        post = dbHandler.getPost(postId);
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
