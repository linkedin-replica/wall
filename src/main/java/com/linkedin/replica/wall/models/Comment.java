package com.linkedin.replica.wall.models;

public class Comment {

        private String commentId,authorId,parentPostId;
        private int likesCount,repliesCount;
        private String [] images;
        private String [] urls;
        private String [] mentions;
        private String text,timeStamp;

        public Comment(String commentId,String authorId,String parentPostId, int likesCount,int repliesCount, String [] images,String [] urls,String [] mentions, String text,String timeStamp){

            this.commentId = commentId;
            this.authorId = authorId;
            this.parentPostId = parentPostId;
            this.likesCount = likesCount;
            this.repliesCount = repliesCount;
            this.images = images;
            this.urls = urls;
            this.mentions = mentions;
            this.text = text;
            this.timeStamp = timeStamp;

        }

        public String getCommentId() {
            return commentId;
        }

        public void setCommentId(String commentId) {
            this.commentId = commentId;
        }

        public String getAuthorId() {
            return authorId;
        }

        public void setAuthorId(String authorId) {
            this.authorId = authorId;
        }

        public String getParentPostId() {
            return parentPostId;
        }

        public void setParentPostId(String parentPostId) {
            this.parentPostId = parentPostId;
        }

        public int getLikesCount() {
            return likesCount;
        }

        public void setLikesCount(int likesCount) {
            this.likesCount = likesCount;
        }

        public int getRepliesCount() {
            return repliesCount;
        }

        public void setRepliesCount(int repliesCount) {
            this.repliesCount = repliesCount;
        }

        public String[] getImages() {
            return images;
        }

        public void setImages(String[] images) {
            this.images = images;
        }

        public String[] getUrls() {
            return urls;
        }

        public void setUrls(String[] urls) {
            this.urls = urls;
        }

        public String[] getMentions() {
            return mentions;
        }

        public void setMentions(String[] mentions) {
            this.mentions = mentions;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(String timeStamp) {
            this.timeStamp = timeStamp;
        }
    }

