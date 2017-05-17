package se.kth.projectarbor.project_arbor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences("se.kth.projectarbor.project_arbor", Context.MODE_PRIVATE);
        Log.d("ARBOR_AGE", "Inside boot receiver");
        if (sharedPreferences.getBoolean("FIRST_TREE", false)) {
            Intent intentToService = new Intent(context, MainService.class)
                    .putExtra("MESSAGE_TYPE", MainService.MSG_BOOT);
            context.startService(intentToService);
        }
    }
}