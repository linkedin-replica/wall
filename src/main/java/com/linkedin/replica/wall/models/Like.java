package com.linkedin.replica.wall.models;

public class Like {
    private String likeId;
    private String likerId;
    private String likedPostId;
    private String likedCommentId;
    private String likedReplyId;
    private String userName;
    private String headLine;
    private String imageUrl;

    public Like(String likeId, String likerId, String likedPostId, String likedCommentId, String likedReplyId, String userName, String headLine, String imageUrl) {
        this.likeId = likeId;
        this.likerId = likerId;
        this.likedPostId = likedPostId;
        this.likedCommentId = likedCommentId;
        this.likedReplyId = likedReplyId;
        this.userName = userName;
        this.headLine = headLine;
        this.imageUrl = imageUrl;
    }

    public String getLikeId() {
        return likeId;
    }

    public String getLikerId() {
        return likerId;
    }

    public String getLikedPostId() {
        return likedPostId;
    }

    public String getLikedCommentId() {
        return likedCommentId;
    }

    public String getLikedReplyId() {
        return likedReplyId;
    }

    public String getUserName() {
        return userName;
    }

    public String getHeadLine() {
        return headLine;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
