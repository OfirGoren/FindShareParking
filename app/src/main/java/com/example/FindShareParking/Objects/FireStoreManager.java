package com.example.FindShareParking.Objects;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.example.FindShareParking.CallBacks.AllPostCallBack;
import com.example.FindShareParking.CallBacks.CurrentUserImageCallBack;
import com.example.FindShareParking.CallBacks.GetAllNotifyCallBack;
import com.example.FindShareParking.CallBacks.GetCurrentUserDetailsCallBack;
import com.example.FindShareParking.CallBacks.GetUserCallBack;
import com.example.FindShareParking.CallBacks.GetWhoDidLikeCallBack;
import com.example.FindShareParking.CallBacks.ImageUriCallBack;
import com.example.FindShareParking.CallBacks.MyPostsCallBack;
import com.example.FindShareParking.Utils.AuthUtils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class FireStoreManager {
    public static final String ALL_POST = "AllPost";
    private static final String USERS = "users";
    private static final String POST = "post";
    private static final String USER_PHOTO = "userPhoto";
    private static final String ZERO = "0";
    private static final String NOTIFY = "Notify";
    private static final String NAME = "name";
    private final FirebaseFirestore db;
    private AllPostCallBack allPostCallBack;
    private ImageUriCallBack imageUriCallBack;
    private MyPostsCallBack myPostsCallBack;
    private GetUserCallBack getUserCallBack;
    private CurrentUserImageCallBack currentUserImageCallBack;
    private GetWhoDidLikeCallBack getWhoDidLikeCallBack;
    private ListenerRegistration registrationMyPost;
    private ListenerRegistration registrationAllPost;
    private GetAllNotifyCallBack getAllNotifyCallBack;
    private GetCurrentUserDetailsCallBack getCurrentUserDetailsCallBack;
    private boolean returnAllPost;
    private boolean isFirstTimeGetMyPost;


    public FireStoreManager() {
        db = FirebaseFirestore.getInstance();
        returnAllPost = true;
        isFirstTimeGetMyPost = true;

    }

    /*

     */
    public static void deleteLikeFromFireStore(Post post) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(ALL_POST).document(post.getFiledId()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        handleDeleteLikeFireStore(task);
                    }
                });


    }

    private static void handleDeleteLikeFireStore(Task<DocumentSnapshot> task) {
        DocumentSnapshot documentSnapshot = task.getResult();
        Post updatePost = documentSnapshot.toObject(Post.class);
        if (updatePost != null) {
            updatePost.deleteLike(AuthUtils.getCurrentUser());
            updateAllPostInFireStore(updatePost);
            updatePostInMyPostFireStore(updatePost);
        }
    }


    public void updateUserPhotoInFireStore(String userPhoto) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference myRef = db.collection(USERS).document(AuthUtils.getCurrentUser());
        myRef.update(USER_PHOTO, userPhoto).addOnCompleteListener(task -> {

        });


    }

    public void getNameAndPhotoWhoDidLikeCallBack(String id, MyRecyclerViewNotify.ViewHolder holder) {

        db.collection(USERS).document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                User user = documentSnapshot.toObject(User.class);
                if (getWhoDidLikeCallBack != null) {
                    getWhoDidLikeCallBack.getWhoDidLikeCallBack(user, holder);
                }
            }
        });

    }


    public void getUserFromFireStoreCallBack(MyRecyclerViewAdapter.ViewHolder holder, Post post) {
        db.collection(USERS).document(post.getCurrentUserId()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                User user = documentSnapshot.toObject(User.class);
                if (getUserCallBack != null) {
                    getUserCallBack.getUserCallBack(user, holder, post);
                }
            }
        });


    }


    public void initAllPostCallBack(AllPostCallBack allPostCallBack) {
        this.allPostCallBack = allPostCallBack;

    }

    public void initMyPostCallBack(MyPostsCallBack myPostsCallBack) {
        this.myPostsCallBack = myPostsCallBack;
    }

    public void initGetAllNotifyCallBack(GetAllNotifyCallBack getAllNotifyCallBack) {
        this.getAllNotifyCallBack = getAllNotifyCallBack;
    }

    public void initGetUserCallBack(GetUserCallBack getUserCallBack) {
        this.getUserCallBack = getUserCallBack;
    }

    public void initGetWhoDidLikeCallBack(GetWhoDidLikeCallBack getWhoDidLikeCallBack) {
        this.getWhoDidLikeCallBack = getWhoDidLikeCallBack;
    }

    public void initCurrentUserImageCallBack(CurrentUserImageCallBack currentUserImageCallBack) {
        this.currentUserImageCallBack = currentUserImageCallBack;
    }

    public void initGetCurrentUserDetailsCallBack(GetCurrentUserDetailsCallBack getCurrentUserDetailsCallBack) {
        this.getCurrentUserDetailsCallBack = getCurrentUserDetailsCallBack;
    }

    public static void saveNewUserToFireStore(User user) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference newUser = db.collection(USERS).document(AuthUtils.getCurrentUser());
        newUser.set(user).addOnCompleteListener(task -> {

        });
    }


    public void savePhotoToStorageBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        // Create a storage reference from our app
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        StorageReference mountainImagesRef = storageRef.child("image/" + System.currentTimeMillis());

        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> {

        }).addOnSuccessListener(this::getImageUri);

    }


    public void savePhotoToStorageUri(Uri uri) {

        // Create a storage reference from our app
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        StorageReference mountainImagesRef = storageRef.child("image/" + System.currentTimeMillis());

        UploadTask uploadTask = mountainImagesRef.putFile(uri);
        uploadTask.addOnFailureListener(exception -> {


        }).addOnSuccessListener(this::getImageUri);


    }


    private void getImageUri(UploadTask.TaskSnapshot taskSnapshot) {
        if (taskSnapshot.getMetadata() != null) {
            if (taskSnapshot.getMetadata().getReference() != null) {
                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                result.addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    if (imageUriCallBack != null) {
                        imageUriCallBack.imageUriCallBack(imageUrl);

                    }

                });
            }
        }

    }

    public void initImageUriCallBack(ImageUriCallBack imageUriCallBack) {
        this.imageUriCallBack = imageUriCallBack;
    }


    /**
     * save the post in fire store , including the user that was publish this post
     * (take the user information from DB)
     */
    public void saveNewPostInFireStore(Post post) {
        savePostInMyPost(post);
        savePostInAllPost(post);
    }

    private void savePostInAllPost(Post post) {
        DocumentReference newUser = db.collection(ALL_POST).document(post.getFiledId());
        newUser.set(post).addOnSuccessListener(aVoid -> {
        });

    }


    public void getUserPhotoFromFireStore() {
        db.collection(USERS).document(AuthUtils.getCurrentUser()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot result = task.getResult();
                        User user = result.toObject(User.class);
                        if (currentUserImageCallBack != null && user != null) {
                            currentUserImageCallBack.currentUserImageCallBack(user.getUserPhoto());
                        }
                    }
                });
    }

    private void savePostInMyPost(Post post) {
        DocumentReference newUser = db.collection(USERS).document(post.getCurrentUserId())
                .collection(POST).document(post.getFiledId());
        newUser.set(post).addOnSuccessListener(aVoid -> {
        });
    }


    /*
    return in call back all the posts in another users that post (including my posts)
    and also array that contains only my posts
    */
    public void getAllPost() {

        // save the time when first time get all post from DB (register to account)
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection(ALL_POST);
        registrationAllPost = query
                .addSnapshotListener((value, error) -> {
                    ArrayList<Post> allPost = new ArrayList<>();
                    if (value != null) {
                        //when there isn't collection of post
                        if (value.size() == 0) {
                            // create one
                            createAllPostCollection();
                            //there is already collection in FireStore
                        } else {
                            Log.d("getAllPost", "getAllPost: " + returnAllPost);
                            // we want to return to all post
                            if (returnAllPost) {
                                // get all post from fire store
                                insertAllPostToArray(allPost, value);
                            } else {
                                //insert only the post that was changed
                                insertOnlyPostWasChanges(allPost, value);
                            }
                        }
                    }
//                    getOnlyPostsAccordingTypeOfParking(allPost);
                    passDataAccordingReturnAllPostValue(allPost);

                });


    }

    private void passDataAccordingReturnAllPostValue(ArrayList<Post> allPost) {
        if (allPostCallBack != null) {
            if (returnAllPost) {
                allPostCallBack.allPostsCallBack(allPost);
            } else {
                allPostCallBack.postsWasChanges(allPost);
            }
            returnAllPost = false;
        }
    }

    private void insertOnlyPostWasChanges(ArrayList<Post> allPost, QuerySnapshot value) {
        for (DocumentChange documentSnapshots : value.getDocumentChanges()) {

            if (!documentSnapshots.getDocument().getId().equals(ZERO)) {
                Post post = documentSnapshots.getDocument().toObject(Post.class);

                allPost.add(post);
            }
        }
    }

    private void insertAllPostToArray(ArrayList<Post> allPost, QuerySnapshot value) {
        for (DocumentSnapshot documentSnapshots : value.getDocuments()) {
            if (!documentSnapshots.getId().equals(ZERO)) {
                Post post = documentSnapshots.toObject(Post.class);
                allPost.add(post);
            }
        }
    }

    public void setReturnAllPost(Boolean value) {
        this.returnAllPost = value;
    }




    public void stopAllPostSnapShotListener() {

        if (registrationAllPost != null) {
            registrationAllPost.remove();
        }

    }

    public void stopMyPostSnapShotListener() {
        if (registrationMyPost != null) {
            registrationMyPost.remove();
        }

    }


    private void createPostCollection() {
        db.collection(USERS).document(AuthUtils.getCurrentUser()).
                collection(POST).document(ZERO).set(new HashMap<String, String>())
                .addOnCompleteListener(task -> {
                });
    }

    private void createAllPostCollection() {
        db.collection(ALL_POST).document(ZERO).set(new HashMap<String, String>())
                .addOnCompleteListener(task -> {
                });
    }


    public void getMyPost() {


        FirebaseFirestore db1 = FirebaseFirestore.getInstance();
        Query query = db1.collection(USERS).document(AuthUtils.getCurrentUser()).collection(POST);
        registrationMyPost = query
                .addSnapshotListener((value, error) -> {
                    ArrayList<Post> myPost = new ArrayList<>();
                    if (value != null) {
                        //when there isn't collection of post
                        if (value.size() == 0) {
                            // create
                            createPostCollection();
                        } else {
                            if (isFirstTimeGetMyPost) {
                                insertAllMyPostsToArray(myPost, value);
                            } else {
                                insertMyPostsWasChanges(myPost, value);
                            }
                        }
                    }
                    passList(myPost);
                });
    }

    private void passList(ArrayList<Post> myPost) {
        if (myPostsCallBack != null) {
            if (isFirstTimeGetMyPost) {
                myPostsCallBack.myPostsCallBack(myPost);
                isFirstTimeGetMyPost = false;
            } else {
                myPostsCallBack.postChanges(myPost);
            }
            isFirstTimeGetMyPost = false;
        }
    }

    private void insertMyPostsWasChanges(ArrayList<Post> myPost, QuerySnapshot value) {
        for (DocumentChange documentSnapshots : value.getDocumentChanges()) {
            if (!documentSnapshots.getDocument().getId().equals(ZERO)) {
                Post post = documentSnapshots.getDocument().toObject(Post.class);
                if (!myPost.contains(post)) {
                    myPost.add(post);
                }
            }
        }
    }

    private void insertAllMyPostsToArray(ArrayList<Post> myPost, QuerySnapshot value) {
        for (DocumentSnapshot documentSnapshots : value.getDocuments()) {
            if (!documentSnapshots.getId().equals(ZERO)) {
                Post post = documentSnapshots.toObject(Post.class);
                if (!myPost.contains(post)) {
                    myPost.add(post);
                }
            }
        }
    }


    // delete from my post and all post
    public void deletePost(Post post) {
        deleteFromMyPostList(post);
        deleteFromAllPost(post);

    }

    public void deleteFromAllPost(Post post) {
        db.collection(ALL_POST).document(post.getFiledId()).delete();
    }


    public void deleteFromMyPostList(Post post) {
        db.collection(USERS).document(post.getCurrentUserId()).collection(POST).
                document(post.getFiledId()).delete();

    }


    public static void updateLike(Post post) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(ALL_POST).document(post.getFiledId()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        Post updatePost = documentSnapshot.toObject(Post.class);
                        if (updatePost != null) {
                            updatePost.addNewAccountWhoDidLike(AuthUtils.getCurrentUser());
                            updateAllPostInFireStore(updatePost);
                            updatePostInMyPostFireStore(updatePost);
                        }
                    }

                });
    }

    private static void updatePostInMyPostFireStore(Post updatePost) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(USERS).document(updatePost.getCurrentUserId()).collection(POST).document(updatePost.getFiledId())
                .set(updatePost).addOnSuccessListener(unused -> {
        });
    }


    private static void updateAllPostInFireStore(Post updatePost) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(ALL_POST).document(updatePost.getFiledId()).set(updatePost).addOnSuccessListener(unused -> {
        });
    }


    public void getCurrentUser() {
        db.collection(USERS).document(AuthUtils.getCurrentUser()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (getCurrentUserDetailsCallBack != null) {
                            DocumentSnapshot result = task.getResult();
                            User user = result.toObject(User.class);
                            getCurrentUserDetailsCallBack.getCurrentUserCallBack(user);
                        }
                    }
                });
    }


    public void getAllNotify() {
        ArrayList<Notify> notifies = new ArrayList<>();
        db.collection(USERS).document(AuthUtils.getCurrentUser()).collection(NOTIFY).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() > 0) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if (!document.getId().equals(ZERO)) {
                                    Notify notify = document.toObject(Notify.class);
                                    notifies.add(notify);
                                }
                            }
                            if (getAllNotifyCallBack != null) {
                                getAllNotifyCallBack.getAllNotifyCallBack(notifies);
                            }

                        } else {
                            crateNewCollectionNotify();
                        }
                    }
                });

    }

    private void crateNewCollectionNotify() {
        db.collection(USERS).document(AuthUtils.getCurrentUser()).collection(NOTIFY)
                .document(ZERO).set(new HashMap<String, String>());
    }


    public void uploadNotifyToFireStore(Notify notify) {
        db.collection(USERS).document(AuthUtils.getCurrentUser()).collection(NOTIFY).document(notify.getNotifyId()).set(notify)
                .addOnCompleteListener(task -> {

                });

    }


    public void deleteNotifyFromFireStore(Notify notify) {

        db.collection(USERS).document(AuthUtils.getCurrentUser()).collection(NOTIFY).document(notify.getNotifyId()).delete()
                .addOnCompleteListener(task -> {
                });

    }

    public static void updateNameInFireStore(String name) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(USERS).document(AuthUtils.getCurrentUser()).update(NAME, name);


    }

}


