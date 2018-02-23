package com.linkedin.replica.wall.handlers.impl;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import com.linkedin.replica.wall.config.DatabaseConnection;
import com.linkedin.replica.wall.handlers.DatabaseHandler;
import com.linkedin.replica.wall.models.Bookmark;
import com.linkedin.replica.wall.models.Post;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ArangoWallHandler implements DatabaseHandler {
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

    public String addBookmark(Bookmark bookmark) {
        String userCollection = properties.getProperty(properties.getProperty("collections.users.name"));
        String userId = bookmark.getUserId();

        String getUserQuery = "FOR t IN @userCollection FILTER t.userId == @userId RETURN t";
        String message = "";
        try {
            BaseDocument user = arangoDB.db(dbName).collection(userCollection).
                    getDocument(getUserQuery, BaseDocument.class);
            List<Object> l = (List<Object>) user.getAttribute("bookmarks");
            if (l == null)
                l = new ArrayList<Object>();
            l.add(bookmark);
            arangoDB.db(dbName).collection(userCollection).updateDocument("bookmarks", l);
            message = "Success to add bookmark";

        } catch (ArangoDBException e) {
            System.err.println("Failed to delete bookmark. " + e.getMessage());
            message = "Failed to add bookmark. " + e.getMessage();
        }
        return message;
    }


    public String deleteBookmark(Bookmark bookmark) {
        String userCollection = properties.getProperty(properties.getProperty("collections.users.name"));
        String userId = bookmark.getUserId();
        String getUserQuery = "FOR t IN @userCollection FILTER t.userId == @userId RETURN t";
        String message = "";
        try {
            BaseDocument user = arangoDB.db(dbName).collection(userCollection).
                    getDocument(getUserQuery, BaseDocument.class);
            List<Object> l = (List<Object>) user.getAttribute("bookmarks");
            if (l == null)
                l = new ArrayList<Object>();
            l.remove(bookmark);
            arangoDB.db(dbName).collection(userCollection).updateDocument("bookmarks", l);
            message = "Success to add bookmark";

        } catch (ArangoDBException e) {
            System.err.println("Failed to delete bookmark. " + e.getMessage());
            message = "Failed to delete bookmark";
        }
        return message;
    }

    public List<Post> getPosts() {
        return null;
    }

    public String addPost() {
        return null;

    }

    public String editPost() {
        return null;

    }

    public String deletePost() {
        return null;

    }

    public List<Post> getComments() {
        return null;
    }

    public String addComment() {
        return null;

    }

    public String editComment() {
        return null;

    }

    public String deleteComment() {
        return null;

    }

    public List<Post> getReplies() {
        return null;
    }

    public String addReply() {
        return null;

    }

    public String editReply() {
        return null;

    }

    public String deleteReply() {
        return null;

    }

    public List<Post> getlikes() {
        return null;
    }

    public String addLike() {
        return null;

    }

    public String deleteLike() {
        return null;

    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        DatabaseHandler arangoWallHandler = new ArangoWallHandler();

        arangoWallHandler.addBookmark(new )

    }
}
