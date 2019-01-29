package com.cleanseproject.cleanse;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        TextView txtTituloo = findViewById(R.id.txtTitulo);
        SpannableString ss = new SpannableString("C l e a n S e");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            ForegroundColorSpan fcsVerde = new ForegroundColorSpan(getColor(R.color.verdehojaexterno));
            ss.setSpan(fcsVerde, 10, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        txtTituloo.setText(ss);
        ImageView imagenGota = findViewById(R.id.imgGota);
        Animation myanim = AnimationUtils.loadAnimation(this, R.anim.animation_abajo);
        imagenGota.startAnimation(myanim);
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashScreen.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        }, 2300);
    }
}
