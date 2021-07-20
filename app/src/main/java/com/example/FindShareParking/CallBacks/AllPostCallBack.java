package com.example.FindShareParking.CallBacks;

import com.example.FindShareParking.Objects.Post;

import java.util.ArrayList;

public interface AllPostCallBack {

    void allPostsCallBack(ArrayList<Post> newPosts);
    void addNewPost(ArrayList<Post> posts);
    void postsWasChanges(ArrayList<Post> posts);
    void setRefreshingOff();


}
