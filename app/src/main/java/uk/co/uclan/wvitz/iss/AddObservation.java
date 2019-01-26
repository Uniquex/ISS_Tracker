package uk.co.uclan.wvitz.iss;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.TextureView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.orm.SugarContext;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.PermissionChecker;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import uk.co.uclan.wvitz.iss.DT.Image;
import uk.co.uclan.wvitz.iss.DT.Observation;
import uk.co.uclan.wvitz.iss.adapters.ObservationImageAdapter;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class AddObservation extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {


    private EditText et_lat;
    private EditText et_lon;
    private EditText et_time;
    private EditText et_date;
    private EditText et_note;
    private Button btn_addImage;
    private Button btn_save;
    private TextureView textureView;
    private FloatingActionButton mImageFAB;

    private ArrayList<byte[]> images = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private ObservationImageAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private long timestamp;
    private int m_day, m_month, m_year, m_hour, m_minute;

    private LocationManager locationManager;

    private final String TAG = "AddObservation";

    private final int PICK_PHOTO_CODE = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_observation);
        setTitle(R.string.add_observation);
        SugarContext.init(this);

        this.et_lat = findViewById(R.id.lat);
        this.et_lon = findViewById(R.id.lon);
        this.et_date = findViewById(R.id.date);
        this.et_time = findViewById(R.id.time);
        this.et_note = findViewById(R.id.note);
        this.btn_addImage = findViewById(R.id.btn_addImage);
        this.btn_save = findViewById(R.id.btn_save);

        mAdapter = new ObservationImageAdapter(images);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);

        mRecyclerView = findViewById(R.id.rv_images);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        btn_addImage.setOnClickListener(v -> {
            onPickPhoto();

        });

        et_date.setOnTouchListener((v, event) -> {
            showDatePicker();
            return true;
        });

        et_time.setOnTouchListener((v, event) -> {
            showTimePicker();
            return true;
        });

        btn_save.setOnClickListener(v -> {
            this.saveData();
        });

        this.setCurrentLocation();
        this.setCurrentTime();
    }

    public void setCurrentTime() {
        Calendar now = Calendar.getInstance();
        this.m_year = now.get(Calendar.YEAR);
        this.m_month = now.get(Calendar.MONTH)+1;
        this.m_day = now.get(Calendar.DAY_OF_MONTH);
        this.m_hour = now.get(Calendar.HOUR);
        this.m_minute = now.get(Calendar.MINUTE);

        this.timestamp = System.currentTimeMillis();

        this.setTVDate(m_year, m_month, m_day);
        this.setTVTime(m_hour, m_minute);
    }

    public void setTVTime(int hour, int minute) {
        String time = hour+":"+ minute;

        this.m_hour = hour;
        this.m_minute = minute;
        this.setTimestamp();
        this.et_time.setText(time);
    }
    public void setTVDate(int year, int month, int day) {
        String fDate = day+"."+(month+1)+"."+year;

        this.m_year = year;
        this.m_month = month;
        this.m_day = day;
        this.setTimestamp();
        this.et_date.setText(fDate);

    }

    public void setTimestamp() {
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.DAY_OF_MONTH, m_day);
        cal.set(Calendar.MONTH, m_month-1);
        cal.set(Calendar.YEAR, m_year);
        cal.set(Calendar.MINUTE, m_minute);
        cal.set(Calendar.HOUR, m_hour);

        this.timestamp = cal.getTimeInMillis();
    }


    public void showTimePicker() {
        Calendar now = Calendar.getInstance();

        TimePickerDialog.OnTimeSetListener oTSL = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                setTVTime(hourOfDay, minute);
            }
        };

        TimePickerDialog tpd = new TimePickerDialog(AddObservation.this, oTSL, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
        tpd.show();
    }


    public void showDatePicker() {
        Calendar now = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener oDSL = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                setTVDate(year, month, dayOfMonth);
            }
        };

        DatePickerDialog dpd = new DatePickerDialog(AddObservation.this, oDSL, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        dpd.show();
    }

    // Trigger gallery selection for a photo
    public void onPickPhoto() {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Uri photoUri = data.getData();
            // Do something with the photo based on Uri
            Bitmap selectedImage = null;
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                // Load the selected image into a preview
                this.images.add(this.getBytesFromBitmap(selectedImage));
                this.mAdapter.notifyDataSetChanged();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);

        return stream.toByteArray();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Toast.makeText(this, "You picked the following date: "+dayOfMonth+"/"+(month+1)+"/"+year, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Toast.makeText(this, "You picked the following time: "+hourOfDay+"h"+minute+"m", Toast.LENGTH_SHORT).show();
    }

    public void setCurrentLocation() {
        if (PermissionChecker.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            try {
                Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                this.et_lat.setText(String.valueOf(loc.getLatitude()));
                this.et_lon.setText(String.valueOf(loc.getLongitude()));

            } catch (SecurityException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "No permission for location access", Toast.LENGTH_LONG);
        }


    }

    public void saveData() {
        Observation observation = new Observation(this.timestamp, this.et_lon.getText().toString(), this.et_lat.getText().toString() ,this.et_note.getText().toString());
        observation.save();

        for(int x = 0; x < images.size(); x++) {
            Image im = new Image(observation, images.get(x));
            im.save();
        }


        Intent myIntent = new Intent(this, Observations.class);
        startActivity(myIntent);
    }

}
