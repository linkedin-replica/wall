package com.linkedin.replica.wall.models;

import com.arangodb.entity.DocumentField;

import java.util.ArrayList;

public class UserProfile {
    private ArrayList<Bookmark> bookmarks;

    @DocumentField(DocumentField.Type.KEY)
    private String userId;

    private String email;
    private String firstName;
    private String lastName;
    private ArrayList<String> friendsList;
    public UserProfile() {

    }

    public UserProfile(String email, String firstName, String lastName) {
        this();
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bookmarks = new ArrayList<>();
        this.friendsList = new ArrayList<>();
    }


    public ArrayList<String> getFriendsList() {
        return this.friendsList;
    }

    public void setFriendsList(ArrayList<String> friendsList) {
        this.friendsList = friendsList;
    }

    public ArrayList<Bookmark> getBookmarks(){return this.bookmarks;}

    public void setBookmarks(ArrayList<Bookmark> bookmarks){this.bookmarks = bookmarks;}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "UserProfile {" +
                "key='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}