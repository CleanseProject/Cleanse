package com.cleanseproject.cleanse.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.adapters.UsersInEventAdapter;
import com.cleanseproject.cleanse.dataClasses.Event;
import com.cleanseproject.cleanse.dataClasses.User;
import com.cleanseproject.cleanse.services.ChatManagerService;
import com.cleanseproject.cleanse.services.EventManagerService;
import com.cleanseproject.cleanse.services.ImageManagerService;
import com.cleanseproject.cleanse.services.LocationService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class EventDetailsActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EventManagerService eventManagerService;
    private ChatManagerService chatManagerService;
    private ImageManagerService imageManagerService;
    private LocationService locationService;
    private Toolbar toolbar;
    private Event event;

    private ImageView imagenEvento;
    private TextView txtDescripcion, txtDistancia, txtJoinChat;
    private RecyclerView rvUsuarios;
    private UsersInEventAdapter adapter;
    private FloatingActionButton fab_menu;
    private FloatingActionButton fab_chat;
    private FloatingActionButton fab_equis;
    private FloatingActionButton fab_check;
    private boolean fabAbierto;
    private boolean suscrito;


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
        suscrito = false;
        imagenEvento = findViewById(R.id.imagenEventoSeleccionado);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        // txtDistancia = findViewById(R.id.txtDistancia);
        txtJoinChat = findViewById(R.id.txt_join_chat);
        rvUsuarios = findViewById(R.id.rvUsuarios);
        fab_menu = findViewById(R.id.fabMenu);
        fab_chat = findViewById(R.id.fabchat);
        fab_equis = findViewById(R.id.fabequis);
        fab_check = findViewById(R.id.fabcheck);
        fab_menu.setOnClickListener(v -> {
            if (fabAbierto) {
                fab_chat.animate().translationX(0);
                fab_equis.animate().translationY(0);
                fab_check.animate().translationY(0);
                fabAbierto = false;

            } else if (!fabAbierto) {
                fab_chat.animate().translationX(-180);
                fab_equis.animate().translationY(170);
                fab_check.animate().translationY(170);
                fabAbierto = true;
            }
        });
        if (suscrito == true) {
            fab_check.setAlpha(0f);
            fab_equis.setAlpha(1.0f);
            fab_equis.setEnabled(true);
            fab_check.setEnabled(false);
        } else if (suscrito == false) {
            fab_check.setAlpha(1.0f);
            fab_equis.setAlpha(0f);
            fab_equis.setEnabled(false);
            fab_check.setEnabled(true);
        }

        fab_check.setOnClickListener(v -> {
            fab_check.animate().alpha(0f);
            fab_equis.animate().alpha(1.0f);
            fab_equis.setEnabled(true);
            fab_check.setEnabled(false);
            suscrito = false;
        });

        fab_equis.setOnClickListener(v -> {
            fab_check.animate().alpha(1.0f);
            fab_equis.animate().alpha(0f);
            fab_equis.setEnabled(false);
            fab_check.setEnabled(true);
            suscrito = true;
        });

        eventManagerService = new EventManagerService();
        chatManagerService = new ChatManagerService();
        imageManagerService = new ImageManagerService();
        locationService = new LocationService(this);
        firebaseAuth = FirebaseAuth.getInstance();
        fab_chat.setOnClickListener(v -> startChat());
        txtDescripcion.setMovementMethod(new ScrollingMovementMethod());
        Intent intent = getIntent();
        String idEvento = intent.getStringExtra("Evento");
        eventManagerService.getEvent(idEvento, event -> {
            this.event = event;
            Coltoolbar.setTitle(event.getName());
            txtDescripcion.setText(event.getDescription());
            String distancia;
            if (event.getDistance() >= 1000)
                distancia = Math.round(event.getDistance() / 1000) + " km";
            else
                distancia = Math.round(event.getDistance()) + " m";
            //txtDistancia.setText(distancia);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startChat() {
        chatManagerService.joinChat(firebaseAuth.getCurrentUser().getUid(), event.getId());
        Intent intent = new Intent(EventDetailsActivity.this, ChatActivity.class);
        intent.putExtra("chatuid", event.getId());
        intent.putExtra("chatname", event.getName());
        startActivity(intent);
    }

}
