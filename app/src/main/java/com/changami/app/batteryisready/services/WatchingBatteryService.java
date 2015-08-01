package com.changami.app.batteryisready.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.changami.app.batteryisready.R;
import com.changami.app.batteryisready.activities.MainActivity;

public class WatchingBatteryService extends Service {

    final int NOTIFICATION_ON_SERVICE = 1;
    final int NOTIFICATION_CANCEL = 2;
    final long[] PATTERN = {500, 2000};

    Vibrator vibrator;
    BroadcastReceiver broadcastReceiver;
    NotificationManagerCompat notificationManager;
    SharedPreferences preference;

    @Override
    public void onCreate() {
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        preference = getSharedPreferences(getString(R.string.preference_name), MODE_PRIVATE);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final int targetVolume = preference.getInt(getString(R.string.preference_target_volume, ""), 100);

        // Intent to start MainActivity for clicking this app's notification
        Intent notificationIntent = new Intent(this,
                MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        // Making Notification
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.notification_running, targetVolume))
                        .setContentIntent(notificationPendingIntent);
        notificationManager =
                NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ON_SERVICE, notificationBuilder.build());

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int batteryLevel = intent.getIntExtra(getString(R.string.battery_level), 0);
                if (batteryLevel >= targetVolume) {
                    // switch notification for stopping vibrator
                    notificationManager.cancel(NOTIFICATION_ON_SERVICE);
                    NotificationCompat.Builder cancelBuilder = new NotificationCompat.Builder(WatchingBatteryService.this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(getString(R.string.app_name))
                            .setContentText(getString(R.string.notification_guide));
                    notificationManager =
                            NotificationManagerCompat.from(WatchingBatteryService.this);
                    notificationManager.notify(NOTIFICATION_CANCEL, cancelBuilder.build());

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

        notificationManager.cancel(NOTIFICATION_ON_SERVICE);
        notificationManager.cancel(NOTIFICATION_CANCEL);
        vibrator.cancel();

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
