/*******************************************************************************
 * This file is part of BOINC.
 * http://boinc.berkeley.edu
 * Copyright (C) 2012 University of California
 * 
 * BOINC is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * 
 * BOINC is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with BOINC.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.computeforcancer.android.attach;

import java.util.ArrayList;
import org.computeforcancer.android.R;
import org.computeforcancer.android.fragments.AbstractBaseFragment;
import org.computeforcancer.android.fragments.OnBoardingFirstFragment;
import org.computeforcancer.android.fragments.OnBoardingSecondFragment;
import org.computeforcancer.android.fragments.OnBoardingThirdFragment;
import org.computeforcancer.android.fragments.PreSignInFragment;
import org.computeforcancer.android.fragments.SignInFragment;
import org.computeforcancer.android.utils.*;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

public class CredentialInputActivity extends FragmentActivity {

	private ProjectAttachService attachService = null;
	private boolean asIsBound = false;
	//private AbstractBaseFragment mCurrentFragment;
	private String mEmail, mName;
	private FrameLayout mFrameHolder;
	private ViewPager mViewPager;
	private PagerAdapter mPagerAdapter;
	private boolean isPagerVisible;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);
        if(Logging.DEBUG) Log.d(Logging.TAG, "CredentialInputActivity onCreate"); 
        doBindService();
        setContentView(R.layout.credential_input_activity);
		mFrameHolder = (FrameLayout)findViewById(R.id.cia_fragment_holder);
		mViewPager = (ViewPager)findViewById(R.id.cia_view_pager);
		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mPagerAdapter);
		setPagerVisibility(false);

		openFragment(new PreSignInFragment(), false);
        
        /*CheckBox showPwdCb = (CheckBox) findViewById(R.id.show_pwd_cb);
        showPwdCb.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(((CheckBox) v).isChecked()) {
					pwdET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
				} else {
					pwdET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
					pwdET.setTransformationMethod(PasswordTransformationMethod.getInstance());
				}
			}
        });*/
    }

	public void setPagerVisibility(boolean setVisible) {
		isPagerVisible = setVisible;
		if (setVisible) {
			mFrameHolder.setVisibility(View.GONE);
			mViewPager.setVisibility(View.VISIBLE);
		} else {
			mFrameHolder.setVisibility(View.VISIBLE);
			mViewPager.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onDestroy() {
		doUnbindService();
		super.onDestroy();
	}

	public void signIn(String email, String user, String pwd) {
        if(Logging.DEBUG) Log.d(Logging.TAG, "CredentialInputActivity.continueClicked.");
        
		
		// set credentials in service
		if(asIsBound) {
	        // verfiy input, return if failed.
			if(!attachService.verifyInput(email, user, pwd)) return;
			// set credentials
			attachService.setCredentials(email, user, pwd);
		}
		else {
			if(Logging.ERROR) Log.e(Logging.TAG, "CredentialInputActivity.continueClicked: service not bound.");
			return;
		}

        if(Logging.DEBUG) Log.d(Logging.TAG, "CredentialInputActivity.continueClicked: starting BatchProcessingActivity...");
		startActivity(new Intent(this, BatchProcessingActivity.class));
	}

	public void openFragment(final AbstractBaseFragment fragment, final boolean addToStack) {
		closeKeyboard();
		//mCurrentFragment = fragment;
		if (fragment instanceof SignInFragment) {
			Bundle arguments = new Bundle();
			arguments.putString(SignInFragment.KEY_EMAIL, mEmail);
			arguments.putString(SignInFragment.KEY_NAME, mName);
			fragment.setArguments(arguments);
		}
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.cia_fragment_holder, fragment, fragment.getTAG());
		if (addToStack) {
			ft.addToBackStack(fragment.getTAG());
		}
		ft.commitAllowingStateLoss();
	}

	private boolean closeKeyboard() {
		if (getCurrentFocus() == null)
			return false;
		if (getCurrentFocus().getWindowToken() == null)
			return false;
		InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		return inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
	}

	@Override
	public void onBackPressed() {
		if (isPagerVisible) {
			if (mViewPager.getCurrentItem() != 0) {
				mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
			} else {
				setPagerVisibility(false);
			}
		} else {
			super.onBackPressed();
		}
	}

	public void openPage(int page) {
		mViewPager.setCurrentItem(page);
	}

	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(final int position) {
			switch (position) {
				case 0:
					return new OnBoardingFirstFragment();
				case 1:
					return new OnBoardingSecondFragment();
				case 2:
					return new OnBoardingThirdFragment();
			}
			return null;
		}

		@Override
		public int getCount() {
			return 3;
		}

	}
	// triggered by individual button
	/*
	public void individualClicked(View v) {
        if(Logging.DEBUG) Log.d(Logging.TAG, "CredentialInputActivity.individualClicked.");

		// set credentials in service, in case user typed before deciding btwn batch and individual attach
		if(asIsBound) attachService.setCredentials(emailET.getText().toString(), nameET.getText().toString(), pwdET.getText().toString());
		
		//startActivity(new Intent(this, IndividualAttachActivity.class));
		Intent intent = new Intent(this, BatchConflictListActivity.class);
		intent.putExtra("conflicts", false);
		startActivity(new Intent(this, BatchConflictListActivity.class));
	}*/
	
	private ServiceConnection mASConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        // This is called when the connection with the service has been established, getService returns 
	    	// the Monitor object that is needed to call functions.
	        attachService = ((ProjectAttachService.LocalBinder)service).getService();
		    asIsBound = true;
		    
		    ArrayList<String> values = attachService.getUserDefaultValues();
			mEmail = values.get(0);
			mName = values.get(1);
	        //emailET.setText(values.get(0));
	        //nameET.setText(values.get(1));
	    }

	    public void onServiceDisconnected(ComponentName className) {
	    	// This should not happen
	    	attachService = null;
	    	asIsBound = false;
	    }
	};
	
	private void doBindService() {
		// bind to attach service
		bindService(new Intent(this, ProjectAttachService.class), mASConnection, Service.BIND_AUTO_CREATE);
	}

	private void doUnbindService() {
	    if (asIsBound) {
	        // Detach existing connection.
	        unbindService(mASConnection);
	        asIsBound = false;
	    }
	}
}
