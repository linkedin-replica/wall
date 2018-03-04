package com.linkedin.replica.wall.models;
import com.arangodb.entity.DocumentField;

public class Bookmark {
    @DocumentField(DocumentField.Type.KEY)
    private String  postId;
    private String userId;

    /**
     * bookmark constructor.
     */

    private  Bookmark(){
        super();
    }
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

