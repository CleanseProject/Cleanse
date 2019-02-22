package com.cleanseproject.cleanse.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;

import com.cleanseproject.cleanse.R;

import java.util.HashSet;

public class NotificationManager {

    private Context context;
    private HashSet<String> shownNotifications;

    public NotificationManager(Context context) {
        this.context = context;
        shownNotifications = new HashSet<>();
    }

    public void showNotification(Intent intent) {
        String title = intent.getStringExtra("title");
        String body = intent.getStringExtra("body");
        String mensaje = "";
        if (body != null && title != null) {
            mensaje = title + ": " + body;
        } else if (body != null) {
            mensaje = body;
        } else if (title != null) {
            mensaje = title;
        }
        if (!shownNotifications.contains(mensaje))
            Snackbar.make(((Activity) context).findViewById(R.id.homeCoordinatorLayout), mensaje,
                    Snackbar.LENGTH_LONG)
                    .show();
        shownNotifications.add(mensaje);
    }

}
