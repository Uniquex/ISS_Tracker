package uk.co.uclan.wvitz.iss;

import androidx.appcompat.app.AppCompatActivity;
import uk.co.uclan.wvitz.iss.DT.Image;
import uk.co.uclan.wvitz.iss.DT.Observation;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

public class SettingsActivity extends AppCompatActivity {

    MaterialButton btn_deleteAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btn_deleteAll = findViewById(R.id.btn_deleteAll);
        setTitle("Settings");

        btn_deleteAll.setOnClickListener((view) -> {
            Observation.deleteAll(Observation.class);
            Image.deleteAll(Image.class);
        });

        Toast.makeText(this, "Successfull deleted entries", Toast.LENGTH_LONG);
    }
}
