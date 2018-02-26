package com.linkedin.replica.wall.models;

import java.util.ArrayList;

public class Comment {

        private String commentId,authorId,parentPostId;
        private int likesCount,repliesCount;
        private ArrayList<String> images;
        private ArrayList<String> urls;
        private ArrayList<String> mentions;
        private String text,timeStamp;

        public Comment(String commentId,String authorId,String parentPostId, int likesCount,int repliesCount, ArrayList<String> images,ArrayList<String> urls,ArrayList<String> mentions, String text,String timeStamp){

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

        public ArrayList<String> getImages() {
            return this.images;
        }

        public void setImages(ArrayList<String> images) {
            this.images = images;
        }

        public ArrayList<String> getUrls() {
            return this.urls;
        }

        public void setUrls(ArrayList<String> urls) {
            this.urls = urls;
        }

        public ArrayList<String> getMentions() {
            return this.mentions;
        }

        public void setMentions(ArrayList<String> mentions) {
            this.mentions = mentions;
        }

        public String getText() {
            return this.text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getTimeStamp() {
            return this.timeStamp;
        }

        public void setTimeStamp(String timeStamp) {
            this.timeStamp = timeStamp;
        }
    }

