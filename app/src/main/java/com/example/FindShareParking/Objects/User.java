package com.example.FindShareParking.Objects;

import java.util.Objects;

public class User {

    private String userPhoto;
    private String email;
    private String name;
    private String userId;


    public User(String userPhoto, String email, String name, String userId) {
        setUserPhoto(userPhoto);
        this.email = email;
        this.name = name;
        this.userId = userId;

    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    public User() {
        this("", "", "", "");
    }

    public String getUserId() {
        return userId;
    }

    public User setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public User setUserPhoto(String userPhoto) {
        if (userPhoto != null) {
            this.userPhoto = userPhoto;
        } else {
            this.userPhoto = "";
        }
        return this;
    }

    public String getUserPhoto() {
        return userPhoto;
    }


    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }


}
