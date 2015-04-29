package com.changami.app.batteryisready.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Vibrator;
import com.changami.app.batteryisready.R;

public class WatchingBatteryService extends Service {

    final long[] PATTERN = {500, 2000};
    Vibrator vibrator;
    BroadcastReceiver broadcastReceiver;

    @Override
    public void onCreate() {
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int batteryLevel = intent.getIntExtra(getString(R.string.battery_level), 0);
                if (batteryLevel > 99/*TODO*/) {
                    vibrator.vibrate(PATTERN, 0);
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        vibrator.cancel();

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
