package com.cleanseproject.cleanse.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cleanseproject.cleanse.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class SliderActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SliderPage sliderPage = new SliderPage();
        sliderPage.setTitle("¿Qué es Cleanse?");
        sliderPage.setImageDrawable(R.drawable.imagen);
        sliderPage.setDescription("Pues hacemos cosas");
        sliderPage.setBgColor(ContextCompat.getColor(getApplicationContext(), R.color.verdehojainterno));
        addSlide(AppIntroFragment.newInstance(sliderPage));

        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle("¿Qué harás con nosotros?");
        sliderPage2.setImageDrawable(R.drawable.imagen);
        sliderPage2.setDescription("Quedaras y todo eso con gente y tal");
        sliderPage2.setBgColor(ContextCompat.getColor(getApplicationContext(), R.color.verdehojainterno));
        addSlide(AppIntroFragment.newInstance(sliderPage2));

        showSkipButton(false);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent intent = new Intent(getApplicationContext(),UserDataActivity.class);
        startActivity(intent);
    }
}
