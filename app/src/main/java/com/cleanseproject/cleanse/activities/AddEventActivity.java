package com.cleanseproject.cleanse.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.asksira.bsimagepicker.BSImagePicker;
import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.dataClasses.Event;
import com.cleanseproject.cleanse.fragments.MapFragment;
import com.cleanseproject.cleanse.services.EventManagerService;
import com.cleanseproject.cleanse.services.LocationService;
import com.google.android.gms.maps.model.LatLng;
import com.yalantis.ucrop.UCrop;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

public class AddEventActivity extends AppCompatActivity implements BSImagePicker.OnSingleImageSelectedListener {

    private EventManagerService eventManagerService;
    private DatePickerDialog mDateSetListener;
    private Button btnSelectDate;
    private ImageView selectedImage;
    private EditText txtTitle, txtDescription;
    private RadioButton rdbtn_limpio;
    private RadioButton rdbtn_sucio;
    private RadioButton rdbtn_critico;
    private int selectedState = -1;
    private Uri imagePath;
    private boolean frameAbierto;
    private LatLng eventLatLng;
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
                cerrarVentanas();
                break;

        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        Button btnSelectLocation = findViewById(R.id.btn_set_location);
        btnSelectDate = findViewById(R.id.btn_set_date);
        Button btnAdd = findViewById(R.id.btn_event_add);
        selectedImage = findViewById(R.id.imagen_evento);
        rdbtn_limpio = findViewById(R.id.radiobtn_limpio);
        rdbtn_sucio = findViewById(R.id.radiobtn_sucio);
        rdbtn_critico = findViewById(R.id.radiobtn_critico);
        Toolbar toolbar = findViewById(R.id.toolbar_addevent);
        toolbar.setTitle(R.string.add_new_event);
        setSupportActionBar(toolbar);
        eventManagerService = new EventManagerService();
        LocationService locationService = new LocationService(this);
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
            hideKeyboard();
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
            hideKeyboard();
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
                        .setPositiveButton(R.string.ok, (dialog, id) -> dialog.cancel()).show();
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
                            intent.putExtra("evento", event.getId());
                            startActivity(intent);
                        });
            }
        });
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void cerrarVentanas() {
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
        }
    }

}
