package org.computeforcancer.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.computeforcancer.android.R;
import org.computeforcancer.android.attach.CredentialInputActivity;

/**
 * Created by Maksym Shpyl on 01.12.2016.
 */
public class PreSignInFragment extends AbstractBaseFragment {

    private static final String TAG = PreSignInFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.pre_sign_layout, viewGroup, false);

        Button signInButton = (Button) mRootView.findViewById(R.id.psl_sign_in_b);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CredentialInputActivity)getActivity()).openFragment(new SignInFragment(), false);
            }
        });

        Button getStarted = (Button) mRootView.findViewById(R.id.psl_get_started_b);
        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CredentialInputActivity)getActivity()).setPagerVisibility(true);
            }
        });

        return mRootView;
    }

    @Override
    public String getTAG() {
        return TAG;
    }
}
