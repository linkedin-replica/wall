package com.linkedin.replica.wall.commands.impl;

import com.linkedin.replica.wall.commands.Command;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class GetCommentLikesCommand extends Command{

    public GetCommentLikesCommand(HashMap<String, String> args) {
        super(args);
    }


    @Override
    public LinkedHashMap<String, Object> execute() {
        // create a LinkedHashMap to hold results
        LinkedHashMap<String,Object> response = new LinkedHashMap<String, Object>();

        // call dbHandler to get results from db and add returned results to linkedHashMap
        response.put("response", dbHandler.getCommentLikes(request.get("likedCommentId")));
        return response;
    }
}
