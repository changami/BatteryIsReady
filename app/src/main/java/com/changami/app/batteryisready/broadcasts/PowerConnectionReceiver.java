package com.changami.app.batteryisready.broadcasts;

import android.content.*;
import android.os.BatteryManager;
import com.changami.app.batteryisready.R;
import com.changami.app.batteryisready.services.WatchingBatteryService;

public class PowerConnectionReceiver extends BroadcastReceiver {

    boolean isAvailable = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preference = context.getSharedPreferences(context.getString(R.string.preference_name), Context.MODE_PRIVATE);
        isAvailable = preference.getBoolean(context.getString(R.string.preference_available), false);

        // ACTION_POWER_CONNECTED doesn't have EXTRA_STATUS.
        Intent chargingIntent = context.registerReceiver(null, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));
        assert chargingIntent != null;
        final int status = chargingIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = (status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL);

        if (isAvailable && isCharging) {
            context.startService(new Intent(context, WatchingBatteryService.class));
        } else {
            context.stopService(new Intent(context, WatchingBatteryService.class));
        }
    }
}
