package com.example.FindShareParking.Fragments.FirstFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.FindShareParking.CallBacks.LogInRegisterCallBack;
import com.example.FindShareParking.CallBacks.OpenParkingActivityCallBack;
import com.example.FindShareParking.databinding.FragmentWelcomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeFragment extends Fragment {

    private OpenParkingActivityCallBack openParkingActivityCallBack;
    private FirebaseAuth mAuth;
    private LogInRegisterCallBack LogInRegisterCallBack;
    private FragmentWelcomeBinding binding;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();



        initListeners();
        initValues();
        //if the current user already connected log in
        currentUserAlreadyConnected();

        return view;
    }

    private void initValues() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void initListeners() {
        binding.mainLoginInBtn.setOnClickListener(logInPress);
        binding.registerPress.setOnClickListener(RegisterPress);
    }


    private void currentUserAlreadyConnected() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && openParkingActivityCallBack != null) {
            openParkingActivityCallBack.newAccountRegisterCallBack();
        }
    }


    View.OnClickListener logInPress = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (LogInRegisterCallBack != null) {
                //show LogIn Fragment (activate in main activity)
                LogInRegisterCallBack.logInPressBtnCallBack();
            }

        }
    };
    View.OnClickListener RegisterPress = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (LogInRegisterCallBack != null) {
                //show Register Fragment (activate in main activity)
                LogInRegisterCallBack.registerPressCallBack();
            }
        }
    };


    public void initLogInRegisterCallBack(LogInRegisterCallBack logInRegisterCallBack) {
        this.LogInRegisterCallBack = logInRegisterCallBack;
    }

    public void initOpenParkingActivityCallBack(OpenParkingActivityCallBack openParkingActivityCallBack) {
        this.openParkingActivityCallBack = openParkingActivityCallBack;

    }
}
