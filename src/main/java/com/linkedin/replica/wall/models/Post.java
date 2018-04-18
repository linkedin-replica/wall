package com.linkedin.replica.wall.models;
import java.util.ArrayList;
import java.util.Date;

import com.arangodb.ArangoDB;
import com.arangodb.entity.DocumentField;

public class Post implements Comparable<Post>{


    @DocumentField(DocumentField.Type.KEY)
    private String postId;
    private String authorId;
    private String type;
    private String text;
    private String headLine;
    private int likesCount;
    private ArrayList<String> images;
    private ArrayList<String> videos;
    private int commentsCount;
    private boolean isArticle;
    private long timestamp;

    public Post(){
    }

    public String getHeadLine() {
        return headLine;
    }

    public void setHeadLine(String headLine) {
        this.headLine = headLine;
    }


    public boolean isArticle() {
        return isArticle;
    }

    public void setArticle(boolean article) {
        isArticle = article;
    }

    public int compareTo(Post post) {
        return Long.compare(this.getTimestamp(), post.getTimestamp());
    }


    public String getPostId(){
        return postId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getType(){
        return type;
    }

    public String getText() {
        return text;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public ArrayList<String> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<String> videos) {
        this.videos = videos;
    }

    public long getTimestamp() { return timestamp; }

    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "Post{" +
                "postId='" + postId + '\'' +
                ", authorId='" + authorId + '\'' +
                ", type='" + type + '\'' +
                ", text='" + text + '\'' +
                ", headLine='" + headLine + '\'' +
                ", likesCount=" + likesCount +
                ", images=" + images +
                ", videos=" + videos +
                ", commentsCount=" + commentsCount +
                ", isArticle=" + isArticle +
                ", timestamp=" + timestamp +
                '}';
    }

}
