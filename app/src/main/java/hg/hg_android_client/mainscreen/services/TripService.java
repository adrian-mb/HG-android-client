package hg.hg_android_client.mainscreen.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import hg.hg_android_client.R;
import hg.hg_android_client.mainscreen.MainScreenActivity;

public class LocationUpdateService extends Service {

    private static final int SERVICE_ID = 1337;

    private static final int LAUNCH_MAIN_REQUEST_CODE = 1;
    private static final int LAUNCH_STOP_REQUEST_CODE = 2;

    public static final String LAUNCH_FROM_NOTIFICATION = "hg.hg_android_client.LAUNCH_FROM_NOTI";
    public static final String STOP_FROM_NOTIFICATION = "hg.hg_android_client.STOP_FROM_NOTI";

    @Override
    public void onCreate() {

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.fg_notification_text))
                .setContentIntent(mainActivityIntent())
                .addAction(
                        R.drawable.icon_stop,
                        getString(R.string.action_stop),
                        stopServiceIntent())
                .build();

        startForeground(SERVICE_ID, notification);
    }

    private PendingIntent mainActivityIntent() {
        Context context = getApplicationContext();
        Intent intent = new Intent(context, MainScreenActivity.class);
        intent.setAction(LAUNCH_FROM_NOTIFICATION);
        return PendingIntent.getActivity(context, LAUNCH_MAIN_REQUEST_CODE, intent, 0);
    }

    private PendingIntent stopServiceIntent() {
        Context context = getApplicationContext();
        Intent intent = new Intent(context, MainScreenActivity.class);
        intent.setAction(STOP_FROM_NOTIFICATION);
        return PendingIntent.getActivity(context, LAUNCH_STOP_REQUEST_CODE, intent,0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Called when?
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Called when? Does this run in the main thread?
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
