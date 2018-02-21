package com.linkedin.replica.wall.models;

public class Comment {

        private int commentId,authorId,parentPostId,likesCount,repliesCount;
        private String [] images;
        private String [] urls;
        private int [] mentions;
        private String text,timeStamp;

        public Comment(int commentId,int authorId,int parentPostId, int likesCount,int repliesCount, String [] images,String [] urls,int [] mentions, String text,String timeStamp){

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

        public int getCommentId() {
            return commentId;
        }

        public void setCommentId(int commentId) {
            this.commentId = commentId;
        }

        public int getAuthorId() {
            return authorId;
        }

        public void setAuthorId(int authorId) {
            this.authorId = authorId;
        }

        public int getParentPostId() {
            return parentPostId;
        }

        public void setParentPostId(int parentPostId) {
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

        public int[] getMentions() {
            return mentions;
        }

        public void setMentions(int[] mentions) {
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

