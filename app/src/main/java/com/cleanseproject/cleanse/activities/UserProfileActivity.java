package com.cleanseproject.cleanse.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.dataClasses.User;
import com.cleanseproject.cleanse.services.ImageManagerService;
import com.cleanseproject.cleanse.services.UserManagerService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yalantis.ucrop.UCrop;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class UserProfileActivity extends AppCompatActivity implements BSImagePicker.OnSingleImageSelectedListener {

    private FirebaseDatabase firebaseDatabase;
    private ImageView imagenPerfil;
    private FirebaseAuth mAuth;
    private Uri imagePath;
    private Button btnSaveChanges;

    private ImageManagerService imageManagerService;
    private UserManagerService userManagerService;
    private String nombre;
    private String apellido;
    private boolean cambio_de_nombre;
    private boolean cambio_de_imagen;
    private EditText editTextNombre, txtSurname;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Button btn_changepic = findViewById(R.id.btn_EditarPerfil);
        imagenPerfil = findViewById(R.id.ivAutor);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        editTextNombre = findViewById(R.id.txt_edit_name);
        txtSurname = findViewById(R.id.txt_edit_surname);
        TextView txtUsuario = findViewById(R.id.txtUsuario);
        userManagerService = new UserManagerService();
        imageManagerService = new ImageManagerService();
        btnSaveChanges.setEnabled(false);
        cambio_de_imagen = false;
        cambio_de_nombre = false;
        Toolbar toolbar = findViewById(R.id.profile_toolbar);
        toolbar.setTitle(R.string.my_profile);
        setSupportActionBar(toolbar);
        ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        imageManagerService.userImageDownloadUrl(mAuth.getCurrentUser().getUid(), url
                -> Glide.with(this).load(url).apply(RequestOptions.circleCropTransform()).into(imagenPerfil));
        String currentUserID = mAuth.getCurrentUser().getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        getDatosUsuario(currentUserID);
        editTextNombre.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ((nombre != null && !s.equals(nombre))) {
                    cambio_de_nombre = true;
                    btnSaveChanges.setEnabled(true);
                } else if (s.equals(nombre)) {
                    cambio_de_nombre = false;
                    btnSaveChanges.setEnabled(false);
                }
                if (cambio_de_imagen) {
                    btnSaveChanges.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });
        editTextNombre.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ((apellido != null && !s.equals(apellido))) {
                    cambio_de_nombre = true;
                    btnSaveChanges.setEnabled(true);
                } else if (s.equals(nombre)) {
                    cambio_de_nombre = false;
                    btnSaveChanges.setEnabled(false);
                }
                if (cambio_de_imagen) {
                    btnSaveChanges.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });
        if (mAuth.getCurrentUser().getPhoneNumber() != null && !mAuth.getCurrentUser().getPhoneNumber().equals("")) {
            txtUsuario.setText(mAuth.getCurrentUser().getPhoneNumber());
        } else if (mAuth.getCurrentUser().getEmail() != null) {
            txtUsuario.setText(mAuth.getCurrentUser().getEmail());
        } else {
            txtUsuario.setText(getString(R.string.error));
        }
        btn_changepic.setOnClickListener(v -> {
            BSImagePicker singleSelectionPicker = new BSImagePicker.Builder("com.cleanseproject.fileprovider")
                    .build();
            singleSelectionPicker.show(getSupportFragmentManager(), "picker");
        });
        btnSaveChanges.setOnClickListener(v -> guardarCambios());
    }

    private void guardarCambios() {
        String nombre = editTextNombre.getText().toString();
        String apellido = txtSurname.getText().toString();
        if (cambio_de_nombre && cambio_de_imagen) {
            imageManagerService.uploadUserImage(mAuth.getCurrentUser().getUid(), imagePath);
            userManagerService.updateUserData(nombre, apellido);
            this.nombre = nombre;
            Toast.makeText(this, getString(R.string.changes_success), Toast.LENGTH_LONG).show();
        } else if (cambio_de_nombre) {
            userManagerService.updateUserData(nombre, apellido);
            this.nombre = nombre;
            Toast.makeText(this, getString(R.string.changes_success), Toast.LENGTH_LONG).show();
        } else if (cambio_de_imagen) {
            imageManagerService.uploadUserImage(mAuth.getCurrentUser().getUid(), imagePath);
            Toast.makeText(this, getString(R.string.changes_success), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    private void getDatosUsuario(String userId) {
        DatabaseReference userRef = firebaseDatabase.getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    nombre = user.getName();
                    apellido = user.getSurname();
                    editTextNombre.setText(user.getName());
                    txtSurname.setText(user.getSurname());
                }
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
                .withAspectRatio(1, 1)
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
                btnSaveChanges.setEnabled(true);
                cambio_de_imagen = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            UCrop.getError(data).printStackTrace();
            cambio_de_imagen = false;
        }
    }

}
