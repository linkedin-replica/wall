package com.linkedin.replica.wall.models;

import com.arangodb.entity.DocumentField;

public class Like {

    @DocumentField(DocumentField.Type.KEY)
    private String likeId;
    private String likerId;
    private String likedPostId;
    private String likedCommentId;
    private String likedReplyId;
    private String firstName;
    private String lastName;
    private String imageUrl;

    public Like(){
        super();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
