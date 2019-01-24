package com.cleanseproject.cleanse;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imagenHoja = (ImageView)findViewById(R.id.imgHoja);
        ImageView imagenGota = (ImageView)findViewById(R.id.imgGota);

        Animation myanim = AnimationUtils.loadAnimation(this, R.anim.animation_abajo);
        imagenGota.startAnimation(myanim);
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        }, 2500);
    }
}
