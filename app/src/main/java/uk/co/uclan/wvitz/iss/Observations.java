package uk.co.uclan.wvitz.iss;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.orm.SugarContext;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import uk.co.uclan.wvitz.iss.DT.Observation;
import uk.co.uclan.wvitz.iss.adapters.ObservationAdapter;

public class Observations extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    public final String TAG = "Observations";

    private ArrayList<Observation> mList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ObservationAdapter mAdapter;
    private FloatingActionButton mFAB;
    private TextView mNoObservations;

    static final int NEW_OBS = 1111;

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
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        this.setOnlickListeners();

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);


        readDBEntry();
        setSwipe();
    }

    public void setOnlickListeners() {
        mFAB.setOnClickListener(v -> {
            Intent myIntent = new Intent(v.getContext(), AddObservation.class);
            startActivityForResult(myIntent, NEW_OBS);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SugarContext.terminate();
    }

    public void readDBEntry() {

        try {
            List<Observation> observations = Select.from(Observation.class).orderBy("timestamp").list();
            List<Observation> observations2 = new ArrayList<>();
            observations2.addAll(observations);

            if (observations.size() == 0) {
                mRecyclerView.setVisibility(View.GONE);
                mNoObservations.setVisibility(View.VISIBLE);
            } else {
                mRecyclerView.setVisibility(View.VISIBLE);
                mNoObservations.setVisibility(View.GONE);
            }
            mList.clear();
            mList.addAll(observations);

            mAdapter.notifyAdapterDataSetChanged();

            Log.d(TAG, "Observations: " + String.valueOf(observations.size()));
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
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

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof ObservationAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
            String name = this.mList.get(viewHolder.getAdapterPosition()).getTimestampFormatted();

            // backup of removed item for undo purpose
            final Observation deletedItem = mList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            mAdapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.coordinator_layout), "Removed Observation from " + name, Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    mAdapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mAdapter != null) {
            this.readDBEntry();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mAdapter != null) {
            this.readDBEntry();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("Observations onActivityResult  " + requestCode + " " + resultCode);
        if (requestCode == NEW_OBS) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                if (mAdapter != null) {
                    System.out.println("----------------------------Intent received");
                    this.readDBEntry();
                }
            }
        }
    }
}
