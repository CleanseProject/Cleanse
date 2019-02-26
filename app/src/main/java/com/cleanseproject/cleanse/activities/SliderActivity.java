package com.cleanseproject.cleanse.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cleanseproject.cleanse.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

import androidx.core.content.ContextCompat;

public class SliderActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SliderPage sliderPage = new SliderPage();
        sliderPage.setTitle("¿Qué es Cleanse?");
        sliderPage.setDescription("Pues hacemos cosas");
        sliderPage.setImageDrawable(R.drawable.imagen);
        sliderPage.setBgColor(ContextCompat.getColor(getApplicationContext(), R.color.verdehojainterno));
        addSlide(AppIntroFragment.newInstance(sliderPage));

    }
}
