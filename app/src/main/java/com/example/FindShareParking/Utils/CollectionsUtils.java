package com.example.FindShareParking.Utils;

import com.example.FindShareParking.Compartors.NewPostsUp;
import com.example.FindShareParking.Objects.Post;

import java.util.ArrayList;
import java.util.Collections;

public class CollectionsUtils {


    public static void newPostUp(ArrayList<Post> posts) {
        Collections.sort(posts, new NewPostsUp());
    }
}
