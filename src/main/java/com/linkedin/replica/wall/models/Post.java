package com.linkedin.replica.wall.models;
import java.util.ArrayList;
import java.util.Date;

import com.arangodb.entity.DocumentField;

public class Post implements Comparable<Post>{


    @DocumentField(DocumentField.Type.KEY)
    private String postId;
    private String authorId;
    private String type;
    private String companyId;
    private String privacy;
    private String text;
    private ArrayList<String> hashtags;
    private ArrayList<String> mentions;
    private int likesCount;
    private ArrayList<String> images;
    private ArrayList<String> videos;
    private ArrayList<String> urls;
    private int commentsCount;
    private Date timestamp;

    private boolean isCompanyPost;
    private boolean isPrior;


    public Post(String authorId,String type, String companyId, String privacy, String text, ArrayList<String> hashtags, ArrayList<String> mentions, int likesCount, ArrayList<String> images, ArrayList<String> videos, ArrayList<String> urls, int commentsCount, Date timestamp, boolean isCompanyPost, boolean isPrior){
        this.authorId = authorId;
        this.type = type;
        this.companyId = companyId;
        this.privacy = privacy;
        this.text = text;
        this.hashtags = hashtags;
        this.mentions = mentions;
        this.likesCount = likesCount;
        this.images = images;
        this.videos = videos;
        this.urls = urls;
        this.commentsCount = commentsCount;
        this.timestamp = timestamp;
        this.isCompanyPost = isCompanyPost;
        this.isPrior = isPrior;

    }

    public int compareTo(Post post) {
        return this.getTimeStamp().compareTo(post.getTimeStamp());
    }


    public Post(){
        super();
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

    public String getCompanyId() {
        return companyId;
    }

    public String getPrivacy() {
        return privacy;
    }

    public String getText() {
        return text;
    }

    public ArrayList<String> getHashtags(){
        return hashtags;
    }

    public ArrayList<String> getMentions() {
        return mentions;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public ArrayList<String> getVideos() {
        return videos;
    }

    public ArrayList<String> getUrls() {
        return urls;
    }

    public Date getTimeStamp() {
        return this.timestamp;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public boolean isCompanyPost() {
        return isCompanyPost;
    }

    public boolean isPrior() {
        return isPrior;
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

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setHashtags(ArrayList<String> hashtags) {
        this.hashtags = hashtags;
    }

    public void setMentions(ArrayList<String> mentions) {
        this.mentions = mentions;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public void setVideos(ArrayList<String> videos) {
        this.videos = videos;
    }

    public void setUrls(ArrayList<String> urls) {
        this.urls = urls;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public void setCompanyPost(boolean companyPost) {
        isCompanyPost = companyPost;
    }

    public void setPrior(boolean prior) {
        isPrior = prior;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Post{" +
                "PostId = '" + postId + '\'' +
                ", authorId = '" + authorId + '\''+
                ", type = '" + type + '\''+
                ", comapnyId = '" + companyId + '\'' +
                ", privacy = '" + privacy + '\'' +
                ", text = '" + text + '\'' +
                ", hashtags = '" + hashtags + '\''+
                ", mentions ='" + mentions + '\'' +
                ", likesCount = '" + likesCount + '\'' +
                ", images = '" + images + '\'' +
                ", videos = '" + videos + '\'' +
                ", urls = '" + urls + '\'' +
                ", commentsCount = '" + commentsCount + '\'' +
                ", timestamp = '" + timestamp + '\'' +
                ", isCompanyPost = '" + isCompanyPost + '\'' +
                ", isPrior = '" + isPrior + '\'' +
                "}";
    }
}
