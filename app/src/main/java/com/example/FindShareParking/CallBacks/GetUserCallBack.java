package com.example.FindShareParking.CallBacks;

import com.example.FindShareParking.Objects.MyRecyclerViewAdapter;
import com.example.FindShareParking.Objects.Post;
import com.example.FindShareParking.Objects.User;

public interface GetUserCallBack {
    void getUserCallBack(User user , MyRecyclerViewAdapter.ViewHolder holder  , Post post);
}
