package uk.co.uclan.wvitz.iss;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.PermissionChecker;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;
import uk.co.uclan.wvitz.iss.DT.PassTime;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class PassTimes extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private PassTimesAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private LocationManager locationManager;

    private List<PassTime> aList = new ArrayList<>();

    private String TAG = "PassTime";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_times);

        Log.d(TAG, "onCreateView: ");


        mAdapter = new PassTimesAdapter(aList);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        mRecyclerView = findViewById(R.id.rv_passTimes);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        getPassTimes();
    }

    void getPassTimes() {

        if (PermissionChecker.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            try {
                Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                String query = "http://api.open-notify.org/iss-pass.json?lat=" + loc.getLatitude() + "&lon=" + loc.getLongitude() + "&n=5";

                HttpClient.get(query, null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        setPassTimes(response);
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


            } catch (SecurityException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "No permission for location access", Toast.LENGTH_LONG);
        }


    }

    void setPassTimes(JSONObject obj) {
        this.aList.clear();
        try {
            JSONArray jsonArray = (JSONArray) obj.get("response");

            for (int x = 0; x < jsonArray.length(); x++) {
                JSONObject job = jsonArray.getJSONObject(x);

                Double duratioD = ((Integer)job.get("duration")).doubleValue();
                Long risetimeL = ((Integer)job.get("risetime")).longValue()*1000;

                int durationMin = (int) (duratioD%3600) / 60;
                int durationSeconds = (int) (duratioD % 60);


                String duration = String.valueOf(durationMin) + ":" + String.valueOf(durationSeconds);

                Date date = new Date(risetimeL);
                // format of the date
                DateFormat df = SimpleDateFormat.getDateTimeInstance();
                String java_date = df.format(date);
                this.aList.add(new PassTime(duration, java_date));

            }

            Log.d(TAG, "setPassTimes: Set items");

            mAdapter.notifyDataSetChanged();


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException np) {
            np.printStackTrace();
        }
    }



}
