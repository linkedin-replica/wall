package com.linkedin.replica.wall.models;
import com.arangodb.entity.DocumentField;

public class Bookmark {

	private String _key;
    private String bookmarkId;
    private String postId;
    private String userId;

    /**
     * bookmark constructor.
     */

    public  Bookmark(){
    }

    public void setBookmarkId(String bookmarkId) {
        this.bookmarkId = bookmarkId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBookmarkId() {

        return bookmarkId;
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
    /**
     * equals method of bookmarks
     * @param b
     * @return
     */
    @Override
    public boolean equals(Object b){
        Bookmark bookmark = (Bookmark)b;
        return bookmark.getPostId().equals(this.postId) && bookmark.getUserId().equals(this.userId);
    }
}

