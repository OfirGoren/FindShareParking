package com.example.FindShareParking.Fragments.ParkingFragments;


import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.FindShareParking.CallBacks.DataChangedSettingCallBack;
import com.example.FindShareParking.CallBacks.GetCurrentUserDetailsCallBack;
import com.example.FindShareParking.Objects.FireStoreManager;
import com.example.FindShareParking.Objects.MySharedPref;
import com.example.FindShareParking.Objects.User;
import com.example.FindShareParking.R;


public class SettingFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener, GetCurrentUserDetailsCallBack {


    private DataChangedSettingCallBack dataChangedSettingCallBack;
    private FireStoreManager fireStoreManager;
    private EditTextPreference userName;
    private Preference myEmail;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.main_preferences, rootKey);


        initValues();
        initObjects();
        initCallBacks();
        getCurrentUserDetails();


    }

    private void getCurrentUserDetails() {
        fireStoreManager.getCurrentUser();

    }

    private void initCallBacks() {
        fireStoreManager.initGetCurrentUserDetailsCallBack(this);
    }

    private void initObjects() {
        fireStoreManager = new FireStoreManager();
    }

    private void initValues() {
        userName = findPreference(MySharedPref.USER_NAME_KEY);
        myEmail = findPreference(MySharedPref.MY_EMAIL_KEY);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(MySharedPref.USER_NAME_KEY)) {
            FireStoreManager.updateNameInFireStore(sharedPreferences.getString(key, ""));


        } else if (key.equals(MySharedPref.TYPE_OF_PARKING_KEY)) {
            Preference pref = findPreference(key);
            if (pref != null) {
                dataChangedSettingCallBack.showOnlyPostAccordingTypeOfParking();
            }

        }

        if (dataChangedSettingCallBack != null) {
            dataChangedSettingCallBack.dataChangedCallBack();

        }
    }


    @Override
    public void getCurrentUserCallBack(User user) {
        setUserNameInSettings(user.getName());
        setEmailInSettings(user.getEmail());
    }

    private void setEmailInSettings(String email) {
        if (myEmail != null) {
            myEmail.setSummary(email);
        }

    }

    private void setUserNameInSettings(String name) {
        if (userName != null) {
            userName.setText(name);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    public void initDataChangedCallBack(DataChangedSettingCallBack dataChangedSettingCallBack) {
        this.dataChangedSettingCallBack = dataChangedSettingCallBack;
    }


}
