package uk.co.uclan.wvitz.iss;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.orm.SugarContext;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;
import uk.co.uclan.wvitz.iss.DT.Image;
import uk.co.uclan.wvitz.iss.DT.Observation;
import uk.co.uclan.wvitz.iss.DT.PassTime;
import uk.co.uclan.wvitz.iss.adapters.ObservationImageAdapterAdd;
import uk.co.uclan.wvitz.iss.adapters.PassTimesAdapterObs;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class AddObservation extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {


    private EditText et_lat, et_lon, et_time, et_date, et_note;
    private TextView tvPT;
    private Button btn_addImage, btn_save;


    private ArrayList<Image> images = new ArrayList<>();
    private ArrayList<Image> toDelete = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private ObservationImageAdapterAdd mAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private RecyclerView mRecyclerViewPT;
    private PassTimesAdapterObs mAdapterPT;
    private LinearLayoutManager mLinearLayoutManagerPT;

    private MapView mapView;
    private ImageView inflateMap;
    private ConstraintLayout layout;

    private long timestamp;
    private int m_day, m_month, m_year, m_hour, m_minute;

    private LocationManager locationManager;

    private final String TAG = "AddObservation";

    private final int PICK_PHOTO_CODE = 123;

    private boolean edit = false;

    private Observation observation;

    private FusedLocationProviderClient mFusedLocationClient;

    private boolean inflated = false;

    private ConstraintSet mCSet1 = new ConstraintSet(); // create a Constraint Set
    private ConstraintSet mCSet2 = new ConstraintSet(); // create a Constraint Set


    private ArrayList<PassTime> passTimes = new ArrayList<PassTime>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoidW5pcXVleCIsImEiOiJjanFnbGk2cXQxdDBoNDNwdDhibnUzYXp0In0.njtICx6oW5PCpc7M8rlNzQ");
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
        this.mapView = findViewById(R.id.observationMap);
        this.layout = findViewById(R.id.cl_add_obs);
        this.tvPT = findViewById(R.id.tv_observationPT);
        this.inflateMap = findViewById(R.id.inflateMap);
        //this.inflateMap = (ImageViewCompat)(findViewById(R.id.inflateMap));

        mAdapter = new ObservationImageAdapterAdd(images);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);

        mRecyclerView = findViewById(R.id.rv_images);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //setPTRecycler();
        mRecyclerViewPT = findViewById(R.id.rv_passTimesObservation);


        mCSet1.clone(this, R.layout.content_add_observation);
        mCSet2.clone(this, R.layout.content_add_observation_alt);

        btn_addImage.setOnClickListener(v -> {
            //onPickPhoto();
            this.openImageSelector();

        });


        if (getIntent().hasExtra("observation")) {
            Bundle data = getIntent().getExtras();
            try {
                Observation obs = data.getParcelable("observation");
                setFields(obs);
                deactivateFields();
                this.edit = true;
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else {
            this.setCurrentLocation();
            this.setCurrentTime();
        }

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

        updateMap();
    }

    public void deactivateFields() {
        this.et_time.setFocusable(false);
        this.et_time.setEnabled(false);
        this.et_date.setFocusable(false);
        this.et_date.setEnabled(false);
        this.et_lon.setFocusable(false);
        this.et_lon.setEnabled(false);
        this.et_lat.setFocusable(false);
        this.et_lat.setEnabled(false);
    }


    public void getPassTimes() {
        if (et_lon != null && et_lat != null) {
            String query = "http://api.open-notify.org/iss-pass.json?lat=" + this.et_lat.getText().toString() + "&lon=" + this.et_lon.getText().toString() + "&n=5";

            HttpClient.get(query, null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    setPassTimes(response);
                    System.out.println(response);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Toast.makeText(getApplicationContext(), "cannot read json", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, java.lang.Throwable throwable, JSONObject response) {
                    Toast.makeText(getApplicationContext(), "no connection", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    void setPassTimes(JSONObject obj) {
        this.passTimes.clear();
        try {
            JSONArray jsonArray = (JSONArray) obj.get("response");

            for (int x = 0; x < jsonArray.length(); x++) {
                JSONObject job = jsonArray.getJSONObject(x);

                Double duratioD = ((Integer) job.get("duration")).doubleValue();
                Long risetimeL = ((Integer) job.get("risetime")).longValue() * 1000;

                int durationMin = (int) (duratioD % 3600) / 60;
                int durationSeconds = (int) (duratioD % 60);


                String duration = String.valueOf(durationMin) + ":" + String.valueOf(durationSeconds);

                Date date = new Date(risetimeL);
                // format of the date
                DateFormat df = SimpleDateFormat.getDateTimeInstance();
                String java_date = df.format(date);
                this.passTimes.add(new PassTime(duration, java_date));
                System.out.println("added nw PT");

            }

            Log.d(TAG, "setPassTimes: Set items");

            mAdapterPT.notifyDataSetChanged();


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException np) {
            np.printStackTrace();
        }
    }

    public void setPTRecycler() {
        if (mAdapterPT == null) {
            mAdapterPT = new PassTimesAdapterObs(this.passTimes);

            mLinearLayoutManagerPT = new LinearLayoutManager(this);
            mLinearLayoutManagerPT.setOrientation(RecyclerView.VERTICAL);


            mRecyclerViewPT.setHasFixedSize(true);
            mRecyclerViewPT.setLayoutManager(mLinearLayoutManagerPT);
            mRecyclerViewPT.addItemDecoration(new DividerItemDecoration(this, mLinearLayoutManagerPT.getOrientation()));
            mRecyclerViewPT.setAdapter(mAdapterPT);
        }
    }

    public void inflateMap(View v) {
        if (this.inflated) {
            mCSet1.applyTo(layout);
            this.mRecyclerViewPT.setVisibility(INVISIBLE);
            this.tvPT.setVisibility(INVISIBLE);
            this.inflateMap.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_m_baseline_arrow_down));
            this.inflated = false;
        } else {
            mCSet2.applyTo(layout);
            this.mRecyclerViewPT.setVisibility(VISIBLE);
            this.tvPT.setVisibility(VISIBLE);
            this.inflateMap.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_m_baseline_arrow_up));
            getPassTimes();
            setPTRecycler();
            this.inflated = true;
        }
    }

    public void setFields(Observation observation) {
        Calendar ts = Calendar.getInstance();
        ts.setTimeInMillis(observation.getTimestamp());

        //List<Observation> obs = Select.from(Observation.class).list();
        Log.i(TAG, String.valueOf(observation.getIdentifier()));
        Observation observ = Select.from(Observation.class).where(Condition.prop("id").eq(observation.getIdentifier())).first();

        this.observation = observ;
        this.timestamp = this.observation.getTimestamp();

        this.et_note.setText(this.observation.getNote());
        this.et_lon.setText(this.observation.getLonFormatted());
        this.et_lat.setText(this.observation.getLatFormatted());
        this.setTVTime(ts.get(Calendar.HOUR), ts.get(Calendar.MINUTE));
        this.setTVDate(ts.get(Calendar.YEAR), ts.get(Calendar.MONTH), ts.get(Calendar.DAY_OF_MONTH));


        new Thread(() -> {
            {
                List<Image> list = this.observation.getImagesCFromContext();
                if (list.size() != 0) {
                    images.addAll(list);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }).start();


    }

    private void updateMap() {

        Icon iconIss = convertDrawableToIcon(ContextCompat.getDrawable(this, R.drawable.ic_m_baseline_up), false);


        if (!et_lat.getText().toString().isEmpty() && !et_lat.getText().toString().isEmpty()) {
            LatLng latLng = new LatLng(Double.parseDouble(this.et_lat.getText().toString()), Double.parseDouble(this.et_lon.getText().toString()));
            CameraPosition cpos = new CameraPosition.Builder().target(latLng).zoom(11).bearing(0).tilt(0).build();

            mapView.getMapAsync((mapboxMap) -> {
                mapboxMap.removeAnnotations();
                mapboxMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(iconIss)
                        .title(getString(R.string.draw_marker_options_title)));
                mapboxMap.setCameraPosition(cpos);
            });
        }
    }

    public Icon convertDrawableToIcon(Drawable drawable, boolean secondary) {
        Bitmap bitmap;
        if (secondary) {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth() / 2,
                    drawable.getIntrinsicHeight() / 2, Bitmap.Config.ARGB_8888);

        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth() * 2,
                    drawable.getIntrinsicHeight() * 2, Bitmap.Config.ARGB_8888);
        }


        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);


        return IconFactory.getInstance(AddObservation.this).fromBitmap(bitmap);
    }


    public void setCurrentTime() {
        Calendar now = Calendar.getInstance();
        this.m_year = now.get(Calendar.YEAR);
        this.m_month = now.get(Calendar.MONTH) + 1;
        this.m_day = now.get(Calendar.DAY_OF_MONTH);
        this.m_hour = now.get(Calendar.HOUR);
        this.m_minute = now.get(Calendar.MINUTE);

        this.timestamp = System.currentTimeMillis();

        this.setTVDate(m_year, m_month, m_day);
        this.setTVTime(m_hour, m_minute);
    }

    public void setTVTime(int hour, int minute) {
        String time = hour + ":" + minute;

        this.m_hour = hour;
        this.m_minute = minute;
        this.setTimestamp();
        this.et_time.setText(time);
    }

    public void setTVDate(int year, int month, int day) {
        String fDate = day + "." + (month + 1) + "." + year;

        this.m_year = year;
        this.m_month = month;
        this.m_day = day;
        this.setTimestamp();
        this.et_date.setText(fDate);

    }

    public void setTimestamp() {
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.DAY_OF_MONTH, m_day);
        cal.set(Calendar.MONTH, m_month - 1);
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

    public byte[] compressImage(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Toast.makeText(this, "You picked the following date: " + dayOfMonth + "/" + (month + 1) + "/" + year, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Toast.makeText(this, "You picked the following time: " + hourOfDay + "h" + minute + "m", Toast.LENGTH_SHORT).show();
    }

    public void onLocationChange(Location location) {
        this.et_lat.setText(String.valueOf(location.getLatitude()));
        this.et_lon.setText(String.valueOf(location.getLongitude()));
        updateMap();
    }

    public void setCurrentLocation() {
        if (PermissionChecker.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    onLocationChange(location);
                }
            });

            } else{
                Toast.makeText(this, "No permission for location access", Toast.LENGTH_LONG).show();
            }


        }

        public void saveData () {

            if (edit) {
                this.observation.setLatitude(et_lat.getText().toString());
                this.observation.setLongitude(et_lon.getText().toString());
                this.observation.setNote(et_note.getText().toString());
                this.observation.setTimestamp(this.timestamp);

                Observation.update(this.observation);

                for (int x = 0; x < images.size(); x++) {
                    if (images.get(x).getObservation() == null) {
                        Image im = new Image(this.observation, images.get(x).getImage());
                        im.save();
                        images.remove(x);
                        x--;
                    }
                }

                this.toDelete = mAdapter.getDeleteList();

                for (int i = 0; i < toDelete.size(); i++) {
                    Image.delete(toDelete.get(i));
                }

            } else {

                Observation observation = new Observation(this.timestamp, this.et_lon.getText().toString(), this.et_lat.getText().toString(), this.et_note.getText().toString());
                observation.save();

                for (int x = 0; x < images.size(); x++) {
                    Image im = new Image(observation, images.get(x).getImage());
                    im.save();
                }
            }

            Intent returnIntent = new Intent();
            this.setResult(RESULT_OK, returnIntent);

            this.finish();

        }

        public void openImageSelector () {
            ImagePicker.create(this).start();
        }

        @Override
        protected void onActivityResult ( int requestCode, final int resultCode, Intent data){

            if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {

                List<com.esafirm.imagepicker.model.Image> images = ImagePicker.getImages(data);
                for (int x = 0; x < images.size(); x++) {

                    this.images.add(new Image(null, this.compressImage(images.get(x).getPath())));

                }
                this.mAdapter.notifyDataSetChanged();
            }
            super.onActivityResult(requestCode, resultCode, data);
        }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mapView.onDestroy();
    }
}
