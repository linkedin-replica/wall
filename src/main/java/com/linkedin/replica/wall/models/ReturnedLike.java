package com.linkedin.replica.wall.models;

public class ReturnedLike {
    private String likeId;
    private String likerId;
    private String likedPostId;
    private String likedCommentId;
    private String likedReplyId;
    private String likerFirstName;
    private String likerLastName;
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
        return likerFirstName;
    }

    public void setAuthorFirstName(String authorFirstName) {
        this.likerFirstName = authorFirstName;
    }

    public String getAuthorLastName() {
        return likerLastName;
    }

    public void setAuthorLastName(String authorLastName) {
        this.likerLastName = authorLastName;
    }

    public String getLikerProfilePictureUrl() {
        return likerProfilePictureUrl;
    }

    public void setLikerProfilePictureUrl(String likerProfilePictureUrl) {
        this.likerProfilePictureUrl = likerProfilePictureUrl;
    }
}
