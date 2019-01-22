package uk.co.uclan.wvitz.iss.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import uk.co.uclan.wvitz.iss.DT.Observation;
import uk.co.uclan.wvitz.iss.R;

public class ObservationAdapter extends RecyclerView.Adapter<ObservationAdapter.MyViewHolder> {

    private List<Observation> observationList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView timestamp, lon, lat, note;

        public MyViewHolder(View view) {
            super(view);
            timestamp = (TextView) view.findViewById(R.id.timestamp);
            lon = (TextView) view.findViewById(R.id.lon);
            lat = (TextView) view.findViewById(R.id.lat);
            note = (TextView) view.findViewById(R.id.note);
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
        holder.timestamp.setText(String.valueOf(observation.getTimestampFormated()));
        holder.lat.setText(observation.getLatString());
        holder.lon.setText(observation.getLonString());
        holder.note.setText(observation.getNote());
    }

    @Override
    public int getItemCount() {
        return observationList.size();
    }
}