package com.cleanseproject.cleanse.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.adapters.UsersInEventAdapter;
import com.cleanseproject.cleanse.callbacks.UserChangedCallback;
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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

public class EventDetailsActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EventManagerService eventManagerService;
    private ChatManagerService chatManagerService;
    private ImageManagerService imageManagerService;
    private UserManagerService userManagerService;
    private NotificationManager notificationManager;
    private LocationService locationService;
    private Event event;
    private ArrayList<User> users;

    private ImageView imagenEvento, imageAutor;
    private TextView txtDescripcion, txtDistancia, txtAutor, txtFecha;
    private RecyclerView rvUsuarios;
    private UsersInEventAdapter adapter;
    private FloatingActionButton fab_menu;
    private FloatingActionButton fab_chat;
    private FloatingActionButton fab_equis;
    private FloatingActionButton fab_check;
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
        imageAutor = findViewById(R.id.ivAutor);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        txtDistancia = findViewById(R.id.txt_distancia);
        txtFecha = findViewById(R.id.txt_fecha);
        rvUsuarios = findViewById(R.id.rvUsuarios);
        fab_menu = findViewById(R.id.fabMenu);
        fab_chat = findViewById(R.id.fabchat);
        fab_equis = findViewById(R.id.fabequis);
        fab_check = findViewById(R.id.fabcheck);
        txtAutor = findViewById(R.id.txtAutor);
        eventManagerService = new EventManagerService();
        chatManagerService = new ChatManagerService();
        imageManagerService = new ImageManagerService();
        userManagerService = new UserManagerService();
        notificationManager = new NotificationManager(this);
        locationService = new LocationService(this);
        firebaseAuth = FirebaseAuth.getInstance();
        fab_chat.setOnClickListener(v -> startChat());
        txtDescripcion.setMovementMethod(new ScrollingMovementMethod());
        Intent intent = getIntent();
        String idEvento = intent.getStringExtra("evento");
        eventManagerService.getEvent(
                idEvento,
                event -> {
                    this.event = event;
                    imageManagerService.userImageDownloadUrl(event.getCreatorId(), url
                            -> Glide.with(this).load(url).apply(RequestOptions.circleCropTransform().centerCrop()).into(imageAutor));
                    Location location = new Location("");
                    location.setLatitude(event.getLatitude());
                    location.setLongitude(event.getLongitude());
                    event.setDistance(locationService.distance(location));
                    Coltoolbar.setTitle(event.getName());
                    txtDescripcion.setText(event.getDescription());
                    userManagerService.getUser(
                            event.getCreatorId(),
                            user -> txtAutor.setText(String.format("%s %s", user.getName(), user.getSurname())));
                    if (event.getDistance() != -1) {
                        String distancia;
                        if (event.getDistance() >= 1000)
                            distancia = Math.round(event.getDistance() / 1000) + " km";
                        else
                            distancia = Math.round(event.getDistance()) + " m";
                        txtDistancia.setText(distancia);
                    }
                    if (event.getEventDate() != 0)
                        txtFecha.setText(formatDate(event.getEventDate()));
                    eventManagerService.isUserAdmin(event.getId(), isAdmin -> {
                        if (isAdmin)
                            fab_equis.setOnClickListener(v -> deleteEventDialog());
                    });
                    eventManagerService.isEventFavourite(event.getId(), isFavourite -> {
                        if (isFavourite) {
                            fab_check.setAlpha(0f);
                            fab_equis.setAlpha(1.0f);
                            fab_equis.setEnabled(true);
                            fab_check.setEnabled(false);
                            fab_chat.setEnabled(true);
                            int accent = 0xFF81F1B2;
                            fab_chat.setBackgroundTintList(ColorStateList.valueOf(accent));
                            fab_chat.setColorFilter(0xFFFFFFFF);
                        } else {
                            fab_check.setAlpha(1.0f);
                            fab_equis.setAlpha(0f);
                            fab_equis.setEnabled(false);
                            fab_check.setEnabled(true);
                            fab_chat.setEnabled(false);
                            int accentdark = 0xFF60BA87;
                            fab_chat.setBackgroundTintList(ColorStateList.valueOf(accentdark));
                            fab_chat.setColorFilter(0xFFE5E5E5);
                        }
                    });
                    eventManagerService.getEventUsers(
                            event.getId(),
                            new UserChangedCallback() {
                                @Override
                                public void onUserLoad(User user) {
                                    addMemberUser(user);
                                }

                                @Override
                                public void userRemoved(String userId) {
                                    removeMemberUser(userId);
                                }
                            }
                    );
                });
        imageManagerService.eventImageDownloadUrl(
                idEvento,
                imageUrl -> Glide.with(this)
                        .load(imageUrl)
                        .into(imagenEvento));
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvUsuarios.setLayoutManager(layoutManager);
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
            eventManagerService.setEventAsFavourite(event.getId());
            fab_chat.setEnabled(true);
            int accent = 0xFF81F1B2;
            fab_chat.setBackgroundTintList(ColorStateList.valueOf(accent));
            fab_chat.setColorFilter(0xFFFFFFFF);
        });
        fab_equis.setOnClickListener(v -> {
            fab_check.animate().alpha(1.0f);
            fab_equis.animate().alpha(0f);
            fab_equis.setEnabled(false);
            fab_check.setEnabled(true);
            eventManagerService.deleteFavouriteEvent(event.getId());
            fab_chat.setEnabled(false);
            int accentdark = 0xFF60BA87;
            fab_chat.setBackgroundTintList(ColorStateList.valueOf(accentdark));
            fab_chat.setColorFilter(0xFFE5E5E5);
        });
        users = new ArrayList<>();
        adapter = new UsersInEventAdapter(users);
        rvUsuarios.setAdapter(adapter);
    }

    private void deleteEventDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Borrar evento")
                .setMessage("¿Está seguro de que desea borrar el evento?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Borrar", (dialog, which) -> {
                    eventManagerService.deleteEvent(event.getId());
                    setResult(RESULT_OK);
                    finish();
                })
                .show();
    }

    private void addMemberUser(User user) {
        users.add(user);
        adapter.notifyDataSetChanged();
    }

    private void removeMemberUser(String userId) {
        Iterator<User> i = users.iterator();
        while (i.hasNext()) {
            User user = i.next();
            if (user.getUserId().equals(userId))
                i.remove();
        }
        adapter.notifyDataSetChanged();
    }

    private final BroadcastReceiver onEvent = new BroadcastReceiver() {
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

    private String formatDate(long time) {
        Date date = new Date(time);
        Locale locale;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            locale = getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = getResources().getConfiguration().locale;
        }
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        return dateFormat.format(date);
    }

}
