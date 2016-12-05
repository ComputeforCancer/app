package org.computeforcancer.android.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import org.computeforcancer.android.BOINCActivity;
import org.computeforcancer.android.R;
import org.computeforcancer.android.attach.CredentialInputActivity;

/**
 * Created by Maksym Shpyl on 05.12.2016.
 */
public class MainFragment extends AbstractBaseFragment {

    private static final String TAG = MainFragment.class.getSimpleName();

    private ImageView ivWifi;
    private ImageView ivBattery;
    private ImageView ivPower;

    private BroadcastReceiver mPlugInReceiver;
    private BroadcastReceiver mWifiReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.compute_cancer_main, viewGroup, false);

        ivWifi = (ImageView) mRootView.findViewById(R.id.ivWifi);
        ivBattery = (ImageView) mRootView.findViewById(R.id.ivBattery);
        ivPower = (ImageView) mRootView.findViewById(R.id.ivPower);
        registerPlugInReceiver();
        registerWifiReceiver();
/*
        ImageView readyB = (ImageView) mRootView.findViewById(R.id.menu);
        readyB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getContext(), v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.compute_cancer_menu, popup.getMenu());
                popup.show();
            }
        });*/

        return mRootView;
    }

    private void registerPlugInReceiver() {
        mPlugInReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                boolean b = plugged == BatteryManager.BATTERY_PLUGGED_AC ||
                        plugged == BatteryManager.BATTERY_PLUGGED_USB;
                powerIcon(!b);

                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int batteryPct = level * 100 / scale;
                batteryIcon(batteryPct < 90);
                Log.d("TEST", "batteryPct " + batteryPct);
            }
        };
        Intent intent = getActivity().registerReceiver(mPlugInReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean b = plugged == BatteryManager.BATTERY_PLUGGED_AC ||
                plugged == BatteryManager.BATTERY_PLUGGED_USB;
        powerIcon(!b);

        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int batteryPct = level * 100 / scale;
        batteryIcon(batteryPct < 90);
    }

    private void powerIcon(boolean setRed) {
        if (setRed) {
            ivPower.setBackgroundResource(R.drawable.ic_red_power_24_px);
        } else {
            ivPower.setBackgroundResource(R.drawable.ic_power_24_px);
        }
    }

    private void batteryIcon(boolean setRed) {
        if (setRed) {
            ivBattery.setBackgroundResource(R.drawable.ic_red_battery_full_24_px);
        } else {
            ivBattery.setBackgroundResource(R.drawable.ic_battery_full_24_px);
        }
    }

    private void registerWifiReceiver() {
        mWifiReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = conMan.getActiveNetworkInfo();
                if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI)
                    wifiIcon(false);
                else
                    wifiIcon(true);
            }
        };
        IntentFilter filters = new IntentFilter();
        filters.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        getActivity().registerReceiver(mWifiReceiver, filters);
        ConnectivityManager conMngr = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        android.net.NetworkInfo wifi = conMngr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        wifiIcon(!wifi.isConnected());
    }

    private void wifiIcon(boolean setRed) {
        Log.d("TEST", "wifiIcon " + setRed);
        if (setRed) {
            ivWifi.setBackgroundResource(R.drawable.ic_red_network_wifi_24_px);
        } else {
            ivWifi.setBackgroundResource(R.drawable.ic_network_wifi_24_px);
        }
    }

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

    @Override
    public void onDestroyView() {
        getActivity().unregisterReceiver(mPlugInReceiver);
        getActivity().unregisterReceiver(mWifiReceiver);
        super.onDestroyView();
    }

    @Override
    public String getTAG() {
        return TAG;
    }
}
