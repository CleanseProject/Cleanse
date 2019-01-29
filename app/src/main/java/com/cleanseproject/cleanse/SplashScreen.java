package com.cleanseproject.cleanse;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        ImageView imagenGota = findViewById(R.id.imgGota);
        Animation myanim = AnimationUtils.loadAnimation(this, R.anim.animation_abajo);
        imagenGota.startAnimation(myanim);
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashScreen.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        }, 2500);
    }
}
