package com.linkedin.replica.wall.commands.impl;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.*;
import com.linkedin.replica.wall.models.Post;
import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.handlers.impl.ArangoWallHandler;

public class AddPostCommand extends Command{

    Post comment;
    String [] images;
    String [] mentions;
    String [] urls;
    public AddPostCommand() {

        super();
    }

    public LinkedHashMap<String, Object> execute() throws ParseException {

        // create a LinkedHashMap to hold results
        LinkedHashMap<String,Object> response = new LinkedHashMap<String, Object>();
        DateFormat format = new SimpleDateFormat("EEE MMM dd yyyy hh:mm a", Locale.ENGLISH);
        Post post;
        String postID = request.get("postID");
        String authorID = request.get("authorID");
        String type = request.get("type");
        String companyID = request.get("companyID");
        String privacy = request.get("privacy");
        String text = request.get("text");
        Date timeStamp = format.parse(request.get("timeStamp"));
        //String timeStamp = request.get("timeStamp");
        boolean isCompanyPost = Boolean.parseBoolean(request.get("isCompanyPost"));
        boolean isPrior = Boolean.parseBoolean(request.get("isPrior"));
        ArrayList<String> hashtags = new ArrayList<String>(Arrays.asList(request.get("hashtags").split(",")));
        ArrayList<String> mentions = new ArrayList<String>(Arrays.asList(request.get("mentions").split(",")));
        ArrayList<String> images = new ArrayList<String>(Arrays.asList(request.get("images").split(",")));
        ArrayList<String> videos = new ArrayList<String>(Arrays.asList(request.get("videos").split(",")));
        ArrayList<String> urls = new ArrayList<String>(Arrays.asList(request.get("urls").split(",")));
        ArrayList<String> shares = new ArrayList<String>(Arrays.asList(request.get("shares").split(",")));
        Integer likesCount = Integer.parseInt(request.get("likesCount"));
        Integer commentsCount = Integer.parseInt(request.get("commentsCount"));



        post = new Post(postID, authorID, type, companyID, privacy, text, timeStamp,isCompanyPost,isPrior,
                hashtags, mentions,images,videos,urls,shares,likesCount,commentsCount);

        // call dbHandler to get results from db and add returned results to linkedHashMap
        response.put("response", dbHandler.addPost(post));
        return response;
    }
}
