package com.cleanseproject.cleanse.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.fragments.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;

public class AddEventActivity extends AppCompatActivity {

    private DatePickerDialog mDateSetListener;
    private Button btnSelectDate;
    private Button btnSelectLocation;
    private Button btnSelectPic;
    private ImageView imgExit;
    private Spinner spn_estado;
    private ImageView imgEstado;

    private boolean frameAbierto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        imgExit = findViewById(R.id.imgExit);
        btnSelectLocation = findViewById(R.id.btn_set_location);
        btnSelectPic = findViewById(R.id.btn_set_pic);
        btnSelectDate = findViewById(R.id.btn_set_date);
        spn_estado = findViewById(R.id.spnEstado);
        imgEstado = findViewById(R.id.img_estado);
        ////////////////// Intent
        Intent i = getIntent();
        Double lat = i.getDoubleExtra("Latitud", 0);
        Double lon = i.getDoubleExtra("Longitud", 0);
        if (lat != 0 && lon != 0) {
            btnSelectLocation.setText("Lat/Lon: " + lat + "/" + lon);
        }
        //////////////////
        ArrayList<String> lista = new ArrayList<>();
        lista.add("Limpio");
        lista.add("Sucio");
        lista.add("Critico");
        SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, lista);
        spn_estado.setAdapter(spinnerAdapter);
        spn_estado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String imagen = (String) parent.getItemAtPosition(position);
                switch (imagen) {
                    case "Limpio":
                        Log.v("Mensaje", "Limpio");
                        imgEstado.setImageResource(R.drawable.marcadorverde_vector);
                        break;
                    case "Sucio":
                        Log.v("Mensaje", "Sucio");
                        imgEstado.setImageResource(R.drawable.marcadornaranja_vector);
                        break;
                    case "Critico":
                        Log.v("Mensaje", "Critico");
                        imgEstado.setImageResource(R.drawable.marcadorrojo_vector);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        FrameLayout addEvent = findViewById(R.id.FrameLayout_add_event);
        btnSelectDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            mDateSetListener = new DatePickerDialog(AddEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    btnSelectDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                }
            }, 2018, month, day);
            mDateSetListener.show();

        });

        imgExit.setOnClickListener(v -> {
            if (frameAbierto) {
                addEvent.setVisibility(View.GONE);
                frameAbierto = false;
            } else {
                finish();
            }
        });
        btnSelectLocation.setOnClickListener(v -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.FrameLayout_add_event, new MapFragment());
            addEvent.setVisibility(View.VISIBLE);
            transaction.addToBackStack(null);
            transaction.commit();
            frameAbierto = true;
        });
    }

    public void setFrameAbierto(boolean frameAbierto) {
        this.frameAbierto = frameAbierto;
    }

}
