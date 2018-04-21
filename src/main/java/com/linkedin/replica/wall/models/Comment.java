package com.linkedin.replica.wall.models;

import com.arangodb.entity.DocumentField;

import java.util.ArrayList;
import java.util.Date;

public class Comment {

    @DocumentField(DocumentField.Type.KEY)
        private String commentId;
        private String authorId,parentPostId;
        private int likesCount,repliesCount;
        private String text;
        private Long timestamp;
        private ArrayList<String> likers;


        public Comment(){

        }

        public String getCommentId() {
            return this.commentId;
        }

        public void setCommentId(String commentId) {
            this.commentId = commentId;
        }

        public String getAuthorId() {
            return this.authorId;
        }

        public void setAuthorId(String authorId) {
            this.authorId = authorId;
        }

        public String getParentPostId() {
            return this.parentPostId;
        }

        public void setParentPostId(String parentPostId) {
            this.parentPostId = parentPostId;
        }

        public int getLikesCount() {
            return this.likesCount;
        }

        public void setLikesCount(int likesCount) {
            this.likesCount = likesCount;
        }

        public int getRepliesCount() {
            return this.repliesCount;
        }

        public void setRepliesCount(int repliesCount) {
            this.repliesCount = repliesCount;
        }

        public Long getTimestamp() { return timestamp; }

        public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }

        public String getText() { return this.text; }

        public void setText(String text) {
            this.text = text;
        }

    public ArrayList<String> getLikers() {
        return likers;
    }

    public void setLikers(ArrayList<String> likers) {
        this.likers = likers;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentId='" + commentId + '\'' +
                ", authorId='" + authorId + '\'' +
                ", parentPostId='" + parentPostId + '\'' +
                ", likesCount=" + likesCount +
                ", repliesCount=" + repliesCount +
                ", text='" + text + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

