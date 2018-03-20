package com.linkedin.replica.wall.models;

import java.util.ArrayList;
import java.util.Date;

public class Post {

    private String postID,authorID,type,companyID,privacy,text;
    private Date timeStamp;
    private boolean isCompanyPost,isPrior;
    private ArrayList<String> hashtags,mentions,images,videos,urls,shares;
    private int likesCount,commentsCount;

    public Post(String postID, String authorID, String type, String companyID, String privacy, String text,
                Date timeStamp, boolean isCompanyPost, boolean isPrior, ArrayList<String> hashtags,
                ArrayList<String> mentions, ArrayList<String> images,ArrayList<String> videos,
                ArrayList<String> urls, ArrayList<String> shares, int likesCount, int commentsCount){

        this.postID = postID;
        this.authorID = authorID;
        this.type = type;
        this.companyID = companyID;
        this.privacy = privacy;
        this.text =text;
        this.timeStamp = timeStamp;
        this.isCompanyPost = isCompanyPost;
        this.isPrior = isPrior;
        this.hashtags = hashtags;
        this.mentions = mentions;
        this.images = images;
        this.videos = videos;
        this.urls = urls;
        this.shares = shares;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;

    }
    public Post(){
        super();
    }

    public String getPostID() {
        return this.postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getAuthorID() {
        return this.authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCompanyID() {
        return this.companyID;
    }

    public void setCompanyID(String companyID) {
        this.companyID = companyID;
    }

    public String getPrivacy() {
        return this.privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getTimeStamp() {
        return this.timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isCompanyPost() {
        return this.isCompanyPost;
    }

    public void setCompanyPost(boolean companyPost) {
        isCompanyPost = companyPost;
    }

    public boolean isPrior() {
        return this.isPrior;
    }

    public void setPrior(boolean prior) {
        isPrior = prior;
    }

    public ArrayList<String> getHashtags() {
        return this.hashtags;
    }

    public void setHashtags(ArrayList<String> hashtags) {
        this.hashtags = hashtags;
    }

    public ArrayList<String> getMentions() {
        return this.mentions;
    }

    public void setMentions(ArrayList<String> mentions) {
        this.mentions = mentions;
    }

    public ArrayList<String> getImages() {
        return this.images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public ArrayList<String> getVideos() {
        return this.videos;
    }

    public void setVideos(ArrayList<String> videos) {
        this.videos = videos;
    }

    public ArrayList<String> getUrls() {
        return this.urls;
    }

    public void setUrls(ArrayList<String> urls) {
        this.urls = urls;
    }

    public ArrayList<String> getShares() {
        return this.shares;
    }

    public void setShares(ArrayList<String> shares) {
        this.shares = shares;
    }

    public int getLikesCount() {
        return this.likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getCommentsCount() {
        return this.commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }
}





