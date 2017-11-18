package hg.hg_android_client.mainscreen.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.iid.FirebaseInstanceId;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.logging.Level;
import java.util.logging.Logger;

import hg.hg_android_client.R;
import hg.hg_android_client.firebase.MessagingEndpoint;
import hg.hg_android_client.login.repository.TokenRepository;
import hg.hg_android_client.login.repository.TokenRepositoryFactory;
import hg.hg_android_client.mainscreen.MainScreenActivity;
import hg.hg_android_client.mainscreen.event.FirebaseTokenUpdate;
import hg.hg_android_client.mainscreen.event.UpdateLocation;

public class TripService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String LOGGER_TAG = "TripService";

    private static final int SERVICE_ID = 1337;

    private static final int LAUNCH_MAIN_REQUEST_CODE = 1;
    private static final int LAUNCH_STOP_REQUEST_CODE = 2;

    public static final String INTENT_LAUNCH = "hg.hg_android_client.INTENT_LAUNCH";
    public static final String INTENT_STOP = "hg.hg_android_client.INTENT_STOP";

    public static final String ACTION_STOP = "hg.hg_android_client.ACTION_STOP";
    public static final String ACTION_GET_LOCATION = "hg.hg_android_client.ACTION_GET_LOCATION";

    private GoogleApiClient apiclient;

    @Override
    public void onCreate() {
        goForeground();
        googleConnect();
        checkFirebaseToken();
    }

    private void goForeground() {
        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_directions_car_black_24dp)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.fg_notification_text))
                .setContentIntent(launchIntent())
                .addAction(
                        R.drawable.icon_stop,
                        getString(R.string.action_stop),
                        stopIntent())
                .build();

        startForeground(SERVICE_ID, notification);
    }

    private PendingIntent launchIntent() {
        Context context = getApplicationContext();
        Intent intent = new Intent(context, TripService.class);
        intent.setAction(INTENT_LAUNCH);
        return PendingIntent.getService(context, LAUNCH_MAIN_REQUEST_CODE, intent, 0);
    }

    private PendingIntent stopIntent() {
        Context context = getApplicationContext();
        Intent intent = new Intent(context, TripService.class);
        intent.setAction(INTENT_STOP);
        return PendingIntent.getService(context, LAUNCH_STOP_REQUEST_CODE, intent, 0);
    }

    private void googleConnect() {
        if (apiclient == null) {
            apiclient = new GoogleApiClient.Builder(getApplicationContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        apiclient.connect();
    }

    private void checkFirebaseToken() {
        String token = FirebaseInstanceId.getInstance().getToken();
        if (token != null) {
            Logger.getLogger(LOGGER_TAG).log(Level.INFO, token);
            sendFirebaseToken(token);
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onFirebaseTokenUpdate(FirebaseTokenUpdate event) {
        sendFirebaseToken(event.getToken());
    }

    private void sendFirebaseToken(final String token) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                MessagingEndpoint endpoint =
                        new MessagingEndpoint(getApplicationContext());
                endpoint.updateToken(getAuthToken(), token);
                return null;
            }
        }.execute();
    }

    private String getAuthToken() {
        TokenRepositoryFactory f = new TokenRepositoryFactory();
        TokenRepository r = f.getRepository(getApplicationContext());
        return r.getToken();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        switch (intent.getAction()) {
            case INTENT_LAUNCH:
                launchFromNotification();
                break;
            case INTENT_STOP:
                stopAttempt();
                break;
            case ACTION_STOP:
                stop();
                break;
            case ACTION_GET_LOCATION:
                postLastKnownLocation();
                break;
        }

        return START_STICKY;
    }

    private void launchFromNotification() {
        Intent i = new Intent(getApplicationContext(), MainScreenActivity.class);
        i.setAction(MainScreenActivity.ACTION_LAUNCH_FROM_NOTIFICATION);
        i.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP );
        startActivity(i);
    }

    private void stopAttempt() {
        Intent i = new Intent(getApplicationContext(), MainScreenActivity.class);
        i.setAction(MainScreenActivity.ACTION_STOP_ATTEMPT);
        i.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP );
        startActivity(i);
    }

    private void stop() {
        stopSelf();
    }

    @Override
    public void onDestroy() {
        if (apiclient != null && apiclient.isConnected()) {
            apiclient.disconnect();
            apiclient = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        postLastKnownLocation();
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        // TODO: See what to do here.
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // TODO: See what to do here.
    }

    @SuppressLint("MissingPermission")
    private void postLastKnownLocation() {
        Location location = LocationServices.FusedLocationApi.getLastLocation(apiclient);
        if (location != null) {
            onLocationChanged(location);
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        LocationListener callback = this;
        LocationRequest request = new LocationRequest();
        request.setInterval(20000);
        request.setFastestInterval(10000);
        request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(apiclient, request, callback);
    }

    @Override
    public void onLocationChanged(Location location) {
        UpdateLocation update = new UpdateLocation(location);
        sendUpdatePosition(location);
        EventBus.getDefault().post(update);
    }

    private void sendUpdatePosition(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        final hg.hg_android_client.mainscreen.select_path.Location loc =
                new hg.hg_android_client.mainscreen.select_path.Location(
                        latitude, longitude);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                PositionUpdateEndpoint endpoint =
                        new PositionUpdateEndpoint(getApplicationContext());
                endpoint.updateLocation(getAuthToken(), loc);
                return null;
            }
        }.execute();
    }

}
