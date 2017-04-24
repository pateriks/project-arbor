package se.kth.projectarbor.project_arbor;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainService extends Service {
    final static String TAG = "ARBOR";
    private final static int ALARM_TIME = 6;
    final static String filename = "user.dat";

    // Don't use 0, it will mess up everything
    final static int MSG_START = 1;
    final static int MSG_STOP = 2;
    final static int MSG_CREATE = 3;

    // MainService works with following components
    private LocationManager locationManager;
    private Tree tree;
    // end

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(MainService.TAG, "OnCreate()");
        locationManager = new LocationManager(this, 10000, 5);

        List<Object> list = DataManager.readState(this, filename);
        loadState(list);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(MainService.TAG, "onStartCommand()");

        int msg = 0;
        if (intent.getExtras() != null) {
            msg = intent.getExtras().getInt("MESSAGE_TYPE", 0);
            intent.getExtras().remove("MESSAGE_TYPE");
        }

        if (msg == MSG_START) {
            locationManager.connect();
            List<Object> list = DataManager.readState(this, filename);
            loadState(list);
            startForeground();

            return START_STICKY;
        } else {
            if (msg == MSG_STOP) {
                locationManager.disconnect();
                stopForeground(true);
                DataManager.saveState(this, filename, tree, locationManager.getTotalDistance());
            } else if (msg == MSG_CREATE) {
                tree = new Tree();
                DataManager.saveState(this, filename, tree, locationManager.getTotalDistance());
            }

            PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + (ALARM_TIME * 1000), pendingIntent);
        }

        return START_NOT_STICKY;
    }

    private void loadState(List<Object> objects) {
        tree = (Tree) objects.get(0);
        Float distance = (Float) objects.get(1);

        locationManager.setTotalDistance(distance);
    }

    private void startForeground() {
        Notification notification = new Notification.Builder(this)
                .setContentTitle(getText(R.string.app_name))
                .setContentText(getText(R.string.content_text))
                //.setSmallIcon(R.drawable.icon)
                //.setContentIntent(pendingIntent)
                //.setTicker(getText(R.string.ticker_text))
                .getNotification();

        startForeground(1, notification);
    }
}

