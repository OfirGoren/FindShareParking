package com.example.FindShareParking.Objects;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapManagerRecyclerView implements OnMapReadyCallback {
    private final double latitude;
    private final double longitude;
    private static final int ZOOM = 18;


    public MapManagerRecyclerView(MapView mapView, String latitude, String longitude) {
        this.latitude = Double.parseDouble(latitude);
        this.longitude = Double.parseDouble(longitude);
        mapView.onCreate(null);
        mapView.getMapAsync(this);
        mapView.onResume();
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng sydney = new LatLng(this.latitude, this.longitude);
        googleMap.addMarker(new MarkerOptions()
                .position(sydney));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, ZOOM));
        googleMap.getUiSettings().setScrollGesturesEnabled(false);

    }


}

