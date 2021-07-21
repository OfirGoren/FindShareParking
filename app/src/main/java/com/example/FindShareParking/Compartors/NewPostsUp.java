package com.example.FindShareParking.Compartors;

import com.example.FindShareParking.Objects.Post;

import java.util.Comparator;

public class NewPostsUp implements Comparator<Post> {

    @Override
    public int compare(Post o1, Post o2) {
        return (int) (Long.parseLong(o2.getFiledId()) - Long.parseLong(o1.getFiledId()));
    }
}
