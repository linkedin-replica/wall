package com.linkedin.replica.wall.models;

public class ReturnedLike {
    private String likeId;
    private String likerId;
    private String likedPostId;
    private String likedCommentId;
    private String likedReplyId;
    private String likerName;
    private String likerProfilePictureUrl;

    public ReturnedLike(){

    }

    public String getLikeId() {
        return likeId;
    }

    public void setLikeId(String likeId) {
        this.likeId = likeId;
    }

    public String getLikerId() {
        return likerId;
    }

    public void setLikerId(String likerId) {
        this.likerId = likerId;
    }

    public String getLikedPostId() {
        return likedPostId;
    }

    public void setLikedPostId(String likedPostId) {
        this.likedPostId = likedPostId;
    }

    public String getLikedCommentId() {
        return likedCommentId;
    }

    public void setLikedCommentId(String likedCommentId) {
        this.likedCommentId = likedCommentId;
    }

    public String getLikedReplyId() {
        return likedReplyId;
    }

    public void setLikedReplyId(String likedReplyId) {
        this.likedReplyId = likedReplyId;
    }

    public String getAuthorFirstName() {
        return likerName;
    }

    public void setAuthorFirstName(String authorFirstName) {
        this.likerName = authorFirstName;
    }

    public String getLikerProfilePictureUrl() {
        return likerProfilePictureUrl;
    }

    public void setLikerProfilePictureUrl(String likerProfilePictureUrl) {
        this.likerProfilePictureUrl = likerProfilePictureUrl;
    }
}
