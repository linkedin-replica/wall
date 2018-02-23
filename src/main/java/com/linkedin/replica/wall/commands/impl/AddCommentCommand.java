package com.linkedin.replica.wall.commands.impl;

import java.util.HashMap;
import com.linkedin.replica.wall.models.Comment;
import com.linkedin.replica.wall.commands.Command;
import com.linkedin.replica.wall.handlers.impl.ArangoWallHandler;

public class AddCommentCommand extends Command{

    Comment comment;
    String [] images;
    String [] mentions;
    String [] urls;
    public AddCommentCommand(HashMap<String, String> args) {

        super(args);


        comment.setCommentId(args.get("commentID"));
        comment.setAuthorId(args.get("authorID"));
        comment.setParentPostId(args.get("parentPostID"));
        comment.setLikesCount(Integer.parseInt(args.get("likesCount")));
        comment.setRepliesCount(Integer.parseInt(args.get("repliesCount")));
        images = args.get("images").split(",");
        urls = args.get("urls").split(",");
        mentions = args.get("mentions").split(",");
        comment.setImages(images);
        comment.setUrls(urls);
        comment.setMentions(mentions);
        comment.setText(args.get("text"));
        comment.setTimeStamp(args.get("timeStamp"));

    }

    public String execute() {

        return null;
    }
}
