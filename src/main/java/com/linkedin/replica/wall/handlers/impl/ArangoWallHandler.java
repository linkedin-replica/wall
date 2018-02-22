package com.linkedin.replica.wall.handlers.impl;

import com.arangodb.entity.BaseDocument;
import com.linkedin.replica.wall.config.DatabaseConnection;
import com.linkedin.replica.wall.handlers.WallHandler;
import com.linkedin.replica.wall.models.Bookmark;
import com.linkedin.replica.wall.models.Post;

import java.awt.print.Book;
import java.util.List;

public class ArangoWallHandler implements WallHandler {
    public List<Bookmark> getBookmarks() {
        return null;
    }



    public void addBookmark(Bookmark bookmark) {
       // BaseDocument user = DatabaseConnection.getInstance().getArangodb().db().collection("").getDocument("myKey",


    }



    public void deleteBookmark() {

    }

    public List<Post> getPosts() {
        return null;
    }

    public void addPost() {

    }

    public void editPost() {

    }

    public void deletePost() {

    }

    public List<Post> getComments() {
        return null;
    }

    public void addComment() {

    }

    public void editComment() {

    }

    public void deleteComment() {

    }

    public List<Post> getReplies() {
        return null;
    }

    public void addReply() {

    }

    public void editReply() {

    }

    public void deleteReply() {

    }

    public List<Post> getlikes() {
        return null;
    }

    public void addLike() {

    }

    public void deleteLike() {

    }
}
