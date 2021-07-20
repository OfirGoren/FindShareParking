package com.example.FindShareParking.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.FindShareParking.CallBacks.LogInRegisterCallBack;
import com.example.FindShareParking.CallBacks.OpenParkingActivityCallBack;
import com.example.FindShareParking.Fragments.FirstFragments.LoginFragment;
import com.example.FindShareParking.Fragments.FirstFragments.RegisterFragment;
import com.example.FindShareParking.Fragments.FirstFragments.WelcomeFragment;
import com.example.FindShareParking.R;

public class FirstActivity extends AppCompatActivity implements LogInRegisterCallBack, OpenParkingActivityCallBack {

    private LoginFragment loginFragment;
    private WelcomeFragment welcomeFragment;
    private RegisterFragment registerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        findViewsId();
        initFragmentClasses();
        initCallBacks();

        displayFragment(welcomeFragment);


    }

    private void initCallBacks() {
        welcomeFragment.initLogInRegisterCallBack(this);
        registerFragment.initNewAccountCallBack(this);
        welcomeFragment.initOpenParkingActivityCallBack(this);
        loginFragment.initOpenParkingActivityCallBack(this);
    }

    private void initFragmentClasses() {
        loginFragment = new LoginFragment();
        welcomeFragment = new WelcomeFragment();
        registerFragment = new RegisterFragment();

    }

    private void displayFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().setCustomAnimations(
                R.anim.slide_in,  // enter
                R.anim.fade_out,  // exit
                R.anim.fade_in,   // popEnter
                R.anim.slide_out  // popExit
        )
                .replace(R.id.frameLayout, fragment, null)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();
        //when have one fragment in backStack
        if (count == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    private void findViewsId() {

    }

    @Override
    public void logInPressBtnCallBack() {
        displayFragment(loginFragment);
    }

    @Override
    public void registerPressCallBack() {
        displayFragment(registerFragment);

    }

    @Override
    public void newAccountRegisterCallBack() {
        Intent intent = new Intent(FirstActivity.this, ParkingActivity.class);
        startActivity(intent);
        finish();
    }
}