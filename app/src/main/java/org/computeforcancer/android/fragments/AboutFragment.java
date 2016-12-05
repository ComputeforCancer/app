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
public class AboutFragment extends AbstractBaseFragment {

    private static final String TAG = AboutFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.about_layout, viewGroup, false);
        return mRootView;
    }

    @Override
    public String getTAG() {
        return TAG;
    }
}
