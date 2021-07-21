package com.example.FindShareParking.Utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

public class CheckPermissionsUtils {

    private final Context context;
    @SuppressLint("StaticFieldLeak")
    private static CheckPermissionsUtils instance;


    private CheckPermissionsUtils(Context context) {
        this.context = context;

    }

    public static CheckPermissionsUtils getInstance() {
        return instance;
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new CheckPermissionsUtils(context);
        }
    }

    public Boolean isAlreadyAccessLocation() {
        return ContextCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                ;

    }
}