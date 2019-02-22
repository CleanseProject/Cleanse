package com.cleanseproject.cleanse.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.dataClasses.Event;
import com.cleanseproject.cleanse.fragments.MapFragment;
import com.cleanseproject.cleanse.services.EventManagerService;
import com.cleanseproject.cleanse.services.LocationService;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class AddEventActivity extends AppCompatActivity {

    private LocationService locationService;
    private EventManagerService eventManagerService;

    public static final int PICK_IMAGE = 1;

    private DatePickerDialog mDateSetListener;
    private Button btnSelectDate;
    private Button btnSelectLocation;
    private Button btnSelectPic;
    private Button btnAdd;
    private ImageView imgExit, selectedImage;
    private Spinner spn_estado;
    private ImageView imgEstado;
    private EditText txtTitle, txtDescription;

    private Boolean limpio;
    private Boolean sucio;
    private Boolean critico;

    private RadioButton rdbtn_limpio;
    private RadioButton rdbtn_sucio;
    private RadioButton rdbtn_critico;


    private Uri imagePath;

    private Uri filePath;

    private boolean frameAbierto;
    private LatLng eventLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        imgExit = findViewById(R.id.imgExit);
        btnSelectLocation = findViewById(R.id.btn_set_location);

        btnSelectDate = findViewById(R.id.btn_set_date);
        btnAdd = findViewById(R.id.btn_event_add);

        selectedImage = findViewById(R.id.imagen_evento);

        rdbtn_limpio = findViewById(R.id.radiobtn_limpio);
        rdbtn_sucio = findViewById(R.id.radiobtn_sucio);
        rdbtn_critico = findViewById(R.id.radiobtn_critico);

        eventManagerService = new EventManagerService();
        locationService = new LocationService(this);
        ////////////////// Intent
        txtTitle = findViewById(R.id.txt_add_event_title);
        txtDescription = findViewById(R.id.txt_add_description);
        eventManagerService = new EventManagerService();
        Intent i = getIntent();
        double lat = i.getDoubleExtra("latitude", 0);
        double lon = i.getDoubleExtra("longitude", 0);
        if (lat != 0 && lon != 0) {
            eventLatLng = new LatLng(lat, lon);
            btnSelectLocation.setText(locationService.localityName(lat, lon));
        }
        ArrayList<String> lista = new ArrayList<>();
        lista.add("Limpio");
        lista.add("Sucio");
        lista.add("Critico");

        rdbtn_limpio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rdbtn_sucio.setChecked(false);
                rdbtn_critico.setChecked(false);
                rdbtn_limpio.setChecked(isChecked);
                sucio = false;
                critico = false;
                limpio = true;
            }
        });

        rdbtn_sucio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rdbtn_limpio.setChecked(false);
                rdbtn_critico.setChecked(false);
                rdbtn_sucio.setChecked(isChecked);
                sucio = true;
                critico = false;
                limpio = false;
            }
        });

        rdbtn_critico.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rdbtn_sucio.setChecked(false);
                rdbtn_limpio.setChecked(false);
                rdbtn_critico.setChecked(isChecked);
                sucio = false;
                critico = true;
                limpio = false;
            }
        });

        FrameLayout addEvent = findViewById(R.id.FrameLayout_add_event);
        btnSelectDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            mDateSetListener = new DatePickerDialog(AddEventActivity.this,
                    (view, year1, month1, dayOfMonth) ->
                            btnSelectDate.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1),
                    2018, month, day);
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

        selectedImage.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        });
        btnAdd.setOnClickListener(v -> {
            //TODO: Comprobar que se han insertado todos los datos
            String title = txtTitle.getText().toString();
            String description = txtDescription.getText().toString();
            double latitude = eventLatLng.latitude;
            double longitude = eventLatLng.longitude;
            eventManagerService.createEvent(new Event("", title, description, latitude, longitude, 0, false), imagePath);
            //TODO: Mostrar evento creado
            finish();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE) {
            if (data != null) {
                imagePath = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                    selectedImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setFrameAbierto(boolean frameAbierto) {
        this.frameAbierto = frameAbierto;
    }

    public void setEventLatLng(LatLng eventLatLng) {
        this.eventLatLng = eventLatLng;
    }

}
