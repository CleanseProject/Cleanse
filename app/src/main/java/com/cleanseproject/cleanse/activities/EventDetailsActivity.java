package com.cleanseproject.cleanse.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.adapters.UsersInEventAdapter;
import com.cleanseproject.cleanse.dataClasses.Event;
import com.cleanseproject.cleanse.dataClasses.User;
import com.cleanseproject.cleanse.services.ChatManagerService;
import com.cleanseproject.cleanse.services.CleanseFirebaseMessagingService;
import com.cleanseproject.cleanse.services.EventManagerService;
import com.cleanseproject.cleanse.services.ImageManagerService;
import com.cleanseproject.cleanse.services.LocationService;
import com.cleanseproject.cleanse.services.NotificationManager;
import com.cleanseproject.cleanse.services.UserManagerService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class EventDetailsActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EventManagerService eventManagerService;
    private ChatManagerService chatManagerService;
    private ImageManagerService imageManagerService;
    private UserManagerService userManagerService;
    private NotificationManager notificationManager;
    private LocationService locationService;
    private Toolbar toolbar;
    private Event event;

    private ImageView imagenEvento;
    private TextView txtDescripcion, txtDistancia, txtAutor;
    private RecyclerView rvUsuarios;
    private UsersInEventAdapter adapter;
    private FloatingActionButton fab_menu;
    private FloatingActionButton fab_chat;
    private FloatingActionButton fab_equis;
    private FloatingActionButton fab_check;
    private Button btnDelete;
    private boolean fabAbierto;

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter f = new IntentFilter(CleanseFirebaseMessagingService.NOTIFICATION);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(onEvent, f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        CollapsingToolbarLayout Coltoolbar = findViewById(R.id.event_details_toolbar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        fabAbierto = false;
        imagenEvento = findViewById(R.id.imagenEventoSeleccionado);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        txtDistancia = findViewById(R.id.txtDistancia);
        rvUsuarios = findViewById(R.id.rvUsuarios);
        fab_menu = findViewById(R.id.fabMenu);
        fab_chat = findViewById(R.id.fabchat);
        fab_equis = findViewById(R.id.fabequis);
        fab_check = findViewById(R.id.fabcheck);
        txtAutor = findViewById(R.id.txtAutor);
        btnDelete = findViewById(R.id.btn_delete);
        fab_menu.setOnClickListener(v -> {
            if (fabAbierto) {
                fab_chat.animate().translationX(0);
                fab_equis.animate().translationY(0);
                fab_check.animate().translationY(0);
                fabAbierto = false;

            } else {
                fab_chat.animate().translationX(-180);
                fab_equis.animate().translationY(170);
                fab_check.animate().translationY(170);
                fabAbierto = true;
            }
        });
        fab_check.setOnClickListener(v -> {
            fab_check.animate().alpha(0f);
            fab_equis.animate().alpha(1.0f);
            fab_equis.setEnabled(true);
            fab_check.setEnabled(false);
            eventManagerService.deleteFavouriteEvent(event.getId());
        });
        fab_equis.setOnClickListener(v -> {
            fab_check.animate().alpha(1.0f);
            fab_equis.animate().alpha(0f);
            fab_equis.setEnabled(false);
            fab_check.setEnabled(true);
            eventManagerService.setEventAsFavourite(event.getId());
        });
        eventManagerService = new EventManagerService();
        chatManagerService = new ChatManagerService();
        imageManagerService = new ImageManagerService();
        userManagerService = new UserManagerService();
        notificationManager = new NotificationManager(findViewById(R.id.event_details_coordinator_layout));
        locationService = new LocationService(this);
        firebaseAuth = FirebaseAuth.getInstance();
        fab_chat.setOnClickListener(v -> startChat());
        txtDescripcion.setMovementMethod(new ScrollingMovementMethod());
        Intent intent = getIntent();
        String idEvento = intent.getStringExtra("Evento");
        eventManagerService.getEvent(
                idEvento,
                event -> {
                    this.event = event;
                    Coltoolbar.setTitle(event.getName());
                    txtDescripcion.setText(event.getDescription());
                    userManagerService.getUser(
                            event.getCreatorId(),
                            user -> txtAutor.setText(String.format("%s %s", user.getName(), user.getSurname())));
                    String distancia;
                    if (event.getDistance() >= 1000)
                        distancia = Math.round(event.getDistance() / 1000) + " km";
                    else
                        distancia = Math.round(event.getDistance()) + " m";
                    //txtDistancia.setText(distancia);
                    eventManagerService.isUserAdmin(event.getId(), isAdmin -> {
                        if (isAdmin) {
                            btnDelete.setVisibility(View.VISIBLE);
                            btnDelete.setOnClickListener(v -> {
                                eventManagerService.deleteEvent(event.getId());
                                goBack();
                            });
                        } else {
                            eventManagerService.isEventFavourite(event.getId(), isFavourite -> {
                                if (isFavourite) {
                                    fab_check.setAlpha(0f);
                                    fab_equis.setAlpha(1.0f);
                                    fab_equis.setEnabled(true);
                                    fab_check.setEnabled(false);
                                } else {
                                    fab_check.setAlpha(1.0f);
                                    fab_equis.setAlpha(0f);
                                    fab_equis.setEnabled(false);
                                    fab_check.setEnabled(true);
                                }
                            });
                        }
                    });
                });
        imageManagerService.eventImageDownloadUrl(
                idEvento,
                imageUrl -> {
                    Glide.with(this)
                            .load(imageUrl)
                            .into(imagenEvento);
                });
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvUsuarios.setLayoutManager(layoutManager);
        User u1 = new User();
        User u2 = new User();
        ArrayList<User> lista = new ArrayList<>();
        lista.add(u1);
        lista.add(u2);
        adapter = new UsersInEventAdapter(lista);
        rvUsuarios.setAdapter(adapter);
    }

    private BroadcastReceiver onEvent = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent i) {
            notificationManager.showNotification(i);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goBack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    private void goBack() {
        Intent intent = NavUtils.getParentActivityIntent(this);
        // Check if Activity has been opened from notification
        if (NavUtils.shouldUpRecreateTask(this, intent) || isTaskRoot()) {
            TaskStackBuilder.create(this)
                    .addNextIntentWithParentStack(intent)
                    .startActivities();
        } else {
            NavUtils.navigateUpTo(this, intent);
        }
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    private void startChat() {
        chatManagerService.joinChat(firebaseAuth.getCurrentUser().getUid(), event.getId());
        Intent intent = new Intent(EventDetailsActivity.this, ChatActivity.class);
        intent.putExtra("chatuid", event.getId());
        intent.putExtra("chatname", event.getName());
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onEvent);
        super.onPause();
    }

}
