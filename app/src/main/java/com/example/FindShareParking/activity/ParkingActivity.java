package com.example.FindShareParking.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.FindShareParking.CallBacks.DataChangedSettingCallBack;
import com.example.FindShareParking.CallBacks.NotifyPhotoChangedCallBack;
import com.example.FindShareParking.CallBacks.OpenFirstActivityCallBack;
import com.example.FindShareParking.CallBacks.OpenSettingsCallBack;
import com.example.FindShareParking.CallBacks.PostNotifyCallBack;
import com.example.FindShareParking.CallBacks.newPostCallBack;
import com.example.FindShareParking.Fragments.ParkingFragments.HomeParkingFragment;
import com.example.FindShareParking.Fragments.ParkingFragments.MoreFragment;
import com.example.FindShareParking.Fragments.ParkingFragments.MyPostFragment;
import com.example.FindShareParking.Fragments.ParkingFragments.NewPostFragment;
import com.example.FindShareParking.Fragments.ParkingFragments.NotifyFragment;
import com.example.FindShareParking.Fragments.ParkingFragments.SettingFragment;
import com.example.FindShareParking.Objects.Notify;
import com.example.FindShareParking.R;
import com.example.FindShareParking.Utils.CheckPermissionsUtils;
import com.example.FindShareParking.databinding.ActivityParkingBinding;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class ParkingActivity extends AppCompatActivity implements PostNotifyCallBack, OpenSettingsCallBack,
        OpenFirstActivityCallBack, DataChangedSettingCallBack, NotifyPhotoChangedCallBack, newPostCallBack {

    private static final String NEW_POST_TAG = "NEW_POST_TAG";
    private static final String SETTING_TAG = "SETTING_TAG";
    private static final int REQUEST_CODE = 22;
    private static final String HOME_PARKING_TAG = "1";
    private static final String MY_POST_TAG = "2";
    private static final String NOTIFY_TAG = "3";
    private static final String MORE_TAG = "4";


    private HomeParkingFragment homeParkingFragment;
    private MyPostFragment myPostFragment;
    private NewPostFragment newPostFragment;
    private NotifyFragment notifyFragment;
    private MoreFragment moreFragment;
    private SettingFragment settingFragment;
    private final FragmentManager fm = getSupportFragmentManager();
    private Fragment currentActive;
    private BadgeDrawable badge;
    private ActivityParkingBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityParkingBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);
        handleSavedInstanceState(savedInstanceState);
        initCallBacksBottomNav();
        currentActive = homeParkingFragment;

        initFragments();
        initCallBacks();


        listeners();
        initBadgeNotify();
        changeStatusBarColorToOrange();


    }

    private void handleSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            homeParkingFragment = (HomeParkingFragment) fm.findFragmentByTag(HOME_PARKING_TAG);
            myPostFragment = (MyPostFragment) fm.findFragmentByTag(MY_POST_TAG);
            notifyFragment = (NotifyFragment) fm.findFragmentByTag(NOTIFY_TAG);
            moreFragment = (MoreFragment) fm.findFragmentByTag(MORE_TAG);

        } else {
            initFragmentsBottomNav();
            beginTransactionFragments();

        }


    }

    private void initFragments() {
        newPostFragment = new NewPostFragment();
        settingFragment = new SettingFragment();
    }

    private void initCallBacksBottomNav() {
        homeParkingFragment.initNewPostCallBack(this);
        notifyFragment.initPostNotifyCallBack(this);
        moreFragment.initOpenFirstActivityCallBack(this);
        moreFragment.initNotifyPhotoChangedCallBack(this);
    }


    private void initCallBacks() {
        moreFragment.initOpenSettingsCallBack(this);
        settingFragment.initDataChangedCallBack(this);
        newPostFragment.initNewPOstFragmentCallBack(this);
        settingFragment.initDataChangedCallBack(this);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void beginTransactionFragments() {

        fm.beginTransaction().add(R.id.frameLayoutParking, myPostFragment, MY_POST_TAG).hide(myPostFragment).commit();
        fm.beginTransaction().add(R.id.frameLayoutParking, notifyFragment, NOTIFY_TAG).hide(notifyFragment).commit();
        fm.beginTransaction().add(R.id.frameLayoutParking, moreFragment, MORE_TAG).hide(moreFragment).commit();
        fm.beginTransaction().add(R.id.frameLayoutParking, homeParkingFragment, HOME_PARKING_TAG).
                addToBackStack(null).
                commit();

    }


    private void initBadgeNotify() {
        int menuItemId = binding.bottomNav.getMenu().getItem(2).getItemId();
        badge = binding.bottomNav.getOrCreateBadge(menuItemId);
        badge.setVisible(false);
        badge.setBackgroundColor(getResources().getColor(R.color.orange));
    }


    private void initFragmentsBottomNav() {
        homeParkingFragment = new HomeParkingFragment();
        myPostFragment = new MyPostFragment();
        notifyFragment = new NotifyFragment();
        moreFragment = new MoreFragment();


    }


    private boolean kindFragmentDisplay(String tag) {
        Fragment fragment = fm.findFragmentByTag(tag);
        return fragment != null && fragment.isVisible();
    }


    private void listeners() {
        binding.bottomNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }


    private void resetBadgeNotify() {
        if (this.badge != null) {
            this.badge.setNumber(0);
            this.badge.setVisible(false);
        }

    }

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();

            if (id == R.id.mainPage) {
                fm.beginTransaction().hide(currentActive).show(homeParkingFragment).commit();
                currentActive = homeParkingFragment;
                return true;
            } else if (id == R.id.myPostPage) {
                fm.beginTransaction().hide(currentActive).show(myPostFragment).commit();
                currentActive = myPostFragment;
                return true;
            } else if (id == R.id.notification) {
                fm.beginTransaction().hide(currentActive).show(notifyFragment).commit();
                currentActive = notifyFragment;
                resetBadgeNotify();
                return true;
            } else if (id == R.id.morePage) {
                fm.beginTransaction().hide(currentActive).show(moreFragment).commit();
                currentActive = moreFragment;
                return true;
            }
            return false;
        }

    };


    @Override
    public void onBackPressed() {

        int count = fm.getBackStackEntryCount();

        //when have one fragment in backStack
        if (count == 1) {
            finish();
        } else {
            if (kindFragmentDisplay(NEW_POST_TAG) || kindFragmentDisplay(SETTING_TAG)) {
                binding.bottomNav.setVisibility(View.VISIBLE);


            }
            super.onBackPressed();
        }
    }


    private void displayFragmentWithBackStack(Fragment displayFragment, Fragment hideFragment, String tag) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(
                R.anim.slide_in,  // enter
                R.anim.fade_out,  // exit
                R.anim.fade_in,   // popEnter
                R.anim.slide_out
        );// popExit;
        // if the fragment is already in container
        if (displayFragment.isAdded()) {
            ft.show(displayFragment);
            // fragment needs to be added to frame container
        } else {
            ft.add(R.id.frameLayoutParking, displayFragment, tag)
                    .addToBackStack(null);
        }
        // Hide fragment order
        Log.d("displayFragmentWithBa", "displayFragmentWithBackStack: " + hideFragment.isAdded());
        if (hideFragment.isAdded()) {
            ft.hide(hideFragment);
        }
        // Hide fragment
        ft.commit();

    }


    @Override
    public void openNewPostFragment() {

        if (CheckPermissionsUtils.getInstance().isAlreadyAccessLocation()) {
            hideBottomNav();
            displayNewPostFragment();
        } else {
            requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION);


        }
    }


    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    hideBottomNav();
                    displayNewPostFragment();
                } else {
                }
            });


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && requestCode == REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            displayNewPostFragment();
            hideBottomNav();
        }

    }


    private void hideBottomNav() {
        binding.bottomNav.setVisibility(View.GONE);
    }

    private void displayNewPostFragment() {

        displayFragmentWithBackStack(newPostFragment, homeParkingFragment, NEW_POST_TAG);


    }

    @Override
    public void newLike(Notify notify) {
        notifyFragment.newLikeOnPosts(notify);
    }

    @Override
    public void deleteNotify(Notify notify) {
        notifyFragment.deleteNotify(notify);

    }

    @Override
    public void increaseBadgeCallBack(int size) {
        if (this.badge == null) {
            initBadgeNotify();
        }
        badge.setVisible(true);
        badge.setNumber(badge.getNumber() + size);


    }

    @Override
    public void decreaseBadgeCallBack(int size) {
        if (this.badge == null) {
            initBadgeNotify();
        }
        badge.setVisible(true);
        badge.setNumber(badge.getNumber() - size);
        if (badge.getNumber() == 0) {
            badge.setVisible(false);
        }

    }


    @Override
    public void openSettingFragmentCallBack() {
        displayFragmentWithBackStack(settingFragment, currentActive, SETTING_TAG);
        binding.bottomNav.setVisibility(View.GONE);


    }

    @Override
    public void openFirstActivityCallBack() {
        openFirstActivity();

    }

    private void openFirstActivity() {
        Intent intent = new Intent(this, FirstActivity.class);
        ParkingActivity.this.startActivity(intent);
        FirebaseAuth.getInstance().signOut();

        finish();
    }

    @Override
    public void dataChangedCallBack() {
        homeParkingFragment.activateDataChangedRecyclerView();
        myPostFragment.activateDataChangedRecyclerView();

    }

    @Override
    public void showOnlyPostAccordingTypeOfParking() {
        homeParkingFragment.showOnlyPostsAccordingKindOfParking();
    }

    @Override
    public void notifyPhotoUserChangedCallBack() {
        homeParkingFragment.activateDataChangedRecyclerView();

    }

    @Override
    public void closeNewPostFragmentCallBack() {
        onBackPressed();
    }

    @Override
    public void activateLinearProgressBar() {
        homeParkingFragment.turnOnLinearProgressBar();
    }

    private void changeStatusBarColorToOrange() {
        Window window = this.getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.orange));
    }


}
