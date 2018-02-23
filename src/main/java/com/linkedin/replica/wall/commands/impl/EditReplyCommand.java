package com.linkedin.replica.wall.commands.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.models.Reply;

public class EditReplyCommand extends Command{

    public EditReplyCommand() {
        super();
    }

    public LinkedHashMap<String, Object> execute() throws ParseException {
        // create a LinkedHashMap to hold results
        LinkedHashMap<String,Object> response = new LinkedHashMap<String, Object>();
        // call dbHandler to get results from db and add returned results to linkedHashMapM
        Reply reply;
        DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
        String replyId = request.get("replyId");
        String authorId = request.get("authorId");
        String parentPostId = request.get("parentPostId");
        String parentCommentId = request.get("parentCommentId");
        ArrayList<String> mentions = new ArrayList<String>(Arrays.asList(request.get("mentions").split(",")));
        Long likesCount = Long.parseLong(request.get("likesCount"));
        String text = (String) request.get("text");
        Date timestamp = format.parse(request.get("timestamp"));
        ArrayList<String> images = new ArrayList<String>(Arrays.asList(request.get("images").split(",")));
        ArrayList<String> urls = new ArrayList<String>(Arrays.asList(request.get("urls").split(",")));


        reply = new Reply(replyId, authorId, parentPostId, parentCommentId, mentions, likesCount, text, timestamp, images, urls);
        response.put("response", dbHandler.editReply(reply));
        return response;

    }
}
