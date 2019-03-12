package com.cleanseproject.cleanse.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.dataClasses.User;
import com.cleanseproject.cleanse.services.ImageManagerService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yalantis.ucrop.UCrop;

import java.util.UUID;

public class UserDataActivity extends AppCompatActivity implements BSImagePicker.OnSingleImageSelectedListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private ImageManagerService imageManagerService;

    private ImageView ivProfile;
    private Button btnContinue;
    private EditText txtName;
    private EditText txtSurname;
    private boolean nombre, apellido;
    private ProgressBar progressBar;

    private boolean cambioImagen;
    private Uri imagePath;

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);
        firebaseDatabase = FirebaseDatabase.getInstance();
        imageManagerService = new ImageManagerService();
        firebaseAuth = FirebaseAuth.getInstance();
        btnContinue = findViewById(R.id.btn_save_user_data);
        txtName = findViewById(R.id.txt_name);
        txtSurname = findViewById(R.id.txt_surname);
        ivProfile = findViewById(R.id.iv_user_profile_pic);
        btnContinue.setEnabled(false);
        progressBar = findViewById(R.id.progressbar_continue);
        txtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                nombre = txtName.getText().toString().length() > 0;
                if (nombre && apellido) {
                    btnContinue.setEnabled(true);
                }
            }
        });
        txtSurname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                apellido = txtSurname.getText().toString().length() > 0;
                if (nombre && apellido) {
                    btnContinue.setEnabled(true);
                }
            }
        });
        btnContinue.setOnClickListener(v -> {
            guardarDatos(txtName.getText().toString(), txtSurname.getText().toString());
            progressBar.setVisibility(View.VISIBLE);
        });
        ivProfile.setOnClickListener(v -> {
            BSImagePicker singleSelectionPicker = new BSImagePicker.Builder("com.cleanseproject.fileprovider")
                    .build();
            singleSelectionPicker.show(getSupportFragmentManager(), "picker");
        });
    }

    private void guardarDatos(String name, String surname) {
        if (cambioImagen)
            imageManagerService.uploadUserImage(firebaseAuth.getCurrentUser().getUid(), imagePath);
        DatabaseReference userReference = firebaseDatabase.getReference("users");
        FirebaseUser user = firebaseAuth.getCurrentUser();
        userReference.child(user.getUid()).setValue(new User(user.getUid(), name, surname, ""));
        Intent intent = new Intent(UserDataActivity.this, HomeActivity.class);
        intent.putExtra("username", user);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSingleImageSelected(Uri uri, String tag) {
        String tempPath = UserDataActivity.this.getCacheDir().toURI().toString() + UUID.randomUUID();
        UCrop.Options options = new UCrop.Options();
        options.setCircleDimmedLayer(true);
        options.setShowCropGrid(true);
        options.setActiveWidgetColor(getResources().getColor(R.color.colorAccent));
        UCrop.of(uri, Uri.parse(tempPath))
                .withOptions(options)
                .withAspectRatio(1, 1)
                .start(UserDataActivity.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            imagePath = UCrop.getOutput(data);
            Glide.with(this)
                    .load(imagePath)
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivProfile);
            cambioImagen = true;
        }
    }

}
