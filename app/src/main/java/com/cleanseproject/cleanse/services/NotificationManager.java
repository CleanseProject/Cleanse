package com.cleanseproject.cleanse.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.activities.ChatActivity;
import com.tapadoo.alerter.Alerter;

import java.util.HashSet;

public class NotificationManager {

    private final Context context;
    private final HashSet<String> shownNotifications;

    public NotificationManager(Context context) {
        this.context = context;
        shownNotifications = new HashSet<>();
    }

    public void showNotification(Intent intent) {
        String title = intent.getStringExtra("title");
        String body = intent.getStringExtra("body");
        String chatId = intent.getStringExtra("chatuid");
        String notificationid = intent.getStringExtra("notificationid");
        if (!shownNotifications.contains(notificationid)) {
            Alerter.create((Activity) context)
                    .setTitle(title)
                    .setText(body)
                    .setIcon(R.drawable.ic_chat)
                    .setBackgroundColorRes(R.color.colorAccent)
                    .setDuration(6000)
                    .enableSwipeToDismiss()
                    .setOnClickListener(v -> {
                        Intent i = new Intent(context, ChatActivity.class);
                        i.putExtra("chatuid", chatId);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(i);
                    })
                    .show();
            shownNotifications.add(notificationid);
        }
    }

}
