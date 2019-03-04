package com.cleanseproject.cleanse.services;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.activities.ChatActivity;

import java.util.HashSet;

public class NotificationManager {

    private View coordinatorLayout;
    private Context context;
    private HashSet<String> shownNotifications;

    public NotificationManager(Context context, View coordinatorLayout) {
        this.context = context;
        this.coordinatorLayout = coordinatorLayout;
        shownNotifications = new HashSet<>();
    }

    public void showNotification(Intent intent) {
        Log.d("notification", "");
        String title = intent.getStringExtra("title");
        String body = intent.getStringExtra("body");
        String chatId = intent.getStringExtra("chatuid");
        String mensaje = "";
        if (body != null && title != null) {
            mensaje = title + ": " + body;
        } else if (body != null) {
            mensaje = body;
        } else if (title != null) {
            mensaje = title;
        }
        if (!shownNotifications.contains(mensaje)) {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, mensaje,
                    Snackbar.LENGTH_LONG);
            shownNotifications.add(mensaje);
            View sbView = snackbar.getView();
            sbView.setClickable(true);
            sbView.setFocusable(true);
            snackbar.show();
            sbView.setOnClickListener(view -> {
                Intent i = new Intent(context, ChatActivity.class);
                i.putExtra("chatuid", chatId);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(i);
            });
        }
    }

}
