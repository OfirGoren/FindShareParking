package com.example.FindShareParking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

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
import com.example.FindShareParking.databinding.ActivityParkingBinding;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class ParkingActivity extends AppCompatActivity implements PostNotifyCallBack, OpenSettingsCallBack,
        OpenFirstActivityCallBack, DataChangedSettingCallBack, NotifyPhotoChangedCallBack, newPostCallBack {

    private static final String NEW_POST_TAG = "NEW_POST_TAG";
    private static final String SETTING_TAG = "SETTING_TAG";
    private static final int REQUEST_CODE = 22;

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

        initFragments();
        initCallBacks();
        //current Fragment Display
        currentActive = homeParkingFragment;
        beginTransactionFragments();
        initCallBack();
        listeners();
        initBadgeNotify();
        changeStatusBarColorToOrange();

    }

    private void beginTransactionFragments() {
        fm.beginTransaction().add(R.id.frameLayoutParking, myPostFragment, "2").hide(myPostFragment).commit();
        fm.beginTransaction().add(R.id.frameLayoutParking, notifyFragment, "3").hide(notifyFragment).commit();
        fm.beginTransaction().add(R.id.frameLayoutParking, moreFragment, "4").hide(moreFragment).commit();
        fm.beginTransaction().add(R.id.frameLayoutParking, homeParkingFragment, "1").
                addToBackStack(null).
                commit();
    }


    private void initCallBack() {
        moreFragment.initOpenSettingsCallBack(this);
        settingFragment.initDataChangedCallBack(this);
    }

    private void initBadgeNotify() {
        int menuItemId = binding.bottomNav.getMenu().getItem(2).getItemId();
        badge = binding.bottomNav.getOrCreateBadge(menuItemId);
        badge.setVisible(false);
        badge.setBackgroundColor(getResources().getColor(R.color.orange));
    }


    private void initCallBacks() {
        homeParkingFragment.initNewPostCallBack(this);
        notifyFragment.initPostNotifyCallBack(this);
        moreFragment.initOpenFirstActivityCallBack(this);
        settingFragment.initDataChangedCallBack(this);
        moreFragment.initNotifyPhotoChangedCallBack(this);
        newPostFragment.initNewPOstFragmentCallBack(this);

    }

    private void initFragments() {
        homeParkingFragment = new HomeParkingFragment();
        myPostFragment = new MyPostFragment();
        newPostFragment = new NewPostFragment();
        notifyFragment = new NotifyFragment();
        moreFragment = new MoreFragment();
        settingFragment = new SettingFragment();

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
            ft.replace(R.id.frameLayoutParking, displayFragment, tag)
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
        displayNewPostFragment();
        hideBottomNav();

    }

    private void hideBottomNav() {
        binding.bottomNav.setVisibility(View.GONE);
    }

    private void displayNewPostFragment() {
        displayFragmentWithBackStack(newPostFragment, currentActive, NEW_POST_TAG);

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
