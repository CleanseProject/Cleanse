package com.cleanseproject.cleanse.activities;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.asksira.bsimagepicker.BSImagePicker;
import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.dataClasses.Event;
import com.cleanseproject.cleanse.fragments.MapFragment;
import com.cleanseproject.cleanse.services.EventManagerService;
import com.cleanseproject.cleanse.services.LocationService;
import com.cleanseproject.cleanse.services.NotificationManager;
import com.google.android.gms.maps.model.LatLng;
import com.yalantis.ucrop.UCrop;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

public class AddEventActivity extends AppCompatActivity implements BSImagePicker.OnSingleImageSelectedListener {

    private LocationService locationService;
    private EventManagerService eventManagerService;
    private NotificationManager notificationManager;
    private DatePickerDialog mDateSetListener;
    private Button btnSelectDate;
    private Button btnSelectLocation;
    private Button btnSelectPic;
    private Button btnAdd;
    private ImageView imgExit, selectedImage;
    private Spinner spn_estado;
    private ImageView imgEstado;
    private EditText txtTitle, txtDescription;
    private RadioButton rdbtn_limpio;
    private RadioButton rdbtn_sucio;
    private RadioButton rdbtn_critico;
    private int selectedState = -1;
    private Uri imagePath;
    private boolean frameAbierto;
    private LatLng eventLatLng;
    private Toolbar toolbar;
    private FrameLayout addEvent;
    private long timeStamp;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_addevent_con_exit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.imgExitt:
                cerrar_ventanas();
                break;

        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        btnSelectLocation = findViewById(R.id.btn_set_location);
        btnSelectDate = findViewById(R.id.btn_set_date);
        btnAdd = findViewById(R.id.btn_event_add);
        selectedImage = findViewById(R.id.imagen_evento);
        rdbtn_limpio = findViewById(R.id.radiobtn_limpio);
        rdbtn_sucio = findViewById(R.id.radiobtn_sucio);
        rdbtn_critico = findViewById(R.id.radiobtn_critico);
        toolbar = findViewById(R.id.toolbar_addevent);
        toolbar.setTitle(R.string.add_new_event);
        setSupportActionBar(toolbar);
        eventManagerService = new EventManagerService();
        locationService = new LocationService(this);
        txtTitle = findViewById(R.id.txt_add_event_title);
        txtDescription = findViewById(R.id.txt_add_description);
        Intent i = getIntent();
        double lat = i.getDoubleExtra("latitude", 0);
        double lon = i.getDoubleExtra("longitude", 0);
        eventLatLng = new LatLng(lat, lon);
        if (lat != 0 && lon != 0)
            btnSelectLocation.setText(locationService.localityName(lat, lon));
        rdbtn_limpio.setOnCheckedChangeListener((buttonView, isChecked) -> {
            rdbtn_sucio.setChecked(false);
            rdbtn_critico.setChecked(false);
            rdbtn_limpio.setChecked(isChecked);
            selectedState = 0;
        });
        rdbtn_sucio.setOnCheckedChangeListener((buttonView, isChecked) -> {
            rdbtn_limpio.setChecked(false);
            rdbtn_critico.setChecked(false);
            rdbtn_sucio.setChecked(isChecked);
            selectedState = 1;
        });
        rdbtn_critico.setOnCheckedChangeListener((buttonView, isChecked) -> {
            rdbtn_sucio.setChecked(false);
            rdbtn_limpio.setChecked(false);
            rdbtn_critico.setChecked(isChecked);
            selectedState = 2;
        });
        addEvent = findViewById(R.id.FrameLayout_add_event);
        btnSelectDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            mDateSetListener = new DatePickerDialog(AddEventActivity.this,
                    (view, year1, month1, dayOfMonth) -> {
                        btnSelectDate.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1);
                        timeStamp = new GregorianCalendar(year1, month1, dayOfMonth).getTimeInMillis();
                    },
                    year, month, day);
            mDateSetListener.show();

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
            BSImagePicker singleSelectionPicker = new BSImagePicker.Builder("com.cleanseproject.fileprovider")
                    .build();
            singleSelectionPicker.show(getSupportFragmentManager(), "picker");
        });
        btnAdd.setOnClickListener(v -> {
            if (txtTitle.getText().toString().equals("") || eventLatLng == null || selectedState == -1 || btnSelectDate.getText().toString().equals("Select date")) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.missing_data)
                        .setMessage(R.string.fill_all_data)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        }).show();
            } else {
                String title = txtTitle.getText().toString();
                String description = txtDescription.getText().toString();
                double latitude = eventLatLng.latitude;
                double longitude = eventLatLng.longitude;
                eventManagerService.createEvent(
                        new Event("", title, description, latitude, longitude, 0, false, timeStamp, "", selectedState),
                        imagePath,
                        event -> {
                            Intent intent = new Intent(AddEventActivity.this, EventDetailsActivity.class);
                            intent.putExtra("Evento", event.getId());
                            startActivity(intent);
                        });
            }
        });
    }

    public void cerrar_ventanas() {
        if (frameAbierto) {
            addEvent.setVisibility(View.GONE);
            frameAbierto = false;
        } else {
            finish();
        }

    }

    public void setFrameAbierto(boolean frameAbierto) {
        this.frameAbierto = frameAbierto;
    }

    public void setEventLatLng(LatLng eventLatLng) {
        this.eventLatLng = eventLatLng;
    }

    @Override
    public void onSingleImageSelected(Uri uri, String tag) {
        String tempPath = AddEventActivity.this.getCacheDir().toURI().toString() + UUID.randomUUID();
        UCrop.Options options = new UCrop.Options();
        options.setActiveWidgetColor(getResources().getColor(R.color.colorAccent));
        UCrop.of(uri, Uri.parse(tempPath))
                .withOptions(options)
                .withAspectRatio(16, 9)
                .start(AddEventActivity.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            imagePath = UCrop.getOutput(data);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                selectedImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            UCrop.getError(data).printStackTrace();
        }
    }

}
