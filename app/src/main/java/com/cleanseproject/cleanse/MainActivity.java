package com.cleanseproject.cleanse;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imagenhoja = (ImageView)findViewById(R.id.imghoja);
        ImageView imagengota = (ImageView)findViewById(R.id.imggota);
    }
}
