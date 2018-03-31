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
        validateArgs(new String[]{"postId", "authorId", "type", "companyId", "privacy", "text", "hashtags", "mentions", "likesCount", "images", "videos", "urls", "commentsCount", "shares", "isCompanyPost", "isPrior"});

        // call dbHandler to get error or success message from dbHandler
        DateFormat format = new SimpleDateFormat("EEE MMM dd yyyy hh:mm a", Locale.ENGLISH);
        Post post;
        Gson googleJson = new Gson();
        String postId = args.get("postId").toString();
        String authorId = args.get("authorId").toString();
        String type = args.get("type").toString();
        String companyId = args.get("companyId").toString();
        String privacy = args.get("privacy").toString();
        String text = args.get("text").toString();
        ArrayList<String> hashtags = googleJson.fromJson((JsonArray) args.get("hashtags"), ArrayList.class);
        ArrayList<String> mentions = googleJson.fromJson((JsonArray) args.get("mentions"), ArrayList.class);
        int likesCount = (int) args.get("likesCount");
        ArrayList<String> images = googleJson.fromJson((JsonArray) args.get("images"), ArrayList.class);
        ArrayList<String> videos = googleJson.fromJson((JsonArray) args.get("videos"), ArrayList.class);
        ArrayList<String> urls = googleJson.fromJson((JsonArray) args.get("urls"), ArrayList.class);
        int commentsCount = (int) args.get("commentsCount");
        String shares = args.get("shares").toString();
        boolean isCompanyPost = (boolean) args.get("isCompanyPost");
        boolean isPrior = (boolean) args.get("isPrior");

        post = dbHandler.getPost(postId);
        post.setAuthorId(authorId);
        post.setType(type);
        post.setCompanyId(companyId);
        post.setPrivacy(privacy);
        post.setText(text);
        post.setTimestamp(post.getTimeStamp());
        post.setHashtags(hashtags);
        post.setMentions(mentions);
        post.setLikesCount(likesCount);
        post.setImages(images);
        post.setVideos(videos);
        post.setUrls(urls);
        post.setCommentsCount(commentsCount);
        post.setCompanyPost(isCompanyPost);
        post.setPrior(isPrior);

        String response = dbHandler.editPost(post);
        return response;
    }
}
