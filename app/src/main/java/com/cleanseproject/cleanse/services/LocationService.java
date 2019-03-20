package com.cleanseproject.cleanse.services;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.callbacks.LocationUpdatesCallback;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Manages location requests through the app
 *
 * @author Cleanse Project
 */
public class LocationService {

    private final Context context;
    private LocationManager locationManager;
    private final Geocoder geocoder;

    /**
     * Class constructor, creates necessary services
     *
     * @param context Current activity
     */
    public LocationService(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        geocoder = new Geocoder(context, Locale.getDefault());
        requestLocationUpdate();
    }

    /**
     * @return True if the permission is granted by user and is enabled
     */
    public boolean checkPermission() {
        if (!isLocationEnabled())
            return false;
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * @return True if location is turned on
     */
    private boolean isLocationEnabled() {
        int locationMode = 0;
        try {
            locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return locationMode != Settings.Secure.LOCATION_MODE_OFF;
    }

    /**
     * Asks for current location
     */
    private void requestLocationUpdate() {
        if (checkPermission())
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
    }

    /**
     * @return Last known location
     */
    public Location getCurrentLocation() {
        locationManager = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            checkPermission();
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    /**
     * @param callback Called when location is changed
     */
    public void setLocationListener(LocationUpdatesCallback callback) {
        if (checkPermission()) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    callback.locationUpdate(location);
                    locationManager.removeUpdates(this);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }
    }

    /**
     * @param eventLocation Location of the event
     * @return Dstance in meters from current location to event
     */
    public float distance(Location eventLocation) {
        if (checkPermission()) {
            Location currentLocation = getCurrentLocation();
            if (currentLocation != null)
                return currentLocation.distanceTo(eventLocation);
        }
        return -1;
    }

    /**
     * @param lat Location latitude
     * @param lng Location longitude
     * @return Name of the location locality, if null name of the country
     */
    public String localityName(double lat, double lng) {
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null && addresses.size() > 0) {
                String localityName = addresses.get(0).getLocality();
                String country = addresses.get(0).getCountryName();
                return localityName != null ? localityName : country;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return context.getString(R.string.unknown);
    }

}
