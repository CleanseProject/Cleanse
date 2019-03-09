package com.cleanseproject.cleanse.services.geofence;


import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.cleanseproject.cleanse.dataClasses.Event;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class GeofenceManager {

    private Context context;
    private ArrayList<Geofence> geofences;
    private GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;

    public GeofenceManager(Context context) {
        this.context = context;
        geofences = new ArrayList<>();
        geofencingClient = LocationServices.getGeofencingClient(context);
    }

    public void addEventGeofence(Event event) {
        if (geofences.size() < 100) {
            geofences.add(new Geofence.Builder()
                    .setRequestId(event.getId())
                    .setCircularRegion(
                            event.getLatitude(),
                            event.getLongitude(),
                            100000
                    )
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .build());
        }

    }

    public void addEventGeofences() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofences);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        geofencingClient.addGeofences(builder.build(), getGeofencePendingIntent())
                .addOnSuccessListener((Activity) context, aVoid -> Log.d("geofenceAdd", "success"))
                .addOnFailureListener((Activity) context, Throwable::printStackTrace);
    }

    public void removeGeofences() {
        geofencingClient.removeGeofences(getGeofencePendingIntent())
                .addOnSuccessListener((Activity) context, aVoid -> {
                    // Geofences removed
                    // ...
                })
                .addOnFailureListener((Activity) context, e -> {
                    // Failed to remove geofences
                    // ...
                });
    }

    private PendingIntent getGeofencePendingIntent() {
        if (null != geofencePendingIntent) {

            // Return the existing intent
            return geofencePendingIntent;

            // If no PendingIntent exists
        } else {

            // Create an Intent pointing to the IntentService
            Intent intent = new Intent(context, GeofenceReceiver.class);
            //            Intent intent = new Intent(context, ReceiveTransitionsIntentService.class);
            /*
             * Return a PendingIntent to start the IntentService.
             * Always create a PendingIntent sent to Location Services
             * with FLAG_UPDATE_CURRENT, so that sending the PendingIntent
             * again updates the original. Otherwise, Location Services
             * can't match the PendingIntent to requests made with it.
             */
            return PendingIntent.getBroadcast(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

}
