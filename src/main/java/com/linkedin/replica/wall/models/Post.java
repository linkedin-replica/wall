package com.linkedin.replica.wall.models;
import java.util.Date;

import com.arangodb.entity.DocumentField;

public class Post {


    @DocumentField(DocumentField.Type.KEY)
    private String postId;
    private String authorId;
    private String type;
    private String companyId;
    private String privacy;
    private String text;
    private String hashtags;
    private String mentions;
    private int likesCount;
    private String images;
    private String videos;
    private String urls;
    private int commentsCount;
    private String shares;
    private Date timestamp;

    private boolean isCompanyPost;
    private boolean isPrior;

    public Post(String authorId,String type, String companyId, String privacy, String text, String hashtags, String mentions, int likesCount, String images, String videos, String urls, int commentsCount, String shares, Date timestamp, boolean isCompanyPost, boolean isPrior){

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

    public String getHashtags(){
        return hashtags;
    }

    public String getMentions() {
        return mentions;
    }

    public int getLikesCount() {
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

    public Date getTimeStamp() {
        return this.timestamp;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public String getShares(){
        return shares;
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

    public void setHashtags(String hashtags) {
        this.hashtags = hashtags;
    }

    public void setMentions(String mentions) {
        this.mentions = mentions;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public void setVideos(String videos) {
        this.videos = videos;
    }

    public void setUrls(String urls) {
        this.urls = urls;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public void setShares(String shares) {
        this.shares = shares;
    }

    public void setCompanyPost(boolean companyPost) {
        isCompanyPost = companyPost;
    }

    public void setPrior(boolean prior) {
        isPrior = prior;
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
                ", shares ='" + shares + '\'' +
                ", timestamp = '" + timestamp + '\'' +
                ", isCompanyPost = '" + isCompanyPost + '\'' +
                ", isPrior = '" + isPrior + '\'' +
                "}";
    }
}
