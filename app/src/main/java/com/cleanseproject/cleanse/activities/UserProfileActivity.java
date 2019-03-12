package com.cleanseproject.cleanse.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.callbacks.UserNameLoadCallback;
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
import java.util.UUID;

public class UserProfileActivity extends AppCompatActivity implements BSImagePicker.OnSingleImageSelectedListener {
    private FirebaseDatabase firebaseDatabase;
    private ImageView imagenPerfil;
    private TextView txtUsuario;
    private FirebaseAuth mAuth;
    private Button btn_changepic;
    private Uri imagePath;
    private Button btn_savechanges;
    private String currentUserID;

    private ImageManagerService imageManagerService;
    private UserManagerService userManagerService;
    private String nombreCompletoAnterior;
    private String nombreCompletoNuevo;
    private boolean cambio_de_nombre;
    private boolean cambio_de_imagen;
    private EditText editTextNombre;
    private String nombre;
    private String apellido;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_perfil_usuario);
        btn_changepic = findViewById(R.id.btn_EditarPerfil);
        imagenPerfil = findViewById(R.id.ivAutor);
        btn_savechanges = findViewById(R.id.btnSaveChanges);
        editTextNombre = findViewById(R.id.edittxtNombre);
        txtUsuario = findViewById(R.id.txtUsuario);
        userManagerService = new UserManagerService();
        imageManagerService = new ImageManagerService();
        btn_savechanges.setEnabled(false);
        cambio_de_imagen = false;
        cambio_de_nombre = false;
        Toolbar toolbar = findViewById(R.id.profile_toolbar);
        toolbar.setTitle("Profile");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        imageManagerService.userImageDownloadUrl(mAuth.getCurrentUser().getUid(), url
                -> Glide.with(this).load(url).apply(RequestOptions.circleCropTransform()).into(imagenPerfil));
        currentUserID = mAuth.getCurrentUser().getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();

        getDatosUsuario(currentUserID,
                username -> editTextNombre.setText(username));

        editTextNombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                btn_savechanges.setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nombreCompletoNuevo = s.toString();


                if ((!nombreCompletoNuevo.equals(nombreCompletoAnterior) && nombreCompletoAnterior != null)) {
                    cambio_de_nombre = true;
                    btn_savechanges.setEnabled(true);
                } else if (nombreCompletoNuevo.equals(nombreCompletoAnterior)) {
                    cambio_de_nombre = false;
                    btn_savechanges.setEnabled(false);
                }

                if (cambio_de_imagen == true) {
                    btn_savechanges.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        if (mAuth.getCurrentUser().getPhoneNumber() != null && !mAuth.getCurrentUser().getPhoneNumber().equals("")) {
            txtUsuario.setText(mAuth.getCurrentUser().getPhoneNumber());
            Log.v("Cambio", "Movil" + "'" + mAuth.getCurrentUser().getPhoneNumber() + "'");
        } else if (mAuth.getCurrentUser().getEmail() != null) {
            txtUsuario.setText(mAuth.getCurrentUser().getEmail());
            Log.v("Cambio", "Correo");
        } else {
            txtUsuario.setText("error");
            Log.v("Cambio", "error");
        }


        btn_changepic.setOnClickListener(v -> {
            BSImagePicker singleSelectionPicker = new BSImagePicker.Builder("com.cleanseproject.fileprovider")
                    .build();
            singleSelectionPicker.show(getSupportFragmentManager(), "picker");
        });

        btn_savechanges.setOnClickListener(v -> {
            guardarCambios();

        });


    }

    private void guardarCambios() {
        String[] nombre_dividido = new String[2];
        if (cambio_de_nombre == true && cambio_de_imagen == true) {

            imageManagerService.uploadUserImage(mAuth.getCurrentUser().getUid(), imagePath);
            nombre_dividido = nombreCompletoNuevo.split(" ", 2);
            nombre = nombre_dividido[0];
            apellido = nombre_dividido[1];
            userManagerService.updateUserData(nombre, apellido);
            nombreCompletoAnterior = nombre + " " + apellido;
            Toast.makeText(this, "Changes saved successfully!", Toast.LENGTH_LONG).show();
        } else if (cambio_de_nombre == true) {
            nombre_dividido = nombreCompletoNuevo.split(" ", 2);
            nombre = nombre_dividido[0];
            apellido = nombre_dividido[1];
            userManagerService.updateUserData(nombre, apellido);
            nombreCompletoAnterior = nombre + " " + apellido;
            Toast.makeText(this, "Changes saved successfully!", Toast.LENGTH_LONG).show();
        } else if (cambio_de_imagen == true) {
            imageManagerService.uploadUserImage(mAuth.getCurrentUser().getUid(), imagePath);
            Toast.makeText(this, "Changes saved successfully!", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onBackPressed() {
        finish();
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    private void getDatosUsuario(String userId, UserNameLoadCallback callback) {

        DatabaseReference userRef = firebaseDatabase.getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                nombreCompletoAnterior = user.getName() + " " + user.getSurname();
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
                .withAspectRatio(1, 1)
                .start(UserProfileActivity.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            imagePath = UCrop.getOutput(data);
            Log.v("Cambio", imagePath + " ");
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                imagenPerfil.setImageBitmap(bitmap);
                btn_savechanges.setEnabled(true);
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
