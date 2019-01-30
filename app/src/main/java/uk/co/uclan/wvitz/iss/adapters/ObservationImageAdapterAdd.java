package uk.co.uclan.wvitz.iss.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import uk.co.uclan.wvitz.iss.DT.Image;
import uk.co.uclan.wvitz.iss.R;


public class ObservationImageAdapterAdd extends RecyclerView.Adapter<ObservationImageAdapterAdd.MyViewHolder> {

    private List<Image> imageList;
    private ArrayList<Image> toDelete;
    private final String TAG = "ObservationImageAdapterAdd";


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public View viewL;
        public MaterialButton btnRemove;

        public MyViewHolder(View view) {
            super(view);
            this.viewL = view;
            imageView = view.findViewById(R.id.iv_observation);
            btnRemove = view.findViewById(R.id.btn_remove);
            btnRemove.bringToFront();
        }

        public void setOnClick(Image image, ObservationImageAdapterAdd adapter) {
            btnRemove.setOnClickListener(v -> {
                Toast.makeText(v.getContext(), "Deleted Image", Toast.LENGTH_SHORT);
                adapter.deleteImage(image);
            });
        }
    }


    public ObservationImageAdapterAdd(List<Image> imageList) {
        this.imageList = imageList;
        this.toDelete = new ArrayList<>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_observation_image_add, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Image image = imageList.get(position);

        Log.i(TAG, "Adding image Array size " + imageList.size() );

        holder.setOnClick(image, this);

        Glide.with(holder.viewL).load(image.getImage()).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public ArrayList<Image> getDeleteList() {
        return this.toDelete;
    }

    private void deleteImage(Image image) {
        byte[] img = image.getImage();
        this.imageList.remove(image);
        toDelete.add(image);
        this.notifyDataSetChanged();
    }
}