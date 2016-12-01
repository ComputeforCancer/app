package org.computeforcancer.android.fragments;

import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by Maksym Shpyl on 01.12.2016.
 */
public abstract class AbstractBaseFragment extends Fragment {

    protected View mRootView;

    public abstract String getTAG();

}
