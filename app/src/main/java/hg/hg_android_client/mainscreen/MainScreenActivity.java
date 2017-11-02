package hg.hg_android_client.mainscreen;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import hg.hg_android_client.R;
import hg.hg_android_client.mainscreen.event.CancelTripSetup;
import hg.hg_android_client.mainscreen.event.ConfirmDriver;
import hg.hg_android_client.mainscreen.event.ConfirmPath;
import hg.hg_android_client.mainscreen.event.SelectDriver;
import hg.hg_android_client.mainscreen.event.UpdateLocation;
import hg.hg_android_client.mainscreen.event.SendSelectMessage;
import hg.hg_android_client.mainscreen.event.ShowPath;
import hg.hg_android_client.mainscreen.event.UpdateDestination;
import hg.hg_android_client.mainscreen.select_destination.SelectDestinationFragment;
import hg.hg_android_client.mainscreen.select_driver.Driver;
import hg.hg_android_client.mainscreen.select_driver.SelectDriverFragment;
import hg.hg_android_client.mainscreen.select_path.SelectPathFragment;
import hg.hg_android_client.mainscreen.services.TripService;
import hg.hg_android_client.mainscreen.state.StateKey;
import hg.hg_android_client.mainscreen.state.StateVector;
import hg.hg_android_client.profile.ProfileActivity;
import hg.hg_android_client.util.BitmapLoader;
import hg.hg_android_client.util.LlevameActivity;

public class MainScreenActivity extends LlevameActivity implements
        OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String ACTION_STOP_ATTEMPT =
            "hg.hg_android_client.mainscreen.STOP_ATTEMPT";

    public static final String ACTION_LAUNCH_FROM_NOTIFICATION =
            "hg.hg_android_client.mainscreen.LAUNCH_FROM_NOTIFICATION";

    private static final int PERMISSION_REQUEST_ID = 0;

    private GoogleMap map;

    private Marker locationMarker;
    private Marker destinationMarker;
    private Marker driverMarker;

    private Polyline selectedPath;

    private StateVector statevector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkExitConditions();
        initializeMap();
    }

    private void checkExitConditions() {
        Intent i = getIntent();
        if (i.getAction() != null && ACTION_STOP_ATTEMPT.equals(i.getAction())) {
            handleStop();
        }
    }

    private boolean checkPermissions() {
        int granted = PackageManager.PERMISSION_GRANTED;
        boolean has = true;

        String required[] = new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        for (String permission : required) {
            has = has && ActivityCompat.checkSelfPermission(this, permission) == granted;
        }

        if (!has) {
            ActivityCompat.requestPermissions(this, required, PERMISSION_REQUEST_ID);
        }

        return has;
    }

    @Override
    public void onRequestPermissionsResult(int code, String[] permissions, int[] grants) {
        for (int i = 0; i < permissions.length; i++) {
            if (grants[i] != PackageManager.PERMISSION_GRANTED) {
                handleStop();
            }
        }
        initializeLocation();
    }

    private void initializeMap() {
        SupportMapFragment m = getMap();
        m.getMapAsync(this);
    }

    private SupportMapFragment getMap() {
        return (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        this.map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        this.map.moveCamera(CameraUpdateFactory.zoomTo((float)15.0));
        initializeFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (checkPermissions()) {
            initializeLocation();
        };
    }

    private void initializeLocation() {
        Intent i = new Intent(getApplicationContext(), TripService.class);
        i.setAction(TripService.ACTION_GET_LOCATION);
        startService(i);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationUpdate(UpdateLocation event) {
        updateLocation(event.getLocation());
    }

    private void updateLocation(LatLng position) {
        if (locationMarker == null) {
            MarkerOptions opts = new MarkerOptions();
            opts.title(getString(R.string.location_me));
            opts.position(position);
            if (map != null) {
                locationMarker = map.addMarker(opts);
                map.moveCamera(CameraUpdateFactory.newLatLng(position));
            }
        } else {
            locationMarker.setPosition(position);
        }
        statevector.setOrigin(position);
        statevector.persist(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_profile_action:
                handleGoToProfile();
                return true;
            case R.id.menu_logout_action:
                stopTripService();
                handleLogout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void handleGoToProfile() {
        // TODO Check whether we are on a trip; profile edition should be blocked during trips.
        Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(i);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        switch (intent.getAction()) {
            case ACTION_STOP_ATTEMPT:
                handleStop();
                break;
        }
    }

    private void handleStop() {
        Intent i = new Intent(getApplicationContext(), TripService.class);
        i.setAction(TripService.ACTION_STOP);
        startService(i);
        ExitActivity.exitApplication(getApplicationContext());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (statevector != null && statevector.isPassengerWaitingConfirmation()) {
            cancelDriverSelection();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (map != null) {
            map = null;
        }
    }

    //==============================================================================================
    //----------------------------------------------------------------------------------------------
    // DOWN BELOW HERE IS STATE DEPENDANT UI BEHAVIOR
    //----------------------------------------------------------------------------------------------

    private void initializeFragment() {
        statevector = StateVector.load(getApplicationContext());

        if (statevector.isPassengerPickingDestination()) {
            initializePassengerSelectDestination();
        } else if (statevector.isPassengerPickingPath()) {
            initializePassengerSelectPath();
        } else if (statevector.isPassengerPickingDriver()) {
            initializePassengerSelectDriver();
        }

        statevector.propagate();
    }

    private void initializePassengerSelectDestination() {
        SelectDestinationFragment f = new SelectDestinationFragment();
        map.setOnMapClickListener(f);
        replaceStateFragment(f);
    }

    private void initializePassengerSelectPath() {
        map.setOnMapClickListener(null);
        Fragment target = new SelectPathFragment();
        replaceStateFragment(target);
    }

    private void initializePassengerSelectDriver() {
        map.setOnMapClickListener(null);
        Fragment target = new SelectDriverFragment();
        replaceStateFragment(target);
    }

    private void replaceStateFragment(Fragment target) {
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.state_container, target);
        t.commit();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateDestination(UpdateDestination event) {
        LatLng destination = event.getPosition();
        if (destinationMarker == null) {
            MarkerOptions opts = new MarkerOptions();
            opts.title(getString(R.string.location_destination));
            opts.icon(loadbmp(R.drawable.destination));
            opts.anchor(0.5f, 0.5f);
            opts.position(destination);
            destinationMarker = map.addMarker(opts);
        } else {
            destinationMarker.setPosition(destination);
        }
        statevector.setDestination(destination);
        statevector.persist(getApplicationContext());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSendSelectMessage(SendSelectMessage event) {
        statevector.setKey(StateKey.PASSENGER_SELECT_PATH);
        statevector.persist(getApplicationContext());
        initializePassengerSelectPath();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCancelTripSetup(CancelTripSetup event) {
        cleanState();
        initializeFragment();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowPath(ShowPath event) {
        if (selectedPath != null) {
            selectedPath.remove();
        }
        selectedPath = event.getPath().addTo(map);
        statevector.setPath(event.getPath());
        statevector.persist(getApplicationContext());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConfirmPath(ConfirmPath event) {
        statevector.setKey(StateKey.PASSENGER_SELECT_DRIVER);
        statevector.persist(getApplicationContext());
        initializePassengerSelectDriver();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectDriver(SelectDriver event) {
        Driver driver = event.getDriver();
        if (driverMarker == null) {
            MarkerOptions options = new MarkerOptions();
            options.icon(loadbmp(R.drawable.ic_directions_car_black_24dp));
            options.anchor(0.5f, 0.5f);
            options.position(driver.getLocation().toLatLng());
            driverMarker = map.addMarker(options);
        } else {
            driverMarker.setPosition(driver.getLocation().toLatLng());
        }
        driverMarker.setTitle(driver.getProfile().getFullName());
        driverMarker.showInfoWindow();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConfirmDriver(ConfirmDriver event) {
        statevector.setKey(StateKey.PASSENGER_WAIT_FOR_TRIP_CONFIRMATION);
        statevector.setDriver(event.getDriver());
        String title = getString(R.string.waiting_driver_confirmation);
        String message = getString(R.string.please_wait);; // TODO: Load these...
        showDialog(title, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                cancelDriverSelection();
            }
        });
        // TODO: Send confirm push notification.
        // TODO: Somehow, origin, destination and passenger name need to be sent too.
    }

    private void cancelDriverSelection() {
        // TODO: Send cancel push notification.
        // TODO: If driver does not answer, user will need to cancel.
        statevector.setDriver(null);
        statevector.setKey(StateKey.PASSENGER_SELECT_DRIVER);
        dismissDialog();
    }

    private BitmapDescriptor loadbmp(int vectorid) {
        BitmapLoader loader = new BitmapLoader();
        return loader.load(getApplicationContext(), vectorid);
    }

    private void cleanState() {
        statevector.clear(getApplicationContext());

        if (locationMarker != null) {
            locationMarker.remove();
            locationMarker = null;
        }
        if (destinationMarker != null) {
            destinationMarker.remove();
            destinationMarker = null;
        }
        if (selectedPath != null) {
            selectedPath.remove();
            selectedPath = null;
        }
        if (driverMarker != null) {
            driverMarker.remove();
            driverMarker = null;
        }

        initializeLocation();
    }

}
