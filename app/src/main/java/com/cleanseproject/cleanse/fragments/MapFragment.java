package com.cleanseproject.cleanse.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.dataClasses.Event;
import com.cleanseproject.cleanse.services.EventManagerService;
import com.cleanseproject.cleanse.services.LocationService;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EventManagerService eventManagerService;
    private LocationService locationService;

    private boolean followUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        View overlay = view.findViewById(R.id.map_overlay);
        followUser = true;
        overlay.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                v.performClick();
            }
            followUser = false;
            return false;
        });
        eventManagerService = new EventManagerService();
        locationService = new LocationService(getContext());
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (checkPermission())
            mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(() -> {
            followUser = true;
            return false;
        });
        locationService.setLocationListener(location -> {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if (followUser)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
        });
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnMapClickListener(latLng -> Log.v("Mensaje", latLng + ""));
        Location currentLocation = locationService.getCurrentLocation();
        eventManagerService.getCloseEvents(
                new GeoLocation(currentLocation.getLatitude(), currentLocation.getLongitude()),
                10,
                this::addEventToMap);
    }

    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void addEventToMap(Event event) {
        LatLng latLng = new LatLng(Double.parseDouble(event.getLatitude()), Double.parseDouble(event.getLongitude()));
        mMap.addMarker(new MarkerOptions().position(latLng).title(event.getName()));
    }

}
