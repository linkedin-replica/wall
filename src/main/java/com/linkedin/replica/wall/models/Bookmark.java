package com.linkedin.replica.wall.models;

public class Bookmark {
    private String  postId, userId;
    /**
     * bookmark constructor.
     */
    public Bookmark(String userId, String postId){
        this.postId = postId;
        this.userId = userId;
    }

    /**
     *
     * @return postId
     */
    public String getPostId() {
        return postId;
    }

    /**
     *
     * @return bookmark userId
     */
    public String getUserId() {
        return userId;
    }
}

