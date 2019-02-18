package com.cleanseproject.cleanse.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.adapters.UsersInEventAdapter;
import com.cleanseproject.cleanse.dataClasses.Event;
import com.cleanseproject.cleanse.dataClasses.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventDetailsActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;
    private ImageView imagenEvento, imagenBack;
    private TextView txtDescripcion, txtTituloImagen, txtDistancia;
    private RecyclerView rvUsuarios;
    private UsersInEventAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_evento_seleccionado);
        imagenEvento = findViewById(R.id.imagenEventoSeleccionado);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        txtTituloImagen = findViewById(R.id.txtTituloImagen);
        txtDistancia = findViewById(R.id.txtDistancia);
        rvUsuarios = findViewById(R.id.rvUsuarios);
        imagenBack = findViewById(R.id.imagenBack);
        imagenBack.setOnClickListener(v -> onBackPressed());
        firebaseDatabase = FirebaseDatabase.getInstance();
        txtDescripcion.setMovementMethod(new ScrollingMovementMethod());

        Intent intent = getIntent();
        String idEvento = intent.getStringExtra("Evento");
        DatabaseReference refEvents = firebaseDatabase.getReference("events").child(idEvento);
        refEvents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                // TODO: Set de la imagen
                txtTituloImagen.setText(event.getName());
                txtDescripcion.setText(event.getDescription());
                String posicionEvento = "Lat:" + event.getLatitude() + " Long:" + event.getLongitude();
                txtDistancia.setText(posicionEvento);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
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

}
