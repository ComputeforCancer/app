package org.computeforcancer.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.computeforcancer.android.R;
import org.computeforcancer.android.attach.CredentialInputActivity;

/**
 * Created by Maksym Shpyl on 05.12.2016.
 */
public class TellMeMoreFragment extends AbstractBaseFragment {

    private static final String TAG = TellMeMoreFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.tell_me_more_layout, viewGroup, false);

        Button readyB = (Button) mRootView.findViewById(R.id.tmml_ready_b);
        readyB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((CredentialInputActivity)getActivity()).openPage(1, true);
            }
        });

        return mRootView;
    }

    @Override
    public String getTAG() {
        return TAG;
    }
}
