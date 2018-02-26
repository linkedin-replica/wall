package com.linkedin.replica.wall.commands.impl;

import java.util.LinkedHashMap;

import com.linkedin.replica.wall.commands.Command;

public class GetPostLikesCommand extends Command{

    public GetPostLikesCommand() {
        super();
    }


    @Override
    public LinkedHashMap<String, Object> execute() {
        // create a LinkedHashMap to hold results
        LinkedHashMap<String,Object> response = new LinkedHashMap<String, Object>();

        // call dbHandler to get results from db and add returned results to linkedHashMap
        response.put("response", dbHandler.getPostLikes(request.get("likedPostId")));
        return response;
    }
}
