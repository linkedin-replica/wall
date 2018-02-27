package com.linkedin.replica.wall.commands.impl;

import java.io.IOException;
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

    public LinkedHashMap<String, Object> execute() {

        // create a LinkedHashMap to hold results
        LinkedHashMap<String,Object> response = new LinkedHashMap<String, Object>();
        Post post;

        String postId = request.get("postId");
        String authorId = request.get("authorId");
        String type = request.get("type");
        String companyId = request.get("companyId");
        String privacy = request.get("privacy");
        String text = request.get("text");
        String hashtags = request.get("hashtags");
        String mentions = request.get("mentions");
        String likesCount = request.get("likesCount");
        String images = request.get("images");
        String videos = request.get("videos");
        String urls = request.get("urls");
        String commentsCount = request.get("commentsCount");
        String shares = request.get("shares");
        String timestamp = request.get("timestamp");

        boolean isCompanyPost = Boolean.parseBoolean(request.get("isCompanyPost"));
        boolean isPrior = Boolean.parseBoolean(request.get("isPrior"));

        post = new Post(postId, authorId, type, companyId, privacy, text, hashtags, mentions, likesCount, images, videos, urls, commentsCount, shares, timestamp, isCompanyPost, isPrior);

        response.put("response", dbHandler.deletePost(post));
        return response;
    }
}
