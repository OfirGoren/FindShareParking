package com.example.FindShareParking.Fragments.ParkingFragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.FindShareParking.CallBacks.CurrentUserImageCallBack;
import com.example.FindShareParking.CallBacks.ImageUriCallBack;
import com.example.FindShareParking.CallBacks.NotifyPhotoChangedCallBack;
import com.example.FindShareParking.CallBacks.OpenFirstActivityCallBack;
import com.example.FindShareParking.CallBacks.OpenSettingsCallBack;
import com.example.FindShareParking.Objects.FireStoreManager;
import com.example.FindShareParking.R;
import com.example.FindShareParking.Utils.ToastUtils;
import com.example.FindShareParking.databinding.FragmentMoreBinding;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.material.navigation.NavigationView;

import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;

public class MoreFragment extends Fragment implements ImageUriCallBack, CurrentUserImageCallBack {

    private FragmentMoreBinding binding;
    private FireStoreManager fireStoreManager;

    private OpenSettingsCallBack openSettingsCallBack;
    private OpenFirstActivityCallBack openFirstActivityCallBack;
    private NotifyPhotoChangedCallBack notifyPhotoChangedCallBack;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMoreBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        initValues();
        initCallBacks();
        getUserPhotoCallBack();
        initListeners();

        return view;
    }

    private void initValues() {
        fireStoreManager = new FireStoreManager();
    }

    private void getUserPhotoCallBack() {
        fireStoreManager.getUserPhotoFromFireStore();
    }

    private void initListeners() {
        binding.layImageEdit.setOnClickListener(uploadImage);
        binding.navigationViewMore.setNavigationItemSelectedListener(navigationItemSelected);
    }

    private void initCallBacks() {
        fireStoreManager.initImageUriCallBack(this);
        fireStoreManager.initCurrentUserImageCallBack(this);
    }


    private final View.OnClickListener uploadImage = v -> openIntentActionImageCapture();


    private void openIntentActionImageCapture() {
        Activity activity = getActivity();
        if (activity != null) {
            ImagePicker.Companion.with(activity)
                    .crop()
                    .cropOval()
                    .maxResultSize(512, 512, true)
                    .createIntentFromDialog(intent -> {
                        Intrinsics.checkNotNullParameter(intent, "intent");
                        launcher.launch(intent);
                        return Unit.INSTANCE;
                    });

        }
    }


    ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (ActivityResult result) -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri uri = data.getData();
                        setImageUserWithUriGlide(uri);

                        fireStoreManager.savePhotoToStorageUri(uri);
                        if (notifyPhotoChangedCallBack != null) {
                            notifyPhotoChangedCallBack.notifyPhotoUserChangedCallBack();
                        }

                    }
                } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
                    ToastUtils.getInstance().ToastMsg(getString(R.string.take_photo_again));
                }
            });

    private void setImageUserWithUriGlide(Uri uri) {

        Glide.with(this)
                .load(uri)
                .centerCrop()
                .into(binding.imageUser);
    }


    @Override
    public void imageUriCallBack(String imageUri) {
        fireStoreManager.updateUserPhotoInFireStore(imageUri);
        Log.d("imageUriCallBack", "imageUriCallBack: " + imageUri);


    }


    @Override
    public void currentUserImageCallBack(String photo) {
        Glide.with(this)
                .load(photo)
                .centerCrop()
                .into(binding.imageUser);
    }

    NavigationView.OnNavigationItemSelectedListener navigationItemSelected = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            int id = item.getItemId();
            if (id == R.id.settings) {
                if (openSettingsCallBack != null) {
                    openSettingsCallBack.openSettingFragmentCallBack();
                }
            } else if (id == R.id.logOut) {
                if (openFirstActivityCallBack != null) {
                    openFirstActivityCallBack.openFirstActivityCallBack();
                }
            }


            return false;
        }
    };

    public void initOpenSettingsCallBack(OpenSettingsCallBack openSettingsCallBack) {
        this.openSettingsCallBack = openSettingsCallBack;
    }


    public void initOpenFirstActivityCallBack(OpenFirstActivityCallBack openFirstActivityCallBack) {
        this.openFirstActivityCallBack = openFirstActivityCallBack;

    }

    public void initNotifyPhotoChangedCallBack(NotifyPhotoChangedCallBack notifyPhotoChangedCallBack) {
        this.notifyPhotoChangedCallBack = notifyPhotoChangedCallBack;
    }
}