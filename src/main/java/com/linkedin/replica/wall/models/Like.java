package com.linkedin.replica.wall.models;

import com.arangodb.entity.DocumentField;

public class Like {

    @DocumentField(DocumentField.Type.KEY)
    private String likeId;
    private String likerId;
    private String likedPostId;
    private String likedCommentId;
    private String likedReplyId;

    public Like(){
        super();
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

    @Override
    public String toString() {
        return "Like{" +
                "likeId='" + likeId + '\'' +
                ", likerId='" + likerId + '\'' +
                ", likedPostId='" + likedPostId + '\'' +
                ", likedCommentId='" + likedCommentId + '\'' +
                ", likedReplyId='" + likedReplyId + '\'' +
                '}';
    }
}
