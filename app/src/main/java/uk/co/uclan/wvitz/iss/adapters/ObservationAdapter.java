package uk.co.uclan.wvitz.iss.adapters;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import uk.co.uclan.wvitz.iss.AddObservation;
import uk.co.uclan.wvitz.iss.DT.Observation;
import uk.co.uclan.wvitz.iss.R;

public class ObservationAdapter extends RecyclerView.Adapter<ObservationAdapter.MyViewHolder> {

    private List<Observation> observationList;
    private MyViewHolder viewHolder;
    private ObservationImageAdapter mAdapter;

    public void notifyAdapterDataSetChanged() {

        Collections.sort(this.observationList, (o1, o2) -> {
            long a = o1.getTimestamp(), b = o2.getTimestamp();
            return a > b ? -1
                    : a < b ? 1
                    : 0;
        });

        this.notifyDataSetChanged();
        if (mAdapter != null) {
            this.mAdapter.notifyDataSetChanged();
        }
    }

    public ObservationAdapter(List<Observation> observationList) {
        this.observationList = observationList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_observation, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        this.viewHolder = holder;
        Observation observation = observationList.get(position);
        holder.timestamp.setText(String.valueOf(observation.getTimestampFormatted()));
        holder.lat.setText(observation.getLatString());
        holder.lon.setText(observation.getLonString());
        holder.note.setText(observation.getNote());
        holder.onClick(observation);

        // ObservationImageAdapter adapter = new ObservationImageAdapter(imageList);
        this.mAdapter = new ObservationImageAdapter(observationList.get(position).getImagesFromContext());
        holder.observationImages.setAdapter(this.mAdapter);
        holder.observationImages.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(null, LinearLayoutManager.HORIZONTAL, false);
        holder.observationImages.setLayoutManager(layoutManager);
    }

    @Override
    public int getItemCount() {
        return observationList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView timestamp, lon, lat, note;
        public RecyclerView observationImages;
        public RelativeLayout viewBackground, viewForeground;
        public MaterialCardView observationC;
        public View view;
        public Button btnEdit, btnMap;

        public final String TAG = "ObservationAdapter";

        public MyViewHolder(View view) {
            super(view);
            timestamp = view.findViewById(R.id.timestamp);
            lon = view.findViewById(R.id.lon);
            lat = view.findViewById(R.id.lat);
            note = view.findViewById(R.id.note);
            observationImages = view.findViewById(R.id.rv_observationImages);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
            btnEdit = view.findViewById(R.id.btn_edit);
            btnMap = view.findViewById(R.id.btn_map);

            observationC = view.findViewById(R.id.cardObservation);
            this.view = view;
        }

        public void onClick(Observation observation) {
            this.btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent (this.view.getContext(), AddObservation.class);
                intent.putExtra("observation", observation);
                this.view.getContext().startActivity(intent);
            });

            this.btnMap.setOnClickListener(v -> {

                String uri = String.format(Locale.ENGLISH, "geo:0,0?q=%s,%s", observation.getLatFormatted(), observation.getLonFormatted());
                Log.i(TAG, uri);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                this.view.getContext().startActivity(intent);
            });
        }
    }

    public void removeItem(int position) {
        Observation observation = observationList.get(position);
        observationList.remove(position);
        Observation.delete(observation);
        notifyItemRemoved(position);
    }

    public void restoreItem(Observation item, int position) {
        observationList.add(position, item);
        Observation.save(item);
        notifyItemInserted(position);
    }
}