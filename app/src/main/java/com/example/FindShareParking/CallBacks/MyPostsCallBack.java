package com.example.FindShareParking.CallBacks;

import com.example.FindShareParking.Objects.Post;

import java.util.ArrayList;

public interface MyPostsCallBack {

    void myPostsCallBack(ArrayList<Post> myPost);
    void postChanges(ArrayList<Post> posts);
}
