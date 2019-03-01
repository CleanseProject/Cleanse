package com.cleanseproject.cleanse.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.asksira.bsimagepicker.BSImagePicker;
import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.callbacks.UserNameLoadCallback;
import com.cleanseproject.cleanse.dataClasses.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yalantis.ucrop.UCrop;

import java.io.IOException;
import java.util.UUID;

public class UserProfileActivity extends AppCompatActivity implements BSImagePicker.OnSingleImageSelectedListener {
    private FirebaseDatabase firebaseDatabase;
    private ImageView imagenPerfil;
    private TextView txtNombre;
    private FirebaseAuth mAuth;
    private Button btn_changepic;
    private Uri imagePath;
    private Button btn_savechanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_perfil_usuario);
        txtNombre = findViewById(R.id.txtNombreUsuario);
        btn_changepic = findViewById(R.id.btn_EditarPerfil);
        imagenPerfil = findViewById(R.id.ivAutor);
        btn_savechanges = findViewById(R.id.btnSaveChanges);
        btn_savechanges.setEnabled(false);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        getDatosUsuario(mAuth.getCurrentUser().getUid(),
                new UserNameLoadCallback() {
                    @Override
                    public void onUsernameLoaded(String username) {
                        txtNombre.setText(username);
                    }
                });

        btn_changepic.setOnClickListener(v -> {
            BSImagePicker singleSelectionPicker = new BSImagePicker.Builder("com.cleanseproject.fileprovider")
                    .build();
            singleSelectionPicker.show(getSupportFragmentManager(), "picker");
        });

        btn_savechanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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


    @Override
    public void onSingleImageSelected(Uri uri, String tag) {
        String tempPath = UserProfileActivity.this.getCacheDir().toURI().toString() + UUID.randomUUID();
        UCrop.Options options = new UCrop.Options();
        options.setCircleDimmedLayer(true);
        options.setShowCropGrid(true);
        options.setActiveWidgetColor(getResources().getColor(R.color.colorAccent));
        UCrop.of(uri, Uri.parse(tempPath))
                .withOptions(options)
                .withAspectRatio(1,1)
                .start(UserProfileActivity.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            imagePath = UCrop.getOutput(data);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                imagenPerfil.setImageBitmap(bitmap);
                btn_savechanges.setEnabled(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            UCrop.getError(data).printStackTrace();
        }
    }
}
