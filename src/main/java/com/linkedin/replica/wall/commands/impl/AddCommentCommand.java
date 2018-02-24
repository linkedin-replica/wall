package com.linkedin.replica.wall.commands.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.*;
import com.linkedin.replica.wall.models.Comment;
import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.handlers.impl.ArangoWallHandler;

public class AddCommentCommand extends Command{

    Comment comment;
    String [] images;
    String [] mentions;
    String [] urls;
    public AddCommentCommand(HashMap<String, String> args) {

        super();
    }

    public LinkedHashMap<String, Object> execute() throws IOException, ClassNotFoundException {

        // create a LinkedHashMap to hold results
        LinkedHashMap<String,Object> response = new LinkedHashMap<String, Object>();
        Comment comment;
        String commentID = request.get("commentID");
        String authorID = request.get("authorID");
        String parentPostID = request.get("parentPostID");
        Integer likesCount = Integer.parseInt(request.get("likesCount"));
        Integer repliesCount = Integer.parseInt(request.get("repliesCount"));
        ArrayList<String> images = new ArrayList<String>(Arrays.asList(request.get("images").split(",")));
        ArrayList<String> urls = new ArrayList<String>(Arrays.asList(request.get("urls").split(",")));
        ArrayList<String> mentions = new ArrayList<String>(Arrays.asList(request.get("mentions").split(",")));
        String text = request.get("text");
        String timeStamp = request.get("timeStamp");
        comment = new Comment(commentID, authorID, parentPostID, likesCount, repliesCount, images, urls,mentions,text,timeStamp);

        // call dbHandler to get results from db and add returned results to linkedHashMap
        response.put("response", dbHandler.addComment(comment));
        return response;
    }
}
