
package org.computeforcancer.android;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.computeforcancer.android.utils.CustomPopUpWindow;

public class TestActivity extends ActionBarActivity {
	private LinearLayout settingsButton;
	private ImageView stateButton;
	private RelativeLayout settingsMenu;
	private ImageView ivWifi;
	private ImageView ivBattery;
	private ImageView ivPower;
	private CustomPopUpWindow popupWindow;

	private boolean menuState =  false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.compute_cancer_main);

		setCustomActionBar();

		findViews();

	}

	private void showPopup() {
		popupWindow = new CustomPopUpWindow(TestActivity.this, null);
	}

	private void findViews() {
		settingsMenu = (RelativeLayout) findViewById(R.id.settings_menu);

		//state icons
		ivWifi = (ImageView) findViewById(R.id.ivWifi);
		ivBattery = (ImageView) findViewById(R.id.ivBattery);
		ivPower = (ImageView) findViewById(R.id.ivPower);
	}

	private void setCustomActionBar() {
		LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflator.inflate(R.layout.custom_action_bar, null);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowHomeEnabled (false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_white)));
		actionBar.setCustomView(v);

		settingsButton = (LinearLayout) v.findViewById(R.id.settings_b);
		stateButton = (ImageView) v.findViewById(R.id.state_b);

		settingsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showSettingsMenu();
			}
		});

		stateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//operation with state
			}
		});

	}

	private void showSettingsMenu() {
		if(menuState) {
			menuState = false;
			settingsMenu.setVisibility(View.GONE);
		} else {
			menuState = true;
			settingsMenu.setVisibility(View.VISIBLE);
			settingsMenu.bringToFront();
		}
	}

	// false = red , true - white
	private void setStateButtonsColor (boolean state) {
		if(!state) {
			ivPower.setBackgroundResource(R.drawable.ic_red_power_24_px);
			ivWifi.setBackgroundResource(R.drawable.ic_red_network_wifi_24_px);
			ivBattery.setBackgroundResource(R.drawable.ic_red_battery_full_24_px);
		} else {
			ivPower.setBackgroundResource(R.drawable.ic_power_24_px);
			ivWifi.setBackgroundResource(R.drawable.ic_network_wifi_24_px);
			ivBattery.setBackgroundResource(R.drawable.ic_battery_full_24_px);
		}
	}


	public void onTopIconsClick(View v) {
		if(v.getId() == R.id.ivPower || v.getId() == R.id.ivBattery || v.getId() == R.id.ivWifi) {
			showPopup();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
}
