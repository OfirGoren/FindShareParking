package com.example.FindShareParking.Objects;

import com.example.FindShareParking.Utils.AuthUtils;

import java.util.ArrayList;
import java.util.Objects;

public class Post {


    private String filedId;
    private String currentUserId;
    private String typeOfParking;
    private String locationParking;
    private String imageLocationStorage;
    private String moreInfo;
    private String longitude;
    private String latitude;
    private final ArrayList<String> makesLike;


    public Post(String filedId, String typeOfParking, String locationParking, String imageLocationStorage, String moreInfo, String latitude
            , String longitude, String currentUserId) {
        this.filedId = filedId;
        this.typeOfParking = typeOfParking;
        this.locationParking = locationParking;
        this.imageLocationStorage = imageLocationStorage;
        this.moreInfo = moreInfo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.currentUserId = currentUserId;
        makesLike = new ArrayList<>();
    }


    public Post() {
        this("", "", "", "", "",
                "0", "0", "");
    }


    public String getCurrentUserId() {
        return currentUserId;
    }

    public Post setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;

        return this;
    }


    public Post setTypeOfParking(String typeOfParking) {
        this.typeOfParking = typeOfParking;
        return this;
    }

    public Post setLocationParking(String locationParking) {
        this.locationParking = locationParking;
        return this;
    }

    public Post setImageLocationStorage(String imageLocationStorage) {
        this.imageLocationStorage = imageLocationStorage;
        return this;
    }

    public Post setMoreInfo(String moreInfo) {
        this.moreInfo = moreInfo;
        return this;
    }

    public Post setLatitude(String latitude) {
        this.latitude = latitude;
        return this;
    }

    public Post setLongitude(String longitude) {
        this.longitude = longitude;
        return this;
    }


    public String getTypeOfParking() {
        return typeOfParking;
    }

    public String getLocationParking() {
        return locationParking;
    }

    public String getImageLocationStorage() {
        return imageLocationStorage;
    }

    public String getMoreInfo() {
        return moreInfo;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public Post setFiledId(String filedId) {
        this.filedId = filedId;
        return this;
    }


    public String getFiledId() {
        return filedId;
    }


    public void addNewAccountWhoDidLike(String account) {
        this.makesLike.add(account);

    }

    public int getAmountOfLikes() {
        return this.makesLike.size();

    }

    public ArrayList<String> getMakesLike() {
        return makesLike;
    }


    public void deleteLike(String deleteId) {

        this.makesLike.remove(deleteId);

    }

    public boolean isCurrentUserMakeLike() {
        return this.makesLike.contains(AuthUtils.getCurrentUser());

    }


    public static boolean isDeleteLikeOnPost(Post beforeChange, Post afterChange) {

        ArrayList<String> whoDidLike = new ArrayList<>(beforeChange.getMakesLike());
        whoDidLike.removeAll(afterChange.makesLike);
        return whoDidLike.size() > 0;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post)) return false;
        Post post = (Post) o;
        return Objects.equals(filedId, post.filedId) &&
                Objects.equals(currentUserId, post.currentUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filedId, currentUserId);
    }

    public static boolean isNewLikeOnPost(Post beforeChange, Post afterChange) {
        ArrayList<String> whoDidLike = new ArrayList<>(afterChange.getMakesLike());
        whoDidLike.removeAll(beforeChange.makesLike);
        return whoDidLike.size() > 0;
    }


    public static ArrayList<String> getWhoDidLike(Post beforeChange, Post afterChange) {
        ArrayList<String> whoDidLike = new ArrayList<>(afterChange.getMakesLike());
        whoDidLike.removeAll(beforeChange.makesLike);
        return whoDidLike;

    }

    public static ArrayList<String> getWhoRemoveLike(Post beforeChange, Post afterChange) {
        ArrayList<String> whoDidLike = new ArrayList<>(beforeChange.getMakesLike());
        whoDidLike.removeAll(afterChange.makesLike);

        return whoDidLike;

    }

    public static void copyMakeLikes(Post toCopy, Post FromCopy) {
        toCopy.getMakesLike().clear();
        toCopy.getMakesLike().addAll(FromCopy.getMakesLike());

    }


}

