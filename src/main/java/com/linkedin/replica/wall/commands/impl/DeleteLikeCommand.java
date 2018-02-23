package com.linkedin.replica.wall.commands.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;

import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.models.Like;

public class DeleteLikeCommand extends Command{

    public DeleteLikeCommand() {
        super();
    }


    @Override
    public LinkedHashMap<String, Object> execute() {
        // create a LinkedHashMap to hold results
        LinkedHashMap<String,Object> response = new LinkedHashMap<String, Object>();
        Like like;
        String likeId = request.get("likeId");
        String likerId = request.get("likerId");
        String userName = request.get("username");
        String headLine = request.get("headline");
        String imageUrl = request.get("imageUrl");
        String likedPostId = request.get("likedPostId");
        String likedCommentId = request.get("likedCommentId");
        String likedReplyId = request.get("likedReplyId");
        like = new Like(likeId, likerId, likedPostId, likedCommentId, likedReplyId, userName, headLine,imageUrl);

        // call dbHandler to get results from db and add returned results to linkedHashMap
        response.put("response", dbHandler.deleteLike(like));
        return response;
    }
}
