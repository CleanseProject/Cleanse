package com.cleanseproject.cleanse;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);
        btnContinue = findViewById(R.id.btn_save_user_data);
        txtName = findViewById(R.id.txt_name);
        txtSurname = findViewById(R.id.txt_surname);
        btnContinue.setOnClickListener(v -> guardarDatos(txtName.getText().toString(), txtSurname.getText().toString()));
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void guardarDatos(String name, String surname) {
        DatabaseReference userReference = firebaseDatabase.getReference("users");
        FirebaseUser user = firebaseAuth.getCurrentUser();
        userReference.child(user.getUid()).setValue(new User(name, surname, ""));
    }

}
