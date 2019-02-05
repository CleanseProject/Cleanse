package com.cleanseproject.cleanse.services;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class CleanseFirebaseMessagingService extends FirebaseMessagingService {


    public static final String NOTIFICATION = "com.cleanseproject.cleanse.NOTIFICATION";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra("mensaje", remoteMessage.getNotification().getTitle());
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(intent);
    }

}
