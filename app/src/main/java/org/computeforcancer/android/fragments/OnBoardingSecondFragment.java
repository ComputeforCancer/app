package org.computeforcancer.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.computeforcancer.android.R;

/**
 * Created by Maksym Shpyl on 02.12.2016.
 */
public class OnBoardingSecondFragment extends AbstractBaseFragment {

    private static final String TAG = OnBoardingSecondFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.onboarding_dont_worry_layout, viewGroup, false);

        return mRootView;
    }

    @Override
    public String getTAG() {
        return TAG;
    }
}
