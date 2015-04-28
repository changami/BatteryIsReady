package com.changami.app.batteryisready.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import com.changami.app.batteryisready.services.WatchingBatteryService;

public class PowerConnectionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // ACTION_POWER_CONNECTED doesn't have EXTRA_STATUS.
        // http://stackoverflow.com/questions/20833241/android-charge-intent-has-no-extra-data
        Intent chargingIntent = context.registerReceiver(null, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));
        assert chargingIntent != null;
        final int status = chargingIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = (status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL);

        if (isCharging) {
            context.startService(new Intent(context, WatchingBatteryService.class));
        } else {
            context.stopService(new Intent(context, WatchingBatteryService.class));
        }
    }
}
