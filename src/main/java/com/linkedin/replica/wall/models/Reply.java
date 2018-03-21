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

    public Reply(String authorId, String parentPostId, String parentCommentId, ArrayList<String> mentions, Long likesCount, String text, Date timestamp, ArrayList<String> images,  ArrayList<String> urls ) {
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

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public void setParentPostId(String parentPostId) {
        this.parentPostId = parentPostId;
    }

    public void setParentCommentId(String parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public void setMentions(ArrayList<String> mentions) {
        this.mentions = mentions;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public void setUrls(ArrayList<String> urls) {
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
