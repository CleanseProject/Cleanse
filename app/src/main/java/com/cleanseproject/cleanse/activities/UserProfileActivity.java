package com.cleanseproject.cleanse.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.cleanseproject.cleanse.R;
import com.google.firebase.database.FirebaseDatabase;

public class UserProfileActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;
    private ImageView imagenPerfil;
    private TextView txtNombre, txtApellido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_perfil_usuario);
        txtNombre = findViewById(R.id.txtNombreUsuario);
        txtApellido = findViewById(R.id.txtApellidoUsuario);
    }
}
