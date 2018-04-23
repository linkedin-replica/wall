package com.linkedin.replica.wall.models;
import java.util.ArrayList;
import java.util.Date;

import com.arangodb.ArangoDB;
import com.arangodb.entity.DocumentField;

public class Post implements Comparable<Post>{


    @DocumentField(DocumentField.Type.KEY)
    private String postId;
    private String authorId;
    private String text;
    private ArrayList<String> images;
    private ArrayList<String> videos;
    private ArrayList<String> likers;
    private int commentsCount;
    private boolean isArticle;
    private long timestamp;
    private boolean isCompanyPost;
    private String title;

    public Post(){
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompanyPost() {
        return isCompanyPost;
    }

    public void setCompanyPost(boolean companyPost) {
        isCompanyPost = companyPost;
    }

    public ArrayList<String> getLikers() {
        return likers;
    }

    public void setLikers(ArrayList<String> likers) {
        this.likers = likers;
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

    public String getText() {
        return text;
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

    public void setText(String text) {
        this.text = text;
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
                ", text='" + text + '\'' +
                ", images=" + images +
                ", videos=" + videos +
                ", likers=" + likers +
                ", commentsCount=" + commentsCount +
                ", isArticle=" + isArticle +
                ", timestamp=" + timestamp +
                ", isCompanyPost=" + isCompanyPost +
                ", title='" + title + '\'' +
                '}';
    }
}
