package com.linkedin.replica.wall.commands.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.*;

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
        String postId = args.get("postId").toString();
        String authorId = args.get("authorId").toString();
        String type = args.get("type").toString();
        String companyId = args.get("companyId").toString();
        String privacy = args.get("privacy").toString();
        String text = args.get("text").toString();
        Date timestamp = format.parse(args.get("timeStamp").toString());
        String hashtags = args.get("hashtags").toString();
        String mentions = args.get("mentions").toString();
        int likesCount = Integer.parseInt(args.get("likesCount").toString());
        String images = args.get("images").toString();
        String videos = args.get("videos").toString();
        String urls = args.get("urls").toString();
        int commentsCount = Integer.parseInt(args.get("commentsCount").toString());
        String shares = args.get("shares").toString();
        boolean isCompanyPost = Boolean.parseBoolean(args.get("isCompanyPost").toString());
        boolean isPrior = Boolean.parseBoolean(args.get("isPrior").toString());
        post = new Post(postId, authorId, type, companyId, privacy, text, hashtags, mentions, likesCount, images, videos, urls, commentsCount, shares, timestamp, isCompanyPost, isPrior);

        String response = dbHandler.editPost(post);
        return response;
    }
}
