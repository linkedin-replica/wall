package com.linkedin.replica.wall.commands.impl;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.*;
import com.linkedin.replica.wall.models.Post;
import com.linkedin.replica.wall.commands.Command;

public class DeletePostCommand extends Command{

    Post comment;
    String [] images;
    String [] mentions;
    String [] urls;
    public DeletePostCommand() {

        super();
    }

    public LinkedHashMap<String, Object> execute() throws ParseException {

        // create a LinkedHashMap to hold results
        LinkedHashMap<String,Object> response = new LinkedHashMap<String, Object>();
        Post post;
        DateFormat format = new SimpleDateFormat("EEE MMM dd yyyy hh:mm a", Locale.ENGLISH);
        String postId = request.get("postId");
        String authorId = request.get("authorId");
        String type = request.get("type");
        String companyId = request.get("companyId");
        String privacy = request.get("privacy");
        String text = request.get("text");
        String hashtags = request.get("hashtags");
        String mentions = request.get("mentions");
        int likesCount = Integer.parseInt(request.get("likesCount"));
        String images = request.get("images");
        String videos = request.get("videos");
        String urls = request.get("urls");
        int commentsCount = Integer.parseInt(request.get("commentsCount"));
        String shares = request.get("shares");
        Date timestamp = format.parse(request.get("timeStamp"));

        boolean isCompanyPost = Boolean.parseBoolean(request.get("isCompanyPost"));
        boolean isPrior = Boolean.parseBoolean(request.get("isPrior"));

        post = new Post(postId, authorId, type, companyId, privacy, text, hashtags, mentions, likesCount, images, videos, urls, commentsCount, shares, timestamp, isCompanyPost, isPrior);

        response.put("response", dbHandler.deletePost(post));
        return response;
    }
}
