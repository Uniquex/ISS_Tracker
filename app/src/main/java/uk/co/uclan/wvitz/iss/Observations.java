package uk.co.uclan.wvitz.iss;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.orm.SugarContext;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import uk.co.uclan.wvitz.iss.DT.Image;
import uk.co.uclan.wvitz.iss.DT.Observation;
import uk.co.uclan.wvitz.iss.adapters.ObservationAdapter;

public class Observations extends AppCompatActivity {
    public final String TAG = "Observations";

    private ArrayList<Observation> mList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ObservationAdapter mAdapter;
    private FloatingActionButton mFAB;
    private TextView mNoObservations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observations);
        SugarContext.init(this);

        setTitle(R.string.observations);


        mFAB = findViewById(R.id.fab);
        mNoObservations = findViewById(R.id.tv_EmptyMessage);

        mAdapter = new ObservationAdapter(mList);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        this.mRecyclerView = findViewById(R.id.rv_observations);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        this.setOnlickListeners();

        readDBEntry();
        setSwipe();

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

    public void readDBEntry() {

        List<Observation> observations = Select.from(Observation.class).orderBy("timestamp").list();

        Collections.sort(observations, new Comparator<Observation>() {

            public int compare(Observation o1, Observation o2) {
                long a = o1.getTimestamp(), b = o2.getTimestamp();
                return a > b ? -1
                        : a < b ? 1
                        : 0;
            }
        });

        if(observations.size() == 0) {
            mRecyclerView.setVisibility(View.GONE);
            mNoObservations.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mNoObservations.setVisibility(View.GONE);
        }

        mList.addAll(observations);
        mAdapter.notifyDataSetChanged();

        Log.d(TAG, "Observations: "+String.valueOf(observations.size()));
    }

    public void setSwipe() {
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Row is swiped from recycler view
                // remove it from adapter
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                // view the background view
            }
        };

// attaching the touch helper to recycler view
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);
    }


}
