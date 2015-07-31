package com.changami.app.batteryisready.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.changami.app.batteryisready.R;
import com.changami.app.batteryisready.activities.MainActivity;

public class WatchingBatteryService extends Service {

    final int notificationId = 1;
    final long[] PATTERN = {500, 2000};

    Vibrator vibrator;
    BroadcastReceiver broadcastReceiver;
    NotificationManagerCompat notificationManager;

    @Override
    public void onCreate() {
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Intent to start MainActivity for clicking this app's notification
        final Intent notificationIntent = new Intent(this,
                MainActivity.class);
        final PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        // Making Notification
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.label_notification, 100))
                        .setContentIntent(contentIntent);
        notificationManager =
                NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, notificationBuilder.build());

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int batteryLevel = intent.getIntExtra(getString(R.string.battery_level), 0);
                if (batteryLevel >= 100/*TODO*/) {
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

        notificationManager.cancel(notificationId);
        vibrator.cancel();

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
