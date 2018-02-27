package com.linkedin.replica.wall.models;

public class Post {

    private String postId;
    private String authorId;
    private String type;
    private String companyId;
    private String privacy;
    private String text;
    private String hashtags;
    private String mentions;
    private String likesCount;
    private String images;
    private String videos;
    private String urls;
    private String commentsCount;
    private String shares;
    private String timestamp;

    private boolean isCompanyPost;
    private boolean isPrior;

    public Post(String postId, String authorId,String type, String companyId, String privacy, String text, String hashtags, String mentions, String likesCount, String images, String videos, String urls, String commentsCount, String shares, String timestamp, boolean isCompanyPost, boolean isPrior){

        this.postId = postId;
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
        this.shares = shares;
        this.timestamp = timestamp;
        this.isCompanyPost = isCompanyPost;
        this.isPrior = isPrior;

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

    public String getHashtags(){
        return hashtags;
    }

    public String getMentions() {
        return mentions;
    }

    public String getLikesCount() {
        return likesCount;
    }

    public String getImages() {
        return images;
    }

    public String getVideos() {
        return videos;
    }

    public String getUrls() {
        return urls;
    }

    public String getCommentsCount() {
        return commentsCount;
    }

    public String getShares() {
        return shares;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean isCompanyPost() {
        return isCompanyPost;
    }

    public boolean isPrior() {
        return isPrior;
    }
}
