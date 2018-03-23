package com.linkedin.replica.wall.models;

import com.arangodb.entity.DocumentField;

public class Like {

    @DocumentField(DocumentField.Type.KEY)
    private String likeId;
    private String likerId;
    private String likedPostId;
    private String likedCommentId;
    private String likedReplyId;
    private String userName;
    private String headLine;
    private String imageUrl;

    public Like(){
        super();
    }

    public Like(String likerId, String likedPostId, String likedCommentId, String likedReplyId, String userName, String headLine, String imageUrl) {
        this.likerId = likerId;
        this.likedPostId = likedPostId;
        this.likedCommentId = likedCommentId;
        this.likedReplyId = likedReplyId;
        this.userName = userName;
        this.headLine = headLine;
        this.imageUrl = imageUrl;
    }


    public void setLikeId(String likeId) {
        this.likeId = likeId;
    }

    public void setLikerId(String likerId) {
        this.likerId = likerId;
    }

    public void setLikedPostId(String likedPostId) {
        this.likedPostId = likedPostId;
    }

    public void setLikedCommentId(String likedCommentId) {
        this.likedCommentId = likedCommentId;
    }

    public void setLikedReplyId(String likedReplyId) {
        this.likedReplyId = likedReplyId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setHeadLine(String headLine) {
        this.headLine = headLine;
    }

    public void setImageUrl(String imageUrl) {
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

    @Override
    public String toString() {
        return "Like{" +
                "likeId='" + likeId + '\'' +
                ", likerId='" + likerId + '\'' +
                ", likedPostId='" + likedPostId + '\'' +
                ", likedCommentId='" + likedCommentId + '\'' +
                ", likedReplyId='" + likedReplyId + '\'' +
                ", userName='" + userName + '\'' +
                ", headLine='" + headLine + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }


}
