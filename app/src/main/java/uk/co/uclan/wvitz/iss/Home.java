package uk.co.uclan.wvitz.iss;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;


import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.loopj.android.http.*;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.orm.SchemaGenerator;
import com.orm.SugarContext;
import com.orm.SugarDb;

import org.json.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;
import uk.co.uclan.wvitz.iss.DT.Astronaut;
import uk.co.uclan.wvitz.iss.adapters.AstronautAdapter;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "main";

    private List<Astronaut> aList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AstronautAdapter aAdapter;
    private ConstraintLayout constraintLayout;
    private MapView mapView;
    private Button mapButton, triviaButton;
    private MaterialCardView mapCard, locCard, astroCard, navCard;
    private DrawerLayout drawer;
    private TextView tvLonLat;

    private ArrayList<LatLng> pastLocations = new ArrayList<>();
    private ArrayList<MarkerOptions> markers;

    private ConstraintSet mCSet1 = new ConstraintSet(); // create a Constraint Set
    private ConstraintSet mCSet2 = new ConstraintSet(); // create a Constraint Set

    private boolean setFocus = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoidW5pcXVleCIsImEiOiJjanFnbGk2cXQxdDBoNDNwdDhibnUzYXp0In0.njtICx6oW5PCpc7M8rlNzQ");
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent myIntent = new Intent(v.getContext(), AddObservation.class);
            startActivity(myIntent);
        });

        // SUGAR
        SugarContext.init(this);
        SchemaGenerator schemaGenerator = new SchemaGenerator(this);
        schemaGenerator.createDatabase(new SugarDb(this).getDB());
        // DRAWER
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Map View
        constraintLayout = findViewById(R.id.mainLayout);
        mapView = findViewById(R.id.mapView);
        mapCard = findViewById(R.id.CardLocMap);
        locCard = findViewById(R.id.CardLoc);
        astroCard = findViewById(R.id.CardAstro);
        navCard = findViewById(R.id.CardNav);
        mapButton = findViewById(R.id.mapButton);
        triviaButton = findViewById(R.id.btn_trivia);
        tvLonLat = this.findViewById(R.id.TVlonlat);
        setLayout();

        // Astronauts
        aAdapter = new AstronautAdapter(aList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView = findViewById(R.id.rv_astronauts);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));
        recyclerView.setAdapter(aAdapter);

        getAstronatus();
        getIssLocation();

        // Buttons
        this.setOnClickListeners();

    }

    public void setOnClickListeners() {
        mapButton.setOnClickListener(view -> toggleMap());

        locCard.setOnClickListener(view -> toggleMap());

        navCard.setOnClickListener(view -> {
            Intent myIntent = new Intent(view.getContext(), TriviaFacts.class);
            startActivity(myIntent);
        });

        triviaButton.setOnClickListener(view -> {
            Intent myIntent = new Intent(view.getContext(), TriviaFacts.class);
            startActivity(myIntent);
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent myIntent = new Intent(this, SettingsActivity.class);
            startActivity(myIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_passTimes: {
                Intent myIntent = new Intent(this, PassTimes.class);
                startActivity(myIntent);
                break;
            }
            case R.id.nav_observations: {
                Intent myIntent = new Intent(this, Observations.class);
                startActivity(myIntent);
                break;
            }
            case R.id.nav_settings: {
                Intent myIntent = new Intent(this, SettingsActivity.class);
                startActivity(myIntent);
                break;
            }
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void setAstronatus(JSONObject obj) {
        try {
            JSONArray crew = (JSONArray) obj.get("people");

            for (int x = 0; x < crew.length(); x++) {
                String name = (new JSONObject(crew.getString(x)).get("name")).toString();
                String fname, lname;
                fname = name.substring(0, name.indexOf(" "));
                lname = name.substring(name.indexOf(" "));
                this.aList.add(new Astronaut(fname, lname));

            }

            this.aAdapter.notifyDataSetChanged();
            TextView astroTitle = findViewById(R.id.astroTitle);
            astroTitle.setText(aList.size() + " " + astroTitle.getText());

            Log.i("Main", aList.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getAstronatus() {
        // http://api.open-notify.org/astros.json


        HttpClient.get("http://api.open-notify.org/astros.json", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                setAstronatus(response);
            }
        });
    }

    void setTvIssLoc(String lon,String lat) {
        this.tvLonLat.setText("Lon: " + lon + " " + "Lat: " + lat);
    }

    void setIssLocation(JSONObject obj) {
        try {
            JSONObject ob = (JSONObject) obj.get("iss_position");
            String lon = ob.get("longitude").toString();
            String lat = ob.get("latitude").toString();

            runOnUiThread(() ->
                this.setTvIssLoc(lon, lat)
            );



            this.setMapViewFocus(lon, lat);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void getIssLocation() {
        // http://api.open-notify.org/iss-now.json
            new Timer().scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {
                    SyncHttpClient client = new SyncHttpClient();
                    client.get("http://api.open-notify.org/iss-now.json", null, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            setIssLocation(response);
                        }
                    });
                }
            }, 0, 5000);
    }

    public Icon convertDrawableToIcon(Drawable drawable, boolean secondary) {
        Bitmap bitmap;
        if(secondary) {
             bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth()/2,
                    drawable.getIntrinsicHeight()/2, Bitmap.Config.ARGB_8888);

        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth()*2,
                    drawable.getIntrinsicHeight()*2, Bitmap.Config.ARGB_8888);
        }



        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);


        return IconFactory.getInstance(Home.this).fromBitmap(bitmap);
    }

    void setMapViewFocus(String lon, String lat) {
        try {
            LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
            if(!this.pastLocations.contains(latLng)) {
                this.pastLocations.add(latLng);
            }

            Log.i(TAG, latLng.toString() + "  " + pastLocations.size());

            if(!pastLocations.isEmpty()) {
                Icon iconpLoc = convertDrawableToIcon(ContextCompat.getDrawable(this, R.drawable.ic_m_baseline_arrow_drop_down_circle), true);

                this.markers = new ArrayList<>();
                for (int x = 0; x < pastLocations.size()-1; x++) {
                    markers.add(new MarkerOptions()
                            .position(this.pastLocations.get(x))
                            .icon(iconpLoc));
                }
            }

            runOnUiThread(() -> this.updateMap(latLng));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void updateMap(LatLng latLng) {

        Icon iconIss = convertDrawableToIcon(ContextCompat.getDrawable(this, R.drawable.ic_m_baseline_up), false);

        CameraPosition cpos = new CameraPosition.Builder().target(latLng).zoom(0).bearing(0).tilt(0).build();

        mapView.getMapAsync((mapboxMap) -> {
            mapboxMap.removeAnnotations();
            mapboxMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(iconIss)
                    .title(getString(R.string.draw_marker_options_title)));
            mapboxMap.addMarkers(markers);
            if(!setFocus) {
                mapboxMap.setCameraPosition(cpos);
                setFocus = true;
            }
        });
    }

    private void setLayout() {
        mCSet1.clone(this, R.layout.content_home);
        mCSet2.clone(this, R.layout.content_home_alt);
    }

    public void toggleMap() {
        switch (mapCard.getVisibility()) {
            case VISIBLE:
                mCSet1.applyTo(constraintLayout);
                break;
            case INVISIBLE:
                mCSet2.applyTo(constraintLayout);
            case GONE:
                mapCard.setVisibility(View.VISIBLE);
        }
    }
}
