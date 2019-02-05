package com.cleanseproject.cleanse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cleanseproject.cleanse.services.CleanseFirebaseMessagingService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private RecyclerView rvEventos;

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter f = new IntentFilter(CleanseFirebaseMessagingService.NOTIFICATION);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(onEvent, f);
    }

    private BroadcastReceiver onEvent = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent i) {
            //if (i.getAction() != null && i.getAction() != MyFirebaseMessagingService.NOTIFICATION) {
            String title = i.getStringExtra("title");
            String body = i.getStringExtra("body");
            String mensaje = "";
            if (body != null && title != null) {
                mensaje = title + ": " + body;
            } else if (body != null) {
                mensaje = body;
            } else if (title != null) {
                mensaje = title;
            }
            Snackbar.make(findViewById(R.id.homeCoordinatorLayout), mensaje,
                    Snackbar.LENGTH_LONG)
                    .show();
            //}
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent = getIntent();
/*       TextView lblHello = findViewById(R.id.lbl_usuario);
        lblHello.setText(intent.getStringExtra("username"));
        try {
            Log.d("user", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }*/
        initializeUI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            menuItem.setChecked(true);
            drawerLayout.closeDrawers();
            return true;
        });

        rvEventos=findViewById(R.id.rv_Eventos);
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("MyFirebaseMsgService", "getInstanceId failed", task.getException());
                            return;
                        }
                        String token = task.getResult().getToken();
                        Log.d("FCMToken", token);
                    }
                });
    }

}
