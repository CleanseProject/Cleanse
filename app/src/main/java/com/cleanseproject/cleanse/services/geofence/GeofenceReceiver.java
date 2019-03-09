package com.cleanseproject.cleanse.services.geofence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.cleanseproject.cleanse.R;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeofenceReceiver extends BroadcastReceiver {
    Context context;

    Intent broadcastIntent = new Intent();

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        broadcastIntent.addCategory("geofenceCategory");
        Log.d("geofencereceiver", "called");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            handleError(intent);
        } else {
            handleEnterExit(intent);
        }
    }


    private void handleError(Intent intent) {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        // Get the error code
        int errorCode = geofencingEvent.getErrorCode();

        // Get the error message
        String errorMessage = GeofenceErrorMessages.getErrorString(context,
                geofencingEvent.getErrorCode());

        // Log the error
        Log.e("apptag",
                context.getString(R.string.unknown_geofence_error) +
                        errorMessage);

        // Set the action and error message for the broadcast intent
        broadcastIntent
                .setAction("geofence_error")
                .putExtra("EXTRA_GEOFENCE_STATUS", errorMessage);

        // Broadcast the error *locally* to other components in this app
        LocalBroadcastManager.getInstance(context).sendBroadcast(
                broadcastIntent);
    }


    private void handleEnterExit(Intent intent) {
        // Get the type of transition (entry or exit)

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        int transition = geofencingEvent.getGeofenceTransition();

        // Test that a valid transition was reported
        if ((transition == Geofence.GEOFENCE_TRANSITION_ENTER)
                || (transition == Geofence.GEOFENCE_TRANSITION_EXIT)) {

            // Post a notification
            List<Geofence> geofences = geofencingEvent.getTriggeringGeofences();
            String[] geofenceIds = new String[geofences.size()];
            String ids = TextUtils.join("GEOFENCE_ID_DELIMITER",
                    geofenceIds);
            String transitionType = getTransitionString(transition);

            for (int index = 0; index < geofences.size(); index++) {
                Geofence geofence = geofences.get(index);

            }
            // Create an Intent to broadcast to the app
            broadcastIntent
                    .setAction("ACTION_GEOFENCE_TRANSITION")
                    .addCategory("CATEGORY_LOCATION_SERVICES")
                    .putExtra("EXTRA_GEOFENCE_ID", geofenceIds)
                    .putExtra("transition_type",
                            transitionType);

            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    transition,
                    triggeringGeofences
            );

            // Send notification and log the transition details.
            sendNotification("Geofence", geofenceTransitionDetails);
            LocalBroadcastManager.getInstance(context)
                    .sendBroadcast(broadcastIntent);

            // Log the transition type and a message
            Log.d("APPTAG", transitionType + ": " + ids);
            Log.d("APPTAG",
                    "transition_notification_text");

            // In debug mode, log the result
            Log.d("APPTAG", "transition");

            // An invalid transition was reported
        } else {
            // Always log as an error
            Log.e("APPTAG",
                    context.getString(R.string.geofence_transition_invalid_type) +
                            context.getString(transition));
        }
    }

    private void sendNotification(String title, final String message) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference notifications = ref.child("notificationRequests");
        Map<String, String> notification = new HashMap<>();
        notification.put("username", FirebaseAuth.getInstance().getCurrentUser().getUid());
        notification.put("title", title);
        notification.put("message", message);
        notification.put("chatuid", "0");
        notifications.push().setValue(notification);
    }

    private String getGeofenceTransitionDetails(
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return context.getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return context.getString(R.string.geofence_transition_exited);
            default:
                return context.getString(R.string.unknown_geofence_transition);
        }
    }

}

