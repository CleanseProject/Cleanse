package com.cleanseproject.cleanse.fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.activities.AddEventActivity;
import com.cleanseproject.cleanse.activities.EventDetailsActivity;
import com.cleanseproject.cleanse.dataClasses.Event;
import com.cleanseproject.cleanse.fragments.mapFragment.CleanseMapFragment;
import com.cleanseproject.cleanse.services.EventManagerService;
import com.cleanseproject.cleanse.services.LocationService;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import java.util.HashMap;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EventManagerService eventManagerService;
    private LocationService locationService;
    private boolean followUser;
    private Marker selectedMarker;
    private HashMap<Marker, String> markers;

    private Location lastLoaded;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
        markers = new HashMap<>();
        eventManagerService = new EventManagerService();
        locationService = new LocationService(getContext());
        CleanseMapFragment mapFragment = (CleanseMapFragment) getChildFragmentManager().findFragmentById(R.id.map);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(this);
        mapFragment.setOnDragListener(motionEvent -> {
            LatLng latLng = mMap.getProjection().getVisibleRegion().latLngBounds.getCenter();
            Location location = new Location("");
            location.setLatitude(latLng.latitude);
            location.setLongitude(latLng.longitude);
            if (lastLoaded == null || (lastLoaded.distanceTo(location) / 1000) > getMapVisibleRadiusKm()) {
                eventManagerService.getCloseEvents(
                        new GeoLocation(latLng.latitude, latLng.longitude),
                        getMapVisibleRadiusKm(),
                        this::addEventToMap);
                lastLoaded = location;
            }
        });
    }

    private double getMapVisibleRadiusKm() {
        VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
        LatLng farRight = visibleRegion.farRight;
        LatLng farLeft = visibleRegion.farLeft;
        LatLng nearRight = visibleRegion.nearRight;
        LatLng nearLeft = visibleRegion.nearLeft;
        float[] distanceWidth = new float[2];
        Location.distanceBetween(
                (farRight.latitude + nearRight.latitude) / 2,
                (farRight.longitude + nearRight.longitude) / 2,
                (farLeft.latitude + nearLeft.latitude) / 2,
                (farLeft.longitude + nearLeft.longitude) / 2,
                distanceWidth
        );
        float[] distanceHeight = new float[2];
        Location.distanceBetween(
                (farRight.latitude + nearRight.latitude) / 2,
                (farRight.longitude + nearRight.longitude) / 2,
                (farLeft.latitude + nearLeft.latitude) / 2,
                (farLeft.longitude + nearLeft.longitude) / 2,
                distanceHeight
        );
        float distance;
        if (distanceWidth[0] > distanceHeight[0]) {
            distance = distanceWidth[0];
        } else {
            distance = distanceHeight[0];
        }
        return distance / 1000;
    }

    public void changeMapType() {
        if (mMap != null) {
            if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            } else if (mMap.getMapType() == GoogleMap.MAP_TYPE_HYBRID) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (checkPermission()) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.setOnMyLocationButtonClickListener(() -> {
                followUser = true;
                return false;
            });
            Location currentLocation = locationService.getCurrentLocation();
            lastLoaded = currentLocation;
            locationService.setLocationListener(location -> {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                if (followUser)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11.0f));
            });
        }
        eventManagerService.getCloseEvents(
                new GeoLocation(0, 0),
                getMapVisibleRadiusKm(),
                this::addEventToMap);
        mMap.setOnMapClickListener(latLng -> {
            if (selectedMarker != null) {
                selectedMarker.remove();
                selectedMarker = null;
            } else {
                selectedMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(bitmapDescriptorFromVector(getContext(), R.drawable.marcadorlimpio_vector))
                        .title(getString(R.string.add_event))
                        .snippet("Haz click para agregar este punto"));
                selectedMarker.showInfoWindow();
            }
        });
        mMap.setOnInfoWindowClickListener(marker -> {
            if (selectedMarker != null) {
                LatLng latLng = marker.getPosition();
                double lat = latLng.latitude;
                double lon = latLng.longitude;
                if (getActivity().getClass() == AddEventActivity.class) {
                    AddEventActivity addEventActivity = (AddEventActivity) getActivity();
                    Button btnLocalizacion = addEventActivity.findViewById(R.id.btn_set_location);
                    if (lat != 0 && lon != 0) {
                        addEventActivity.setEventLatLng(latLng);
                        btnLocalizacion.setText(locationService.localityName(lat, lon));
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
            } else {
                Intent intent = new Intent(getContext(), EventDetailsActivity.class);
                intent.putExtra("evento", markers.get(marker));
                startActivity(intent);
            }
        });
    }

    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void addEventToMap(Event event) {
        int markerType;
        switch (event.getState()) {
            case 1:
                markerType = R.drawable.marcadorsucio_vector;
                break;
            case 2:
                markerType = R.drawable.marcadorcritico_vector;
                break;
            default:
                markerType = R.drawable.marcadorlimpio_vector;
        }
        LatLng latLng = new LatLng(event.getLatitude(), event.getLongitude());
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(event.getName())
                .icon(bitmapDescriptorFromVector(getContext(), markerType)));
        markers.put(marker, event.getId());
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}
