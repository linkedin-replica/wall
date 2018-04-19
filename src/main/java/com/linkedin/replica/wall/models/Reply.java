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
    private int likesCount;
    private String text;
    private Long timestamp;
    private ArrayList<String> likers;

    public Reply(){
        super();
    }

    public ArrayList<String> getLikers() {
        return likers;
    }

    public void setLikers(ArrayList<String> likers) {
        this.likers = likers;
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

    public void setText(String text) {
        this.text = text;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
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

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public String getText() {
        return text;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Reply{" +
                "replyId='" + replyId + '\'' +
                ", authorId='" + authorId + '\'' +
                ", parentPostId='" + parentPostId + '\'' +
                ", parentCommentId='" + parentCommentId + '\'' +
                ", likesCount=" + likesCount +
                ", text='" + text + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

}
