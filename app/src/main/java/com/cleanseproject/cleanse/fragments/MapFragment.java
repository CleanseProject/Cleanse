package com.cleanseproject.cleanse.fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.FrameLayout;

import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.activities.AddEventActivity;
import com.cleanseproject.cleanse.dataClasses.Event;
import com.cleanseproject.cleanse.services.EventManagerService;
import com.cleanseproject.cleanse.services.LocationService;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EventManagerService eventManagerService;
    private LocationService locationService;
    private boolean followUser;
    private double latitud;
    private double longitud;
    private Marker selectedMarker;


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
        mMap.setOnMapClickListener(latLng -> {
            if (selectedMarker != null) {
                selectedMarker.remove();
                selectedMarker = null;
            } else {
                selectedMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(getString(R.string.add_event))
                        .snippet("Haz click para agregar este punto"));
                selectedMarker.showInfoWindow();
                latitud = latLng.latitude;
                longitud = latLng.longitude;
                mMap.setOnInfoWindowClickListener(marker -> {
                    double lat = latLng.latitude;
                    double lon = latLng.longitude;
                    if (getActivity().getClass() == AddEventActivity.class) {
                        AddEventActivity addEventActivity = (AddEventActivity) getActivity();
                        Button btnLocalizacion = addEventActivity.findViewById(R.id.btn_set_location);
                        if (lat != 0 && lon != 0) {
                            addEventActivity.setEventLatLng(latLng);
                            btnLocalizacion.setText("Lat/Lon: " + lat + "/" + lon);
                        }
                        FrameLayout frameLayout = addEventActivity.findViewById(R.id.FrameLayout_add_event);
                        frameLayout.setVisibility(View.GONE);
                        addEventActivity.setFrameAbierto(false);
                    } else {
                        Intent i = new Intent(getContext(), AddEventActivity.class);
                        i.putExtra("latitude", lat);
                        i.putExtra("longitude", lon);
                        startActivity(i);
                    }
                });
            }
        });

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
        LatLng latLng = new LatLng(event.getLatitude(), event.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng).title(event.getName()));
    }

}
