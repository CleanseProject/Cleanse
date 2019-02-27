package com.cleanseproject.cleanse.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.callbacks.UserNameLoadCallback;
import com.cleanseproject.cleanse.dataClasses.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;
    private ImageView imagenPerfil;
    private TextView txtNombre, txtCorreo,txtTelefono;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_perfil_usuario);
        txtNombre = findViewById(R.id.txtNombreUsuario);
        txtCorreo = findViewById(R.id.txtCorreoUsuario);
        txtTelefono = findViewById(R.id.txtTelefonoUsuario);
        mAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        getDatosUsuario(mAuth.getCurrentUser().getUid(),
                new UserNameLoadCallback() {
                    @Override
                    public void onUsernameLoaded(String username) {
                        txtNombre.setText(username);
                    }
                });
    }

    private void getDatosUsuario(String userId, UserNameLoadCallback callback) {
        DatabaseReference userRef = firebaseDatabase.getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                callback.onUsernameLoaded(user.getName() + " " + user.getSurname());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
