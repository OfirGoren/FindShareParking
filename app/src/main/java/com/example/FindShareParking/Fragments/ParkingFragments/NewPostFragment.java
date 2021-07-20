package com.example.FindShareParking.Fragments.ParkingFragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aviadmini.quickimagepick.PickCallback;
import com.aviadmini.quickimagepick.PickSource;
import com.aviadmini.quickimagepick.QiPick;
import com.bumptech.glide.Glide;
import com.example.FindShareParking.CallBacks.ImageUriCallBack;
import com.example.FindShareParking.CallBacks.newPostCallBack;
import com.example.FindShareParking.Objects.FireStoreManager;
import com.example.FindShareParking.Objects.MapManagerPost;
import com.example.FindShareParking.Objects.Post;
import com.example.FindShareParking.R;
import com.example.FindShareParking.Utils.AuthUtils;
import com.example.FindShareParking.databinding.FragmentNewPostBinding;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Objects;

public class NewPostFragment extends Fragment implements ImageUriCallBack {


    private FragmentNewPostBinding binding;
    private newPostCallBack newPostCallBack;
    private Context context;
    private Uri photoUri;
    private FireStoreManager fireStoreManager;
    private MapManagerPost mapManagerPost;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNewPostBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        initValues();
        activateMap();
        initListeners();
        initObjects();
        initCallBacks();

        return view;
    }

    private void initValues() {
        photoUri = null;
    }


    private void initCallBacks() {
        fireStoreManager.initImageUriCallBack(this);
    }

    private void initObjects() {
        fireStoreManager = new FireStoreManager();
    }

    private void initListeners() {
        binding.cameraIcon.setOnClickListener(takePhotoParking);
        binding.postBtn.setOnClickListener(postBtn);
    }


    private final View.OnClickListener postBtn = v -> handleRequestPublishNewPost();


    private void handleRequestPublishNewPost() {
        boolean emptyLocationParking = checkAndHandleEmptyLocationParking();
        boolean photoIsEmpty = checkAndHandleEmptyPhoto();

        if (!emptyLocationParking && !photoIsEmpty) {
            //save the photo in storage DB and return from call back the uri photo
            fireStoreManager.savePhotoToStorageUri(photoUri);
            if (newPostCallBack != null) {
                newPostCallBack.closeNewPostFragmentCallBack();
                newPostCallBack.activateLinearProgressBar();
            }

        }

    }

    private boolean checkAndHandleEmptyPhoto() {
        if (photoUri == null) {
            binding.errorMsgImageRequired.setVisibility(View.VISIBLE);
            return true;
        } else {
            binding.errorMsgImageRequired.setVisibility(View.INVISIBLE);
            return false;
        }

    }


    private boolean checkAndHandleEmptyLocationParking() {
        boolean emptyLocationParking = false;
        if (getLocationParking().equals("")) {
            emptyLocationParking = true;
            setAndEnabledErrorMsgParkingLocation();
        } else {
            binding.locationParking.setErrorEnabled(false);
        }
        return emptyLocationParking;
    }

    private void setAndEnabledErrorMsgParkingLocation() {
        binding.locationParking.setErrorEnabled(true);
        binding.locationParking.setError(getString(R.string.location_required));
    }


    private Post getNewPostObject(String imageUri) {
        Post post = new Post()
                .setTypeOfParking(getStringNameOfCheckedButtonToggleGroup())
                .setLocationParking(getLocationParking())
                .setImageLocationStorage(imageUri)
                .setMoreInfo(getMoreInfo())
                .setCurrentUserId(AuthUtils.getCurrentUser())
                .setFiledId(String.valueOf(System.currentTimeMillis()));


        if (mapManagerPost.getLocation() != null) {
            post.setLatitude(String.valueOf(mapManagerPost.getLocation().getLatitude()));
            post.setLongitude(String.valueOf(mapManagerPost.getLocation().getLongitude()));
        }


        return post;

    }

    private String getMoreInfo() {
        return Objects.requireNonNull(binding.textMoreInfo.getEditText()).getText().toString();
    }

    private String getLocationParking() {
        return Objects.requireNonNull(binding.locationParking.getEditText()).getText().toString();
    }


    private String getStringNameOfCheckedButtonToggleGroup() {
        int buttonId = binding.toggleButton.getCheckedButtonId();
        MaterialButton button = binding.toggleButton.findViewById(buttonId);
        return button.getText().toString();

    }


    private final View.OnClickListener takePhotoParking = v -> dispatchTakePictureIntent();


    private void dispatchTakePictureIntent() {
        QiPick.in(this)
                .fromCamera();


    }

    private final PickCallback mCallback = new PickCallback() {
        @Override
        public void onImagePicked(@NonNull final PickSource pPickSource, final int pRequestType, @NonNull final Uri pImageUri) {
            photoUri = pImageUri;
            glide(pImageUri);
            binding.cameraIcon.setVisibility(View.GONE);
        }

        @Override
        public void onMultipleImagesPicked(final int pRequestType, @NonNull final List<Uri> pImageUris) {
            this.onImagePicked(PickSource.DOCUMENTS, pRequestType, pImageUris.get(0));
        }

        @Override
        public void onError(@NonNull final PickSource pPickSource, final int pRequestType, @NonNull final String pErrorString) {
        }

        @Override
        public void onCancel(@NonNull final PickSource pPickSource, final int pRequestType) {

        }

    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        QiPick.handleActivityResult(context.getApplicationContext(), requestCode, resultCode, data, this.mCallback);


    }

    private void glide(Uri imageUri) {
        Glide.with(this)
                .load(imageUri)
                .centerCrop()
                .into(binding.imageParking);

    }

    // activate map with current location
    private void activateMap() {
        SupportMapFragment supportMapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapManagerPost = new MapManagerPost(context.getApplicationContext(), supportMapFragment);
        mapManagerPost.activateMapWithCurrentLocation();
    }

    // get image uri callBack to save in post
    @Override
    public void imageUriCallBack(String imageUri) {
        saveNewPostInFireStore(imageUri);
    }

    private void saveNewPostInFireStore(String imageUri) {
        Post post = getNewPostObject(imageUri);
        fireStoreManager.saveNewPostInFireStore(post);
    }

    public void initNewPOstFragmentCallBack(newPostCallBack newPostCallBack) {
        this.newPostCallBack = newPostCallBack;
    }
}
