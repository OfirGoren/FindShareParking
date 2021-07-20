package com.example.FindShareParking;

import android.app.Application;

import com.example.FindShareParking.Utils.CheckPermissionsUtils;
import com.example.FindShareParking.Utils.SharedPrefUtils;
import com.example.FindShareParking.Utils.ToastUtils;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPrefUtils.init(this);
        CheckPermissionsUtils.init(this);
        ToastUtils.init(this);
        
    }
}
