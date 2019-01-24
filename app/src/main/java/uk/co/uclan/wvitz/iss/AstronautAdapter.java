package uk.co.uclan.wvitz.iss;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import uk.co.uclan.wvitz.iss.DT.Astronaut;

public class AstronautAdapter extends RecyclerView.Adapter<AstronautAdapter.MyViewHolder> {

    private List<Astronaut> astroList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView fname, lname;

        public MyViewHolder(View view) {
            super(view);
            fname = (TextView) view.findViewById(R.id.fname);
            lname = (TextView) view.findViewById(R.id.lname);
        }
    }


    public AstronautAdapter(List<Astronaut> astroList) {
        this.astroList = astroList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Astronaut astro = astroList.get(position);
        holder.fname.setText(astro.getFirstname());
        holder.lname.setText(astro.getLastname());
    }

    @Override
    public int getItemCount() {
        return astroList.size();
    }
}