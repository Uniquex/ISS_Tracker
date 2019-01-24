package uk.co.uclan.wvitz.iss;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.orm.SugarContext;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import uk.co.uclan.wvitz.iss.DT.Observation;
import uk.co.uclan.wvitz.iss.adapters.ObservationAdapter;

public class Observations extends AppCompatActivity {
    public final String TAG = "Observations";

    private ArrayList<Observation> mList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ObservationAdapter mAdapter;
    private FloatingActionButton mFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observations);
        SugarContext.init(this);

        setTitle(R.string.observations);


        mFAB = findViewById(R.id.fab);

        mAdapter = new ObservationAdapter(mList);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        this.mRecyclerView = findViewById(R.id.rv_observations);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        this.setOnlickListeners();

        readDBEntry();

    }

    public void setOnlickListeners() {
        mFAB.setOnClickListener(v -> {
            Intent myIntent = new Intent(v.getContext(), AddObservation.class);
            startActivity(myIntent);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SugarContext.terminate();
    }

    public void createDBEntry() {
        Long timestamp = System.currentTimeMillis();
        Observation observation = new Observation(timestamp, "-93.3616", "50.9325", "Lorem ipsum at el dol met", null);
        observation.save();
    }

    public void readDBEntry() {

        List<Observation> observations = Observation.listAll(Observation.class);

        mList.addAll(observations);
        mAdapter.notifyDataSetChanged();

        Log.d(TAG, String.valueOf(observations.size()));
    }


}
