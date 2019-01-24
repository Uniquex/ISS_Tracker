package uk.co.uclan.wvitz.iss;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.TextureView;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import uk.co.uclan.wvitz.iss.adapters.ObservationImageAdapter;

public class AddObservation extends AppCompatActivity {


    private EditText et_lat;
    private EditText et_lon;
    private EditText et_date;
    private EditText et_note;
    private Button btn_addImage;
    private TextureView textureView;
    private FloatingActionButton mImageFAB;

    private ArrayList<byte[]> images = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private ObservationImageAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private LocationManager locationManager;

    private final String TAG = "AddObservation";


    public final static int PICK_PHOTO_CODE = 1046;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_observation);
        setTitle(R.string.add_observation);

        this.et_lat = findViewById(R.id.lat);
        this.et_lon = findViewById(R.id.lon);
        this.et_date = findViewById(R.id.date);
        this.et_note = findViewById(R.id.note);
        this.btn_addImage = findViewById(R.id.btn_addImage);
        this.textureView = findViewById(R.id.textureView);

        mAdapter = new ObservationImageAdapter(images);

        mRecyclerView = findViewById(R.id.rv_images);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        btn_addImage.setOnClickListener(v -> {
            onPickPhoto();

        });


    }


    public void setDateTime() {
        Date date = new Date();
    }

    // Trigger gallery selection for a photo
    public void onPickPhoto() {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Uri photoUri = data.getData();
            // Do something with the photo based on Uri
            Bitmap selectedImage = null;
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                // Load the selected image into a preview
                this.images.add(this.getBytesFromBitmap(selectedImage));
                this.mAdapter.notifyDataSetChanged();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);

        return stream.toByteArray();
    }

}
