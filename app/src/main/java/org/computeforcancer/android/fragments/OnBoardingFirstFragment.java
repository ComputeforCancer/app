package org.computeforcancer.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.computeforcancer.android.R;
import org.computeforcancer.android.attach.CredentialInputActivity;

/**
 * Created by Maksym Shpyl on 02.12.2016.
 */
public class OnBoardingFirstFragment extends AbstractBaseFragment {

    private static final String TAG = OnBoardingFirstFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.onboarding_layout, viewGroup, false);

        Button doItB = (Button) mRootView.findViewById(R.id.ol_do_it_b);
        doItB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((CredentialInputActivity)getActivity()).openPage(1);
            }
        });

        return mRootView;
    }

    @Override
    public String getTAG() {
        return TAG;
    }
}
