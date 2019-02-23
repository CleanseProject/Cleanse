package com.cleanseproject.cleanse.services;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import java.util.HashSet;

public class NotificationManager {

    private View coordinatorLayout;
    private HashSet<String> shownNotifications;

    public NotificationManager(View coordinatorLayout) {
        this.coordinatorLayout = coordinatorLayout;
        shownNotifications = new HashSet<>();
    }

    public void showNotification(Intent intent) {
        Log.d("notification", "");
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
            Snackbar.make(coordinatorLayout, mensaje,
                    Snackbar.LENGTH_LONG)
                    .show();
        shownNotifications.add(mensaje);
    }

}
