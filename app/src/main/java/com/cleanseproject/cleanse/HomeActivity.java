package com.cleanseproject.cleanse;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent = getIntent();
        TextView lblHello = findViewById(R.id.lbl_usuario);
        lblHello.setText(intent.getStringExtra("username"));
        try {
            Log.d("user", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName());
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }
}
