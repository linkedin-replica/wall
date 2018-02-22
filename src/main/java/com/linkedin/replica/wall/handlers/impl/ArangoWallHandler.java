package com.linkedin.replica.wall.handlers.impl;

import com.arangodb.ArangoDB;
import com.arangodb.entity.BaseDocument;
import com.linkedin.replica.wall.config.DatabaseConnection;
import com.linkedin.replica.wall.handlers.WallHandler;
import com.linkedin.replica.wall.models.Bookmark;
import com.linkedin.replica.wall.models.Post;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class ArangoWallHandler implements WallHandler {
    private Properties properties;
    private ArangoDB arangoDB;
    private  String dbName;

    public List<Bookmark> getBookmarks() {
        return null;
    }

    private ArangoWallHandler() throws IOException, ClassNotFoundException {
        properties = new Properties();
        properties.load(new FileInputStream("config"));
        dbName = properties.getProperty(properties.getProperty("collections.users.name"));
        arangoDB = DatabaseConnection.getInstance().getArangodb();
    }
    public void addBookmark(Bookmark bookmark) throws IOException, ClassNotFoundException {
        String userCollection = properties.getProperty(properties.getProperty("collections.users.name"));
        String userId = bookmark.getUserId();
        String query = "FOR t IN @userCollection FILTER t.userId == @userId RETURN t";

        BaseDocument user = arangoDB.db(dbName).collection(userCollection).
                getDocument(query, BaseDocument.class);


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
