package com.example.FindShareParking.CallBacks;

import com.example.FindShareParking.Objects.MyRecyclerViewNotify;
import com.example.FindShareParking.Objects.User;

public interface GetWhoDidLikeCallBack {
    void getWhoDidLikeCallBack(User user , MyRecyclerViewNotify.ViewHolder holder);
}
