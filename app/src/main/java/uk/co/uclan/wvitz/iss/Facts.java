package uk.co.uclan.wvitz.iss;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import uk.co.uclan.wvitz.iss.ui.facts.FactsFragment;

public class Facts extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facts_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, FactsFragment.newInstance())
                    .commitNow();
        }
    }
}
