package com.cleanseproject.cleanse.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.dataClasses.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EventDetailsActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;
    private ImageView imagenEvento;
    private TextView txtDescripcion, txtTituloImagen, txtDistancia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_evento_seleccionado);
        imagenEvento = findViewById(R.id.imagenEventoSeleccionado);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        txtTituloImagen = findViewById(R.id.txtTituloImagen);
        txtDistancia = findViewById(R.id.txtDistancia);
        firebaseDatabase=FirebaseDatabase.getInstance();

        Intent intent = getIntent();
        String idEvento = intent.getStringExtra("Evento");
        DatabaseReference refEvents = firebaseDatabase.getReference("events").child(idEvento);
        refEvents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                /**
                 * TODO: Set de la imagen
                 */
                txtTituloImagen.setText(event.getName());
                txtDescripcion.setText(event.getDescription());
                String posicionEvento= "Lat:"+event.getLatitude()+" Long:"+event.getLongitude();
                txtDistancia.setText(posicionEvento);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
