package com.cleanseproject.cleanse.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;

import com.cleanseproject.cleanse.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;


public class SliderActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SliderPage sliderPage = new SliderPage();
        sliderPage.setTitle(getString(R.string.slider_titulo_1));
        sliderPage.setImageDrawable(R.drawable.world);
        sliderPage.setDescription(getString(R.string.slider_descripcion_1));
        sliderPage.setBgColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        addSlide(AppIntroFragment.newInstance(sliderPage));

        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle(getString(R.string.slider_titulo_2));
        sliderPage2.setImageDrawable(R.drawable.marcadorverde_vector);
        sliderPage2.setDescription(getString(R.string.slider_descripcion_2));
        sliderPage2.setBgColor(ContextCompat.getColor(getApplicationContext(), R.color.verdehojainterno));
        addSlide(AppIntroFragment.newInstance(sliderPage2));


    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
    }
}
