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
package org.computeforcancer.android;

import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle; 
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import org.computeforcancer.android.adapter.NavDrawerListAdapter;
import org.computeforcancer.android.adapter.NavDrawerListAdapter.NavDrawerItem;
import org.computeforcancer.android.attach.SelectionListActivity;
import org.computeforcancer.android.client.ClientStatus;
import org.computeforcancer.android.client.Monitor;
import org.computeforcancer.android.client.IMonitor;
import org.computeforcancer.android.fragments.AboutFragment;
import org.computeforcancer.android.fragments.AbstractBaseFragment;
import org.computeforcancer.android.fragments.MainFragment;
import org.computeforcancer.android.fragments.PreSignInFragment;
import org.computeforcancer.android.fragments.SignInFragment;
import org.computeforcancer.android.fragments.TellMeMoreFragment;
import org.computeforcancer.android.utils.BOINCDefs;
import org.computeforcancer.android.utils.CustomPopUpWindow;
import org.computeforcancer.android.utils.Logging;
import org.computeforcancer.android.utils.SharedPrefs;

import java.util.ArrayList;
import java.util.List;

public class BOINCActivity extends FragmentActivity implements CustomPopupMenu.OnMenuItemClickListener {
	
	public static IMonitor monitor;
	private Integer clientComputingStatus = -1;
	private Integer numberProjectsInNavList = 0;
	static Boolean mIsBound = false;
	private FrameLayout mHolder;
	private CustomPopUpWindow mPopupWindow;
	
	// app title (changes with nav bar selection)
	//private CharSequence mTitle;
	// nav drawer title
	//private CharSequence mDrawerTitle;
	
	//private DrawerLayout mDrawerLayout;
	//private ListView mDrawerList;
	//private ActionBarDrawerToggle mDrawerToggle;
	//private NavDrawerListAdapter mDrawerListAdapter;

	private ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        // This is called when the connection with the service has been established, getService returns 
	    	// the Monitor object that is needed to call functions.
	        monitor = IMonitor.Stub.asInterface(service);
		    mIsBound = true;
		    determineStatus();
	    }

	    public void onServiceDisconnected(ComponentName className) {
	    	// This should not happen
	        monitor = null;
		    mIsBound = false;

		    Log.e(Logging.TAG, "BOINCActivity onServiceDisconnected");
	    }
	};
	
	private BroadcastReceiver mClientStatusChangeRec = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context,Intent intent) {
			if(Logging.VERBOSE) Log.d(Logging.TAG, "BOINCActivity ClientStatusChange - onReceive()"); 
			determineStatus();
		}
	};
	private IntentFilter ifcsc = new IntentFilter("org.computeforcancer.android.clientstatuschange");
	
    @Override
    public void onCreate(Bundle savedInstanceState) {  
        if(Logging.DEBUG) Log.d(Logging.TAG, "BOINCActivity onCreate()"); 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		mHolder = (FrameLayout) findViewById(R.id.main_fragment_holder);
		openFragment(new MainFragment());
		showPopup();

        // setup navigation bar
		/*
        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
		mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// display view for selected nav drawer item
				dispatchNavBarOnClick(mDrawerListAdapter.getItem(position),false);
			}});

		mDrawerListAdapter = new NavDrawerListAdapter(getApplicationContext());
		mDrawerList.setAdapter(mDrawerListAdapter);*/
		// enabling action bar app icon and behaving it as toggle button
		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		//getSupportActionBar().setHomeButtonEnabled(true);
/*
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, //nav menu toggle icon
				R.string.app_name, // nav drawer open - description for accessibility
				R.string.app_name // nav drawer close - description for accessibility
		) {
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				supportInvalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(mDrawerTitle);
				mDrawerListAdapter.notifyDataSetChanged(); // force redraw of all items (adapter.getView()) in order to adapt changing icons or number of tasks/notices
				// calling onPrepareOptionsMenu() to hide action bar icons
				supportInvalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
*/

		// pre-select fragment
		// 1. check if explicitly requested fragment present
		// e.g. after initial project attach.
		/*int targetFragId = getIntent().getIntExtra("targetFragment", -1);
		
		// 2. if no explicit request, try to restore previous selection
		if(targetFragId < 0 && savedInstanceState != null)
			targetFragId = savedInstanceState.getInt("navBarSelectionId");
		
		NavDrawerItem item = null;
		if(targetFragId < 0) {
			// if non of the above, go to default
			item = mDrawerListAdapter.getItem(0);
		} else item = mDrawerListAdapter.getItemForId(targetFragId);
		
		if(item != null) dispatchNavBarOnClick(item, true);
		else if(Logging.WARNING) Log.w(Logging.TAG, "onCreate: fragment selection returned null");
*/
        //bind monitor service
        doBindService();
    }
    /*
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("navBarSelectionId", mDrawerListAdapter.selectedMenuId);
		super.onSaveInstanceState(outState);
	}
*/

	private void showPopup() {
		mPopupWindow = new CustomPopUpWindow(BOINCActivity.this, null);
	}


	@Override
	public void onBackPressed() {
		List<Fragment> fragments = getSupportFragmentManager().getFragments();
		if (fragments != null && fragments.size() != 0) {
			for (Fragment fragment : fragments) {
				if (fragment != null && fragment.isVisible() && (fragment instanceof PrefsFragment ||
						fragment instanceof AboutFragment)) {
					openFragment(new MainFragment());
					return;
				}
			}
		}
		super.onBackPressed();
	}

	public void showPopup(View v) {
		CustomPopupMenu popup = new CustomPopupMenu(this, v);
		popup.setOnMenuItemClickListener(this);
		MenuInflater inflater = popup.getMenuInflater();
		inflater.inflate(R.menu.compute_cancer_menu, popup.getMenu());
		popup.show();
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_item_settings:
				openFragment(new PrefsFragment());
				return true;
			case R.id.menu_item_about:
				openFragment(new AboutFragment());
				return true;
			default:
				return false;
		}
	}
	public void openFragment(final AbstractBaseFragment fragment) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.main_fragment_holder, fragment, fragment.getTAG());
		ft.commitAllowingStateLoss();
	}

	@Override
	protected void onDestroy() {
    	if(Logging.DEBUG) Log.d(Logging.TAG, "BOINCActivity onDestroy()");
	    doUnbindService();
	    super.onDestroy();
	}

	@Override
	protected void onNewIntent(Intent intent) {
        if(Logging.DEBUG) Log.d(Logging.TAG, "BOINCActivity onNewIntent()"); 
		// onNewIntent gets called if activity is brought to front via intent, but was still alive, so onCreate is not called again
		// getIntent always returns the intent activity was created of, so this method is the only hook to receive an updated intent
		// e.g. after (not initial) project attach
		super.onNewIntent(intent);
		// navigate to explicitly requested fragment (e.g. after project attach)
		//int id = intent.getIntExtra("targetFragment", -1);
    	//if(Logging.DEBUG) Log.d(Logging.TAG, "BOINCActivity onNewIntent() for target fragment: " + id);
    	//NavDrawerItem item = mDrawerListAdapter.getItemForId(id);
    	//if(item != null) dispatchNavBarOnClick(item,false);
    	//else if(Logging.WARNING) Log.w(Logging.TAG, "onNewIntent: requested target fragment is null, for id: " + id);
	}

	private BroadcastReceiver mDonatedTimeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			//Log.d("TEST", "onReceive");
			final long lastSession = intent.getLongExtra(Monitor.DONATION_LAST_SESSION, 0);
			final long overall = intent.getLongExtra(Monitor.DONATION_OVERALL, 0);
			final String email = intent.getStringExtra(Monitor.DONATION_EMAIL);

			SharedPreferences mSharedPreferences = getApplicationContext().getSharedPreferences("org.computeforcancer.android",
					Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);

			if (email != null && email.equals(mSharedPreferences.getString(SharedPrefs.CURRENT_EMAIL, ""))) {
				mSharedPreferences.edit().putLong(SharedPrefs.LAST_SESSION_TIME + mSharedPreferences.getString(SharedPrefs.CURRENT_EMAIL, ""), lastSession).commit();

				//Log.d("TEST", "put DONATION_TIME " + overall);
				mSharedPreferences.edit().putLong(SharedPrefs.DONATION_TIME + mSharedPreferences.getString(SharedPrefs.CURRENT_EMAIL, ""), overall).commit();

				List<Fragment> fragments = getSupportFragmentManager().getFragments();
				if (fragments != null && fragments.size() != 0) {
					for (Fragment fragment : fragments) {
						if (fragment != null && fragment.isVisible() && (fragment instanceof MainFragment)) {
							final Fragment mainFragment = fragment;
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									((MainFragment)mainFragment).updateUI(lastSession, overall);
								}
							});
							return;
						}
					}
				}
			}

		}
	};
	private IntentFilter mDonatedTimeFilter = new IntentFilter(Monitor.DONATION_TIME_MESSAGE);

	@Override
	protected void onResume() { // gets called by system every time activity comes to front. after onCreate upon first creation
	    super.onResume();
		registerReceiver(mClientStatusChangeRec, ifcsc);
		registerReceiver(mDonatedTimeReceiver, mDonatedTimeFilter);
	    determineStatus();
	}

	@Override
	protected void onPause() { // gets called by system every time activity loses focus.
    	if(Logging.VERBOSE) Log.v(Logging.TAG, "BOINCActivity onPause()");
	    super.onPause();
		unregisterReceiver(mClientStatusChangeRec);
		unregisterReceiver(mDonatedTimeReceiver);
	}

	private void doBindService() {
		// start service to allow setForeground later on...
		startService(new Intent(this, Monitor.class));
	    // Establish a connection with the service, onServiceConnected gets called when
		bindService(new Intent(this, Monitor.class), mConnection, Service.BIND_AUTO_CREATE);
	}

	private void doUnbindService() {
	    if (mIsBound) {
	        // Detach existing connection.
	        unbindService(mConnection);
	        mIsBound = false;
	    }
	}
	/*
	public IMonitor getMonitorService() {
		if(!mIsBound) if(Logging.WARNING) Log.w(Logging.TAG, "Fragment trying to obtain serive reference, but Monitor not bound in BOINCActivity");
		return monitor;
	}
	
	public void startAttachProjectListActivity() {
		if(Logging.DEBUG) Log.d(Logging.TAG, "BOINCActivity attempt to start ");
		startActivity(new Intent(this,SelectionListActivity.class));
	}
	*/
	/*
	 * React to selection of nav bar item
	 * @param item
	 * @param position
	 * @param init
	 */
	/*private void dispatchNavBarOnClick(NavDrawerItem item, boolean init) {
		// update the main content by replacing fragments
		if(item == null) {
			if(Logging.WARNING) Log.w(Logging.TAG, "dispatchNavBarOnClick returns, item null.");
			return;
		}
		if(Logging.DEBUG) Log.d(Logging.TAG, "dispatchNavBarOnClick for item with id: " + item.getId() + " title: " + item.getTitle() + " is project? " + item.isProjectItem());
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Boolean fragmentChanges = false;
		if(init) {
			// if init, setup status fragment
			ft.replace(R.id.status_container, new StatusFragment());
		}
		if(!item.isProjectItem()) {
			switch (item.getId()) {
			case R.string.tab_tasks:
				ft.replace(R.id.frame_container, new TasksFragment());
				fragmentChanges = true;
				break;
			case R.string.tab_notices:
				ft.replace(R.id.frame_container, new NoticesFragment());
				fragmentChanges = true;
				break;
			case R.string.tab_projects:
				ft.replace(R.id.frame_container, new ProjectsFragment());
				fragmentChanges = true;
				break;
	    	case R.string.menu_help:
	    		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://boinc.berkeley.edu/wiki/BOINC_Help"));
	    		startActivity(i);
	    		break;
	    	case R.string.menu_about:
				final Dialog dialog = new Dialog(this);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.dialog_about);
				Button returnB = (Button) dialog.findViewById(R.id.returnB);
				TextView tvVersion = (TextView)dialog.findViewById(R.id.version);
				try {
					tvVersion.setText(getString(R.string.about_version) + " "
							+ getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
				} catch (NameNotFoundException e) {if(Logging.WARNING) Log.w(Logging.TAG, "version name not found.");}
				
				returnB.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				dialog.show();
	    		break;
			case R.string.menu_eventlog:
				startActivity(new Intent(this,EventLogActivity.class));
				break;
			case R.string.projects_add:
				startActivity(new Intent(this, SelectionListActivity.class));
				break;
			case R.string.tab_preferences:
				ft.replace(R.id.frame_container, new PrefsFragment());
				fragmentChanges = true;
				break;
	
			default:
				if(Logging.ERROR) Log.d(Logging.TAG, "dispatchNavBarOnClick() could not find corresponding fragment for " + item.getTitle());
				break;
			}

		} else {
			// ProjectDetailsFragment. Data shown based on given master URL
			Bundle args = new Bundle();
			args.putString("url", item.getProjectMasterUrl());
			Fragment frag = new ProjectDetailsFragment();
			frag.setArguments(args);
			ft.replace(R.id.frame_container, frag);
			fragmentChanges = true;
		}

		mDrawerLayout.closeDrawer(mDrawerList);
		
		if(fragmentChanges) {
			ft.commit();
			setTitle(item.getTitle());
			mDrawerListAdapter.selectedMenuId = item.getId(); //highlight item persistently
			mDrawerListAdapter.notifyDataSetChanged(); // force redraw
		} 

		if(Logging.DEBUG) Log.d(Logging.TAG, "displayFragmentForNavDrawer() " + item.getTitle());
	}
    */
    // tests whether status is available and whether it changed since the last event.
	private void determineStatus() {
    	try {
			if(mIsBound) {
				ConnectivityManager conMngr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
				android.net.NetworkInfo wifi = conMngr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

				if (!wifi.isConnected()) {
					BOINCActivity.monitor.setAutostart(false);
					BOINCActivity.monitor.setRunMode(BOINCDefs.RUN_MODE_NEVER);
				} else {
					SharedPreferences mSharedPreferences = getApplicationContext().getSharedPreferences("org.computeforcancer.android",
							Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
					monitor.setAutostart(mSharedPreferences.getBoolean(SharedPrefs.AUTO_START, true));
					BOINCActivity.monitor.setRunMode(mSharedPreferences.getBoolean(SharedPrefs.AUTO_START, true)
							? BOINCDefs.RUN_MODE_AUTO : BOINCDefs.RUN_MODE_NEVER);
				}
				Integer newComputingStatus = monitor.getComputingStatus();
				if(newComputingStatus != clientComputingStatus) {
					// computing status has changed, update and invalidate to force adaption of action items
					clientComputingStatus = newComputingStatus;
					supportInvalidateOptionsMenu();
				}/*
				if(numberProjectsInNavList != monitor.getProjects().size())
					numberProjectsInNavList = mDrawerListAdapter.compareAndAddProjects((ArrayList<Project>)monitor.getProjects());
				//setAppTitle();
			*/}
    	} catch (Exception e) {}
    }

/*
    public final boolean onKeyDown(final int keyCode, final KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (this.mDrawerLayout.isDrawerOpen(this.mDrawerList)) {
                this.mDrawerLayout.closeDrawer(this.mDrawerList);
            } else {
                this.mDrawerLayout.openDrawer(this.mDrawerList);
            }
            return true;
        }
        return super.onKeyDown(keyCode, keyEvent);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    if(Logging.DEBUG) Log.d(Logging.TAG, "BOINCActivity onCreateOptionsMenu()");

	    MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
	    if(Logging.DEBUG) Log.d(Logging.TAG, "BOINCActivity onPrepareOptionsMenu()");
		
		// run mode, set title and icon based on status
		MenuItem runMode = menu.findItem(R.id.run_mode);
		if(clientComputingStatus == ClientStatus.COMPUTING_STATUS_NEVER) {
			// display play button
			runMode.setTitle(R.string.menu_run_mode_enable);
			runMode.setIcon(R.drawable.playw);
		} else {
			// display stop button
			runMode.setTitle(R.string.menu_run_mode_disable);
			runMode.setIcon(R.drawable.pausew);
		}
		
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if(Logging.DEBUG) Log.d(Logging.TAG, "BOINCActivity onOptionsItemSelected()");

	    // toggle drawer
	    if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

	    switch (item.getItemId()) {
			case R.id.run_mode:
				if(item.getTitle().equals(getApplication().getString(R.string.menu_run_mode_disable))) {
					if(Logging.DEBUG) Log.d(Logging.TAG,"run mode: disable");
					new WriteClientModeAsync().execute(BOINCDefs.RUN_MODE_NEVER);
				} else if (item.getTitle().equals(getApplication().getString(R.string.menu_run_mode_enable))) {
					if(Logging.DEBUG) Log.d(Logging.TAG,"run mode: enable");
					new WriteClientModeAsync().execute(BOINCDefs.RUN_MODE_AUTO);
				} else if(Logging.DEBUG) Log.d(Logging.TAG,"run mode: unrecognized command");
				return true;
			case R.id.projects_add:
				startActivity(new Intent(this, SelectionListActivity.class));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}
*/
	private boolean currentState;

	public void stateChanged(boolean run) {
		if (run) {
			new WriteClientModeAsync().execute(BOINCDefs.RUN_MODE_AUTO);
		} else {
			new WriteClientModeAsync().execute(BOINCDefs.RUN_MODE_NEVER);
		}
	}

	private final class WriteClientModeAsync extends AsyncTask<Integer, Void, Boolean> {
		
		@Override
		protected Boolean doInBackground(Integer... params) {
			// setting provided mode for both, CPU computation and network.
			Boolean runMode;
			try {
				runMode = monitor.setRunMode(params[0]);
			} catch (RemoteException e) {
				runMode = false;
			}
			Boolean networkMode;
			try {
				networkMode = monitor.setNetworkMode(params[0]);
			} catch (RemoteException e) {
				networkMode = false;
			}
			return runMode && networkMode;
		}
		
		@Override
		protected void onPostExecute(Boolean success) {
			if(success)
				try {
					monitor.forceRefresh();
				} catch (RemoteException e) {}
			else if(Logging.WARNING) Log.w(Logging.TAG,"setting run and network mode failed");
		}
	}
}
