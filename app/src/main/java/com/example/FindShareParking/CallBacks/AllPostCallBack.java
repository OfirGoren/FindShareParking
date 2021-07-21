package com.example.FindShareParking.CallBacks;

import com.example.FindShareParking.Objects.Post;

import java.util.ArrayList;

public interface AllPostCallBack {

    void allPostsCallBack(ArrayList<Post> newPosts);
    void postsWasChanges(ArrayList<Post> posts);



}
