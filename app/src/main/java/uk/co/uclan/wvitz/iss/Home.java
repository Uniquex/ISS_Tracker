package uk.co.uclan.wvitz.iss;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.card.MaterialCardView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.*;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.json.*;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import uk.co.uclan.wvitz.iss.DT.Astronaut;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private List<Astronaut> aList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AstronautAdapter aAdapter;
    private String lon;
    private String lat;
    private ConstraintLayout constraintLayout;
    private MapView mapView;
    private Button mapButton;
    private MaterialCardView mapCard;
    private MaterialCardView locCard;
    private MaterialCardView astroCard;
    private MaterialCardView navCard;
    private DrawerLayout drawer;

    private ConstraintSet mCSet1 = new ConstraintSet(); // create a Constraint Set
    private ConstraintSet mCSet2 = new ConstraintSet(); // create a Constraint Set


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoidW5pcXVleCIsImEiOiJjanFnbGk2cXQxdDBoNDNwdDhibnUzYXp0In0.njtICx6oW5PCpc7M8rlNzQ");
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //EDIT

        // Map View
        constraintLayout = findViewById(R.id.mainLayout);
        mapView = findViewById(R.id.mapView);

        mapCard = findViewById(R.id.CardLocMap);
        locCard = findViewById(R.id.CardLoc);
        astroCard = findViewById(R.id.CardAstro);
        navCard = findViewById(R.id.CardNav);

        mapButton = findViewById(R.id.mapButton);


        addAnimationOperations();

        // Astronauts

        aAdapter = new AstronautAdapter(aList);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        recyclerView = (RecyclerView) findViewById(R.id.rv_astronauts);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(aAdapter);

        getAstronatus();
        getIssLocation();

        // Button
        this.setOnClickListeners();

    }

    public void setOnClickListeners() {
        mapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                toggleMap();
            }
        });

        locCard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                toggleMap();
            }
        });

        navCard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), Facts.class);
                startActivity(myIntent);
            }
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

/*        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        switch (id) {
            case R.id.nav_passTimes: {
                Intent myIntent = new Intent(this, PassTimes.class);
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

            Log.i("Main", aList.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getAstronatus() {
        // http://api.open-notify.org/astros.json

        //this.aList.add(new Astronaut("Test", "Test2", "ISS"));
        List<Astronaut> aList2 = this.aList;

        HttpClient.get("http://api.open-notify.org/astros.json", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                setAstronatus(response);
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

    void setIssLocation(JSONObject obj) {
        TextView tv = (TextView) this.findViewById(R.id.TVlonlat);
        try {
            JSONObject ob = (JSONObject) obj.get("iss_position");
            this.lon = ob.get("longitude").toString();
            this.lat = ob.get("latitude").toString();
            tv.setText("Lon: " + this.lon + " " + "Lat: " + this.lat);
            this.setMapViewFocus(lon, lat);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void getIssLocation() {
        // http://api.open-notify.org/iss-now.json

        HttpClient.get("http://api.open-notify.org/iss-now.json", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                setIssLocation(response);
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

    void setMapViewFocus(String lon, String lat) {

        Double lonD = Double.parseDouble(lon);
        Double latD = Double.parseDouble(lat);

        LatLng latLng = new LatLng(latD, lonD);

        CameraPosition cpos = new CameraPosition.Builder()
                .target(latLng)
                .zoom(0)
                .bearing(0)
                .tilt(0)
                .build();


        // Convert SVG to Bitmap to display the ICON

        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_m_baseline_up);

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);


        Icon icon = IconFactory.getInstance(Home.this).fromBitmap(bitmap);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mapboxMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(icon)
                        .title(getString(R.string.draw_marker_options_title)));
                //                     .snippet(getString(R.string.draw_marker_options_snippet)));
                mapboxMap.setCameraPosition(cpos);
            }
        });
    }

    private void addAnimationOperations() {
        boolean set = false;

        mCSet1.clone(this, R.layout.content_home);
        mCSet2.clone(this, R.layout.content_home_alt);

    }

    public void toggleMap() {

        switch (mapCard.getVisibility()) {
            case VISIBLE:
                //mapCard.setVisibility(View.INVISIBLE);
                mCSet1.applyTo(constraintLayout);
                break;
            case INVISIBLE:
                //mapCard.setVisibility(View.VISIBLE);
                mCSet2.applyTo(constraintLayout);
            case GONE:
                mapCard.setVisibility(View.VISIBLE);
        }
    }
}
