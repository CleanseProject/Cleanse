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
        String notificationTitle = remoteMessage.getNotification().getTitle();
        if (notificationTitle != null)
            intent.putExtra("title", notificationTitle);
        intent.putExtra("body", remoteMessage.getNotification().getBody());
        String chatId = remoteMessage.getData().get("chatuid");
        String notificationId = remoteMessage.getData().get("notificationid");
        intent.putExtra("chatuid", chatId);
        intent.putExtra("notificationid", notificationId);
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(intent);
    }

}
