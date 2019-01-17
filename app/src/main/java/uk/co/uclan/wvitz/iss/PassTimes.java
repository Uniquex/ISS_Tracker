package uk.co.uclan.wvitz.iss;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import uk.co.uclan.wvitz.iss.ui.passtimes.PassTimesFragment;

public class PassTimes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pass_times_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, PassTimesFragment.newInstance())
                    .commitNow();
        }
    }
}
