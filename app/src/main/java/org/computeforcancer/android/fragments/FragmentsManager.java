package org.computeforcancer.android.fragments;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.inputmethod.InputMethodManager;

import org.computeforcancer.android.R;
import org.computeforcancer.android.attach.CredentialInputActivity;

/**
 * Created by Maksym Shpyl on 01.12.2016.
 */
public class FragmentsManager {

    private CredentialInputActivity mActivity;
    private AbstractBaseFragment mCurrentFragment;

    public FragmentsManager(CredentialInputActivity activity) {
        mActivity = activity;
        openFragment(new PreSignInFragment(), false);
    }

    public void openFragment(final AbstractBaseFragment fragment, final boolean addToStack) {
        closeKeyboard();
        mCurrentFragment = fragment;
        FragmentManager fm = mActivity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.cia_fragment_holder, fragment, fragment.getTAG());
        if (addToStack) {
            ft.addToBackStack(fragment.getTAG());
        }
        ft.commitAllowingStateLoss();
    }

    private boolean closeKeyboard() {
        if (mActivity == null)
            return false;
        if ((mActivity).getCurrentFocus() == null)
            return false;
        if ((mActivity).getCurrentFocus().getWindowToken() == null)
            return false;
        InputMethodManager inputManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        return inputManager.hideSoftInputFromWindow((mActivity).getCurrentFocus().getWindowToken(), 0);
    }

}
