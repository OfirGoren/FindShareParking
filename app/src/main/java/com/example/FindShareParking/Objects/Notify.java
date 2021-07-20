package com.example.FindShareParking.Objects;

import java.util.Objects;

public class Notify {

    private String whoDidLikeId;
    private String currentUserId;
    private String notifyId;

    public Notify(String whoDidLikeId, String currentUserId, String notifyId) {
        this.whoDidLikeId = whoDidLikeId;
        this.currentUserId = currentUserId;
        this.notifyId = notifyId;
    }

    public Notify() {
        this("", "", "");

    }

    public String getWhoDidLikeId() {
        return whoDidLikeId;
    }

    public Notify setWhoDidLikeId(String whoDidLikeId) {
        this.whoDidLikeId = whoDidLikeId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notify)) return false;
        Notify notify = (Notify) o;
        return currentUserId.equals(notify.currentUserId) &&
                notifyId.equals(notify.notifyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentUserId, notifyId);
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public Notify setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
        return this;
    }

    public String getNotifyId() {
        return notifyId;
    }

    public Notify setNotifyId(String notifyId) {
        this.notifyId = notifyId;
        return this;
    }

}
