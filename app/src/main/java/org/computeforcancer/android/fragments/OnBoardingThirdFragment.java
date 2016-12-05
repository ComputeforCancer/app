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
public class OnBoardingThirdFragment extends AbstractBaseFragment {

    private static final String TAG = OnBoardingThirdFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.onboarding_donation, viewGroup, false);

        Button noThxB = (Button) mRootView.findViewById(R.id.od_no_thanks_b);
        noThxB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((CredentialInputActivity)getActivity()).setPagerVisibility(false);
                ((CredentialInputActivity)getActivity()).openPage(0, true);
            }
        });

        Button sureB = (Button) mRootView.findViewById(R.id.od_sure_b);
        sureB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((CredentialInputActivity)getActivity()).openFragment(new SignInFragment(), false);
                ((CredentialInputActivity)getActivity()).setPagerVisibility(false);
                ((CredentialInputActivity)getActivity()).openPage(0, false);
            }
        });

        return mRootView;
    }

    @Override
    public String getTAG() {
        return TAG;
    }
}
