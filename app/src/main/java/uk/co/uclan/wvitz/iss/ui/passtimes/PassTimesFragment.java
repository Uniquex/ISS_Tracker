package uk.co.uclan.wvitz.iss.ui.passtimes;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.co.uclan.wvitz.iss.R;

public class PassTimesFragment extends Fragment {

    private PassTimesViewModel mViewModel;

    public static PassTimesFragment newInstance() {
        return new PassTimesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pass_times_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PassTimesViewModel.class);
        // TODO: Use the ViewModel
    }

}
