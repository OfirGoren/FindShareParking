package com.example.FindShareParking.Objects;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.FindShareParking.CallBacks.LocationsCallBack;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

public class MyFusedLocation {


    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    LocationsCallBack locationsCallBack;

    public MyFusedLocation(Context context) {
        this.context = context;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

    }

    public void initLocationCallBack(LocationsCallBack locationsCallBack) {
        this.locationsCallBack = locationsCallBack;

    }


    public void ActivateGetLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        if (locationsCallBack != null) {
                            locationsCallBack.getCurrentLocationCallback(location);
                        }
                    } else {
                        checkSettingAndStartLocation();
                    }
                });
    }

    private void checkSettingAndStartLocation() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest request = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build();

        SettingsClient client = LocationServices.getSettingsClient(context);
        Task<LocationSettingsResponse> locationSettingsRequestTask = client.checkLocationSettings(request);
        locationSettingsRequestTask.addOnSuccessListener(locationSettingsResponse -> startLocationUpdate());

    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdate() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationsCallBackRequest, Looper.getMainLooper());

    }

    private final LocationCallback locationsCallBackRequest = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            locationsCallBack.getCurrentLocationCallback(locationResult.getLastLocation());
            Log.e("onLocationResult", "onLocationResult: ");
            stopLocationUpdates();
        }
    };

    public void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationsCallBackRequest);
    }
}
