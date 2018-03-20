package com.linkedin.replica.wall.models;

import com.arangodb.entity.DocumentField;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Reply {
    @DocumentField(DocumentField.Type.KEY)
    private String replyId;
    private String authorId;
    private String parentPostId;
    private String parentCommentId;
    private ArrayList<String> mentions;
    private Long likesCount;
    private String text;
    private Date timestamp;
    private ArrayList<String> images;
    private ArrayList <String> urls;

    public Reply(String replyId, String authorId, String parentPostId, String parentCommentId, ArrayList<String> mentions, Long likesCount, String text, Date timestamp, ArrayList<String> images,  ArrayList<String> urls ) {
        this.replyId = replyId;
        this.authorId = authorId;
        this.parentPostId = parentPostId;
        this.parentCommentId = parentCommentId;
        this.mentions = mentions;
        this.likesCount = likesCount;
        this.text = text;
        this.timestamp = timestamp;
        this.images = images;
        this.urls = urls;
    }
    public Reply(){
        super();
    }

    public String getReplyId() {
        return replyId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getParentPostId() {
        return parentPostId;
    }

    public String getParentCommentId() {
        return parentCommentId;
    }

    public ArrayList<String> getMentions() {
        return mentions;
    }

    public Long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Long likesCount) {
        this.likesCount = likesCount;
    }

    public String getText() {
        return text;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public ArrayList<String> getUrls() {
        return urls;
    }
}
