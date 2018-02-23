package com.linkedin.replica.wall.handlers.impl;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.DocumentUpdateEntity;
import com.linkedin.replica.wall.config.DatabaseConnection;
import com.linkedin.replica.wall.handlers.WallHandler;
import com.linkedin.replica.wall.models.Bookmark;
import com.linkedin.replica.wall.models.Post;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ArangoWallHandler implements WallHandler {
    private Properties properties;
    private ArangoDB arangoDB;
    private String dbName;

    public List<Bookmark> getBookmarks() {
        return null;
    }

    public ArangoWallHandler() throws IOException, ClassNotFoundException {
        properties = new Properties();
        properties.load(new FileInputStream("config"));
        dbName = properties.getProperty(properties.getProperty("collections.users.name"));
        arangoDB = DatabaseConnection.getInstance().getArangodb();
    }

    public void addBookmark(Bookmark bookmark) {
        String userCollection = properties.getProperty(properties.getProperty("collections.users.name"));
        String userId = bookmark.getUserId();

        String getUserQuery = "FOR t IN @userCollection FILTER t.userId == @userId RETURN t";
        try {
            BaseDocument user = arangoDB.db(dbName).collection(userCollection).
                    getDocument(getUserQuery, BaseDocument.class);
            List<Object> l = (List<Object>) user.getAttribute("bookmarks");
            if (l == null)
                l = new ArrayList<Object>();
            l.add(bookmark);
            arangoDB.db(dbName).collection(userCollection).updateDocument("bookmarks", l);

        } catch (ArangoDBException e) {
            System.err.println("Failed to add bookmark. " + e.getMessage());
        }
    }


    public void deleteBookmark(Bookmark bookmark) {
        String userCollection = properties.getProperty(properties.getProperty("collections.users.name"));
        String userId = bookmark.getUserId();
        String getUserQuery = "FOR t IN @userCollection FILTER t.userId == @userId RETURN t";
        try {
            BaseDocument user = arangoDB.db(dbName).collection(userCollection).
                    getDocument(getUserQuery, BaseDocument.class);
            List<Object> l = (List<Object>) user.getAttribute("bookmarks");
            if (l == null)
                l = new ArrayList<Object>();
            l.remove(bookmark);
            arangoDB.db(dbName).collection(userCollection).updateDocument("bookmarks", l);

        } catch (ArangoDBException e) {
            System.err.println("Failed to delete bookmark. " + e.getMessage());
        }
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
