package com.linkedin.replica.wall.models;

import java.util.ArrayList;
import java.util.HashMap;

public class ReturnedReply {
    private String replyId;
    private String authorId;
    private String parentPostId;
    private String parentCommentId;
    private int likesCount;
    private String text;
    private Long timestamp;
    private String authorName;
    private String authorProfilePictureUrl;
    private boolean liked;
    private ArrayList<HashMap<String,String>> likers;

    public ReturnedReply(){

    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public ArrayList<HashMap<String, String>> getLikers() {
        return likers;
    }

    public void setLikers(ArrayList<HashMap<String, String>> likers) {
        this.likers = likers;
    }

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getParentPostId() {
        return parentPostId;
    }

    public void setParentPostId(String parentPostId) {
        this.parentPostId = parentPostId;
    }

    public String getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(String parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAuthorFirstName() {
        return authorName;
    }

    public void setAuthorFirstName(String authorFirstName) {
        this.authorName = authorFirstName;
    }

    public String getAuthorProfilePictureUrl() {
        return authorProfilePictureUrl;
    }

    public void setAuthorProfilePictureUrl(String authorProfilePictureUrl) {
        this.authorProfilePictureUrl = authorProfilePictureUrl;
    }
}
