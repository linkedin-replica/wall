package com.linkedin.replica.wall.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ReturnedComment {
    private String commentId;
    private String authorId;
    private String parentPostId;
    private int repliesCount;
    private String text;
    private long timestamp;

    private String authorName;
    private String authorProfilePictureUrl;
    private boolean liked;
    private ArrayList<Liker> likers;

    public ReturnedComment(){

    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
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

    public int getRepliesCount() {
        return repliesCount;
    }

    public void setRepliesCount(int repliesCount) {
        this.repliesCount = repliesCount;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorProfilePictureUrl() {
        return authorProfilePictureUrl;
    }

    public void setAuthorProfilePictureUrl(String authorProfilePictureUrl) {
        this.authorProfilePictureUrl = authorProfilePictureUrl;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public ArrayList<Liker> getLikers() {
        return likers;
    }

    public void setLikers(Object val) {
        this.likers = new ArrayList<Liker>();
        ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String,Object>>) val;
        for(int i=0; i<list.size(); ++i){
            HashMap<String, Object> map = list.get(i);
            Iterator<String> iter = map.keySet().iterator();
            Liker liker = new Liker();
            likers.add(liker);
            while(iter.hasNext()){
                String key = iter.next();
                Object value = map.get(key);
                liker.set(key, value);
            }
        }
    }

    public void set(String attributeName, Object val){
        switch(attributeName){
            case "commentId" : setCommentId(val.toString()); break;
            case "authorId" : setAuthorId(val.toString()); break;
            case "parentPostId" : setParentPostId(val.toString()); break;
            case "text" : setText(val.toString()); break;
            case "repliesCount" : setRepliesCount(Integer.parseInt(val.toString())); break;
            case "timestamp" : setTimestamp(Long.parseLong(val.toString())); break;
            case "authorName" : setAuthorName(val.toString()); break;
            case "authorProfilePictureUrl" : setAuthorProfilePictureUrl(val.toString()); break;
            case "likers" : setLikers(val);; break;
            case "liked" : setLiked(Boolean.getBoolean(val.toString())); break;
        }
    }

    @Override
    public String toString() {
        return "ReturnedComment{" +
                "commentId='" + commentId + '\'' +
                ", authorId='" + authorId + '\'' +
                ", parentPostId='" + parentPostId + '\'' +
                ", repliesCount=" + repliesCount +
                ", text='" + text + '\'' +
                ", timestamp=" + timestamp +
                ", authorName='" + authorName + '\'' +
                ", authorProfilePictureUrl='" + authorProfilePictureUrl + '\'' +
                ", liked=" + liked +
                ", likers=" + likers +
                '}';
    }
}
