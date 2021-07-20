package com.example.FindShareParking.CallBacks;

import com.example.FindShareParking.Objects.Notify;

public interface PostNotifyCallBack {
    void openNewPostFragment();
    void newLike(Notify notify);
    void deleteNotify(Notify notify);
    void increaseBadgeCallBack(int size);
    void decreaseBadgeCallBack(int size);


}
