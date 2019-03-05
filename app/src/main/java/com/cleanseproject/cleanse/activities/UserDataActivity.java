package com.cleanseproject.cleanse.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.dataClasses.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserDataActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private Button btnContinue;
    private EditText txtName;
    private EditText txtSurname;
    private boolean nombre, apellido;
    private ProgressBar progressBar;

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);
        btnContinue = findViewById(R.id.btn_save_user_data);
        txtName = findViewById(R.id.txt_name);
        txtSurname = findViewById(R.id.txt_surname);
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
                if (txtName.getText().toString().length() > 0) {
                    nombre = true;
                } else {
                    nombre = false;
                }
                if (nombre == true && apellido == true) {
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
                if (txtSurname.getText().toString().length() > 0) {
                    apellido = true;
                } else {
                    apellido = false;
                }
                if (nombre == true && apellido == true) {
                    btnContinue.setEnabled(true);
                }
            }
        });


        btnContinue.setOnClickListener(v -> {
            guardarDatos(txtName.getText().toString(), txtSurname.getText().toString());
            progressBar.setVisibility(View.VISIBLE);
        });


        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void guardarDatos(String name, String surname) {
        DatabaseReference userReference = firebaseDatabase.getReference("users");
        FirebaseUser user = firebaseAuth.getCurrentUser();
        userReference.child(user.getUid()).setValue(new User(user.getUid(), name, surname, ""));
        Intent intent = new Intent(UserDataActivity.this, HomeActivity.class);
        intent.putExtra("username", user);
        startActivity(intent);
        finish();
    }

}
