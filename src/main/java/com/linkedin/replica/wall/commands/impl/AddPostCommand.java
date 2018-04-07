package com.linkedin.replica.wall.commands.impl;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.linkedin.replica.wall.cache.handlers.PostsCacheHandler;
import com.linkedin.replica.wall.database.handlers.DatabaseHandler;
import com.linkedin.replica.wall.database.handlers.WallHandler;
import com.linkedin.replica.wall.models.Post;
import com.linkedin.replica.wall.commands.Command;

public class AddPostCommand extends Command{

    public AddPostCommand(HashMap<String, Object> args, DatabaseHandler dbHandler){
        super(args,dbHandler);
    }


    @Override
    public Object execute() throws ParseException, IllegalAccessException, IOException, InstantiationException {

        // get database handler that implements functionality of this command
        WallHandler dbHandler = (WallHandler) this.dbHandler;
        PostsCacheHandler cacheHandler = (PostsCacheHandler) this.cacheHandler;

        // validate that all required arguments that are passed

        validateArgs(new String[]{"authorId", "type", "companyId", "privacy", "text", "hashtags", "mentions", "likesCount", "images", "videos", "urls", "commentsCount", "shares", "isCompanyPost", "isPrior", "timestamp", "shares", "headLine", "isArticle"});

        // call dbHandler to get error or success message from dbHandler
        DateFormat format = new SimpleDateFormat("EEE MMM dd yyyy hh:mm a", Locale.ENGLISH);
        Post post;
        Gson googleJson = new Gson();
        String authorId = args.get("authorId").toString();
        String type = args.get("type").toString();
        String companyId = args.get("companyId").toString();
        String privacy = args.get("privacy").toString();
        String text = args.get("text").toString();
        String headLine = args.get("headLine").toString();
        Date timestamp = format.parse(args.get("timestamp").toString());
        ArrayList<String> hashtags = googleJson.fromJson((JsonArray) args.get("hashtags"), ArrayList.class);
        ArrayList<String> mentions = googleJson.fromJson((JsonArray) args.get("mentions"), ArrayList.class);
        int likesCount = (int) args.get("likesCount");
        ArrayList<String> images = googleJson.fromJson((JsonArray) args.get("images"), ArrayList.class);
        ArrayList<String> videos = googleJson.fromJson((JsonArray) args.get("videos"), ArrayList.class);
        ArrayList<String> urls = googleJson.fromJson((JsonArray) args.get("urls"), ArrayList.class);
        ArrayList<String> shares = googleJson.fromJson((JsonArray) args.get("shares"), ArrayList.class);
        int commentsCount = (int) args.get("commentsCount");
        boolean isCompanyPost = (boolean) args.get("isCompanyPost");
        boolean isPrior = (boolean) args.get("isPrior");
        boolean isArticle = (boolean) args.get("isArticle");

        post = new Post(authorId, type, companyId, privacy, text, hashtags, mentions, likesCount, images, videos, urls, commentsCount, timestamp, isCompanyPost, isPrior, shares, headLine,isArticle);

        String response = dbHandler.addPost(post);
        cacheHandler.cachePost(post.getPostId(),post);
        return response;
    }
}
