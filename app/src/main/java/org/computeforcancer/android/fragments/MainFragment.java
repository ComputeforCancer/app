package org.computeforcancer.android.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.computeforcancer.android.BOINCActivity;
import org.computeforcancer.android.R;
import org.computeforcancer.android.utils.BOINCDefs;
import org.computeforcancer.android.utils.Logging;
import org.computeforcancer.android.utils.SharedPrefs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

        Button tellFriendsB = (Button) mRootView.findViewById(R.id.btnTellFriends);
        tellFriendsB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareClicked();
            }
        });

        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences mSharedPreferences = getContext().getSharedPreferences("org.computeforcancer.android",
                Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
        //Log.d("TEST", "onResume get DONATION_TIME " + mSharedPreferences.getLong(SharedPrefs.DONATION_TIME + mSharedPreferences.getString(SharedPrefs.CURRENT_EMAIL, ""), 0));

        updateUI(mSharedPreferences.getLong(SharedPrefs.LAST_SESSION_TIME + mSharedPreferences.getString(SharedPrefs.CURRENT_EMAIL, ""), 0),
                mSharedPreferences.getLong(SharedPrefs.DONATION_TIME + mSharedPreferences.getString(SharedPrefs.CURRENT_EMAIL, ""), 0));
    }

    public void updateUI(long lastSession, long overall) {
        SimpleDateFormat fmtOut = new SimpleDateFormat("MMM dd");
        ((TextView) mRootView.findViewById(R.id.tvLastSessionState)).setText(
                fmtOut.format(new Date()).toUpperCase());

        ((TextView) mRootView.findViewById(R.id.session_hours)).setText(
                String.format("%02d", TimeUnit.MILLISECONDS.toHours(lastSession)));
        ((TextView) mRootView.findViewById(R.id.session_minutes)).setText(
                String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(lastSession) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(lastSession))));
        ((TextView) mRootView.findViewById(R.id.session_seconds)).setText(
                String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(lastSession) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(lastSession))));

        ((TextView) mRootView.findViewById(R.id.totaltime_hours)).setText(
                String.format("%02d", TimeUnit.MILLISECONDS.toHours(overall)));
        ((TextView) mRootView.findViewById(R.id.totaltime_minutes)).setText(
                String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(overall) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(overall))));
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

    private void shareClicked() {
        if(Logging.DEBUG) Log.d(Logging.TAG, "shareClicked.");
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        SharedPreferences mSharedPreferences = getContext().getSharedPreferences("org.computeforcancer.android",
                Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
        //Log.d("TEST", "get DONATION_TIME " + mSharedPreferences.getLong(SharedPrefs.DONATION_TIME + mSharedPreferences.getString(SharedPrefs.CURRENT_EMAIL, ""), 0));

        long overall = mSharedPreferences.getLong(SharedPrefs.DONATION_TIME + mSharedPreferences.getString(SharedPrefs.CURRENT_EMAIL, ""), 0);
        String hours =
                String.format("%02d", TimeUnit.MILLISECONDS.toHours(overall));
        String minutes =
                String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(overall) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(overall)));
        // Add data to the intent, the receiving app will decide what to do with it.
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.social_invite_content_title));
        intent.putExtra(Intent.EXTRA_TEXT, String.format(getString(R.string.share), hours, minutes));

        startActivity(Intent.createChooser(intent, getString(R.string.social_invite_intent_title)));
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
                    try {
                        wifiIcon(false);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                else
                    try {
                        wifiIcon(true);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
            }
        };
        IntentFilter filters = new IntentFilter();
        filters.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        getActivity().registerReceiver(mWifiReceiver, filters);
        ConnectivityManager conMngr = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        android.net.NetworkInfo wifi = conMngr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        try {
            wifiIcon(!wifi.isConnected());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void wifiIcon(boolean setRed) throws RemoteException {
        SharedPreferences mSharedPreferences = getContext().getSharedPreferences("org.computeforcancer.android",
                Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
        if (setRed) {
            if (BOINCActivity.monitor != null) {
                BOINCActivity.monitor.setAutostart(false);
                BOINCActivity.monitor.setRunMode(BOINCDefs.RUN_MODE_NEVER);
            }
            ivWifi.setBackgroundResource(R.drawable.ic_red_network_wifi_24_px);
        } else {
            if (BOINCActivity.monitor != null) {
                BOINCActivity.monitor.setAutostart(mSharedPreferences.getBoolean(SharedPrefs.AUTO_START, true));
                BOINCActivity.monitor.setRunMode(mSharedPreferences.getBoolean(SharedPrefs.AUTO_START, true)
                        ? BOINCDefs.RUN_MODE_AUTO : BOINCDefs.RUN_MODE_NEVER);
            }
            ivWifi.setBackgroundResource(R.drawable.ic_network_wifi_24_px);
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
