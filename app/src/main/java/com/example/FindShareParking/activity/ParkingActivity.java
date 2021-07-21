package com.example.FindShareParking.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
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
    private static final String CURRENT_FRAGMENT_KEY = "CURRENT_FRAGMENT_KEY";


    private HomeParkingFragment homeParkingFragment;
    private MyPostFragment myPostFragment;
    private NewPostFragment newPostFragment;
    private NotifyFragment notifyFragment;
    private MoreFragment moreFragment;
    private SettingFragment settingFragment;
    private FragmentManager fm;
    private Fragment currentActiveFragment;
    private BadgeDrawable badge;
    private ActivityParkingBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityParkingBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        fm = getSupportFragmentManager();

        setContentView(view);
        handleSavedInstanceState(savedInstanceState);
        initCallBacks();
        listeners();
        initBadgeNotify();
        changeStatusBarColorToOrange();


    }

    private void handleSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            initFindFragmentsByTag();
            initCurrentFragmentValueBeforeAppProcessKilled(savedInstanceState);
            initSettingAndNewPostIfNull();

        } else {
            initFragmentsObjects();
            beginTransactionFragments();
            currentActiveFragment = homeParkingFragment;
        }

    }


    private void initFindFragmentsByTag() {
        homeParkingFragment = (HomeParkingFragment) fm.findFragmentByTag(HOME_PARKING_TAG);
        myPostFragment = (MyPostFragment) fm.findFragmentByTag(MY_POST_TAG);
        notifyFragment = (NotifyFragment) fm.findFragmentByTag(NOTIFY_TAG);
        moreFragment = (MoreFragment) fm.findFragmentByTag(MORE_TAG);
        settingFragment = (SettingFragment) fm.findFragmentByTag(SETTING_TAG);
        newPostFragment = (NewPostFragment) fm.findFragmentByTag(NEW_POST_TAG);
    }

    private void initSettingAndNewPostIfNull() {
        if (settingFragment == null) {
            settingFragment = new SettingFragment();
        }
        if (newPostFragment == null) {
            newPostFragment = new NewPostFragment();
        }
    }

    private void initCurrentFragmentValueBeforeAppProcessKilled(Bundle savedInstanceState) {
        String numTag = savedInstanceState.getString(CURRENT_FRAGMENT_KEY);
        switch (numTag) {
            case HOME_PARKING_TAG:
                currentActiveFragment = homeParkingFragment;
                break;
            case MY_POST_TAG:
                currentActiveFragment = myPostFragment;
                break;
            case NOTIFY_TAG:
                currentActiveFragment = notifyFragment;
                break;
            case MORE_TAG:
                currentActiveFragment = moreFragment;
                break;
            case NEW_POST_TAG:
                currentActiveFragment = newPostFragment;
                break;
            case SETTING_TAG:
                currentActiveFragment = settingFragment;
                break;
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString(CURRENT_FRAGMENT_KEY, HOME_PARKING_TAG);
    }


    private void initCallBacks() {
        homeParkingFragment.initNewPostCallBack(this);
        notifyFragment.initPostNotifyCallBack(this);
        moreFragment.initOpenFirstActivityCallBack(this);
        moreFragment.initNotifyPhotoChangedCallBack(this);
        moreFragment.initOpenSettingsCallBack(this);
        settingFragment.initDataChangedCallBack(this);
        newPostFragment.initNewPOstFragmentCallBack(this);
        settingFragment.initDataChangedCallBack(this);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saveTagCurrentFragmentDisplay(outState);

    }

    /*
     save the tag of current fragment display
     */

    private void saveTagCurrentFragmentDisplay(Bundle outState) {
        if (currentActiveFragment instanceof HomeParkingFragment) {
            outState.putString(CURRENT_FRAGMENT_KEY, HOME_PARKING_TAG);

        } else if (currentActiveFragment instanceof MyPostFragment) {
            outState.putString(CURRENT_FRAGMENT_KEY, MY_POST_TAG);

        } else if (currentActiveFragment instanceof NotifyFragment) {
            outState.putString(CURRENT_FRAGMENT_KEY, NOTIFY_TAG);

        } else if (currentActiveFragment instanceof MoreFragment) {
            outState.putString(CURRENT_FRAGMENT_KEY, MORE_TAG);

        } else if (currentActiveFragment instanceof NewPostFragment) {
            outState.putString(CURRENT_FRAGMENT_KEY, NEW_POST_TAG);

        } else if (currentActiveFragment instanceof SettingFragment) {
            outState.putString(CURRENT_FRAGMENT_KEY, SETTING_TAG);
        }
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


    private void initFragmentsObjects() {
        homeParkingFragment = new HomeParkingFragment();
        myPostFragment = new MyPostFragment();
        notifyFragment = new NotifyFragment();
        moreFragment = new MoreFragment();
        settingFragment = new SettingFragment();
        newPostFragment = new NewPostFragment();


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
                fm.beginTransaction().hide(currentActiveFragment).show(homeParkingFragment).commit();
                currentActiveFragment = homeParkingFragment;
                return true;
            } else if (id == R.id.myPostPage) {
                fm.beginTransaction().hide(currentActiveFragment).show(myPostFragment).commit();
                currentActiveFragment = myPostFragment;
                return true;
            } else if (id == R.id.notification) {
                fm.beginTransaction().hide(currentActiveFragment).show(notifyFragment).commit();
                currentActiveFragment = notifyFragment;
                resetBadgeNotify();
                return true;
            } else if (id == R.id.morePage) {
                fm.beginTransaction().hide(currentActiveFragment).show(moreFragment).commit();
                currentActiveFragment = moreFragment;
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
            handleCurrentFragmentNewPostOrSetting();
            super.onBackPressed();
        }
    }

    private void handleCurrentFragmentNewPostOrSetting() {

        if (kindFragmentDisplay(NEW_POST_TAG)) {
            binding.bottomNav.setVisibility(View.VISIBLE);
            //return to home parking fragment
            currentActiveFragment = homeParkingFragment;

        } else if (kindFragmentDisplay(SETTING_TAG)) {
            binding.bottomNav.setVisibility(View.VISIBLE);
            //return to more fragment
            currentActiveFragment = moreFragment;

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
        displayFragmentWithBackStack(newPostFragment, currentActiveFragment, NEW_POST_TAG);
        currentActiveFragment = newPostFragment;


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
        displayFragmentWithBackStack(settingFragment, currentActiveFragment, SETTING_TAG);
        binding.bottomNav.setVisibility(View.GONE);
        currentActiveFragment = settingFragment;


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
