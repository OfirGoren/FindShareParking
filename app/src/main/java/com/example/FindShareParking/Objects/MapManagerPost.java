package com.example.FindShareParking.Objects;

import android.content.Context;
import android.location.Location;

import androidx.annotation.NonNull;

import com.example.FindShareParking.CallBacks.LocationsCallBack;
import com.example.FindShareParking.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapManagerPost implements OnMapReadyCallback, LocationsCallBack {

    private static final int ZOOM = 18;

    private GoogleMap mMap;
    private final Context context;
    private Location location;
    SupportMapFragment mapFragment;

    public MapManagerPost(Context context, SupportMapFragment mapFragment) {
        this.mapFragment = mapFragment;
        this.context = context;

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        updateMapCurrentLocation();


    }

    public void activateMapWithCurrentLocation() {
        MyFusedLocation myFusedLocation = new MyFusedLocation(context);
        myFusedLocation.initLocationCallBack(this);
        myFusedLocation.ActivateGetLocation();


    }

    private void updateMapCurrentLocation() {


        if (mMap != null && location != null) {
            LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(sydney)
                    .title(context.getString(R.string.here)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, ZOOM));
        }
    }

    //get current location and activate map
    @Override
    public void getCurrentLocationCallback(Location location) {
        this.location = location;
        mapFragment.getMapAsync(this);

    }


    public Location getLocation() {
        return location;
    }


}









