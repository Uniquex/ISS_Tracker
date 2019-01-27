package uk.co.uclan.wvitz.iss.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import uk.co.uclan.wvitz.iss.DT.Observation;
import uk.co.uclan.wvitz.iss.R;

public class ObservationAdapter extends RecyclerView.Adapter<ObservationAdapter.MyViewHolder> {

    private List<Observation> observationList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView timestamp, lon, lat, note;
        public RecyclerView observationImages;
        public RelativeLayout viewBackground, viewForeground;

        public MyViewHolder(View view) {
            super(view);
            timestamp = view.findViewById(R.id.timestamp);
            lon = view.findViewById(R.id.lon);
            lat = view.findViewById(R.id.lat);
            note = view.findViewById(R.id.note);
            observationImages = view.findViewById(R.id.rv_observationImages);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
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
        Observation observation = observationList.get(position);
        holder.timestamp.setText(String.valueOf(observation.getTimestampFormatted()));
        holder.lat.setText(observation.getLatString());
        holder.lon.setText(observation.getLonString());
        holder.note.setText(observation.getNote());

        // ObservationImageAdapter adapter = new ObservationImageAdapter(imageList);
        ObservationImageAdapter adapter = new ObservationImageAdapter(observationList.get(position).getImagesFromContext());
        holder.observationImages.setAdapter(adapter);
        holder.observationImages.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(null, LinearLayoutManager.HORIZONTAL, false);
        holder.observationImages.setLayoutManager(layoutManager);
    }

    @Override
    public int getItemCount() {
        return observationList.size();
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