package hg.hg_android_client.mainscreen;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
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
import hg.hg_android_client.mainscreen.event.ConfirmPath;
import hg.hg_android_client.mainscreen.event.LocationUpdate;
import hg.hg_android_client.mainscreen.event.SendSelectMessage;
import hg.hg_android_client.mainscreen.event.ShowPath;
import hg.hg_android_client.mainscreen.event.UpdateDestination;
import hg.hg_android_client.mainscreen.repository.StateRepository;
import hg.hg_android_client.mainscreen.repository.StateRepositoryFactory;
import hg.hg_android_client.mainscreen.select_destination.SelectDestinationFragment;
import hg.hg_android_client.mainscreen.select_path.Path;
import hg.hg_android_client.mainscreen.select_path.SelectPathFragment;
import hg.hg_android_client.mainscreen.services.TripService;
import hg.hg_android_client.mainscreen.state.StateKey;
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

    private Path selectedPath;
    private Polyline selectedPathPoly;

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
    public void onLocationUpdate(LocationUpdate event) {
        updateLocation(event.getLocation());
    }

    private void updateLocation(Location location) {
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
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
        StateKey state = retrieveState();

        switch (state) {
            case PASSENGER_SELECT_DESTINATION:
                initializePassengerSelectDestination();
                break;
            case DRIVER_WAIT_FOR_TRIP_REQUEST:
                // TODO initializeDriverWaitForTrip();
                break;
            case PASSENGER_SELECT_PATH:
                initializePassengerSelectPath();
                break;
        }
    }

    private StateKey retrieveState() {
        StateRepositoryFactory f = new StateRepositoryFactory();
        StateRepository r = f.get(getApplicationContext());
        return r.getKey();
    }

    private void initializePassengerSelectDestination() {
        SelectDestinationFragment f = new SelectDestinationFragment();
        map.setOnMapClickListener(f);
        replaceStateFragment(f);
    }

    private void initializePassengerSelectPath() {
        moveToSelectPath();
        loadState();
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
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSendSelectMessage(SendSelectMessage event) {
        updateState(StateKey.PASSENGER_SELECT_PATH);
        moveToSelectPath();
    }

    private void moveToSelectPath() {
        map.setOnMapClickListener(null);
        Fragment target = new SelectPathFragment();
        replaceStateFragment(target);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCancelTripSetup(CancelTripSetup event) {
        cleanRecords();
        initializeFragment();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowPath(ShowPath event) {
        if (selectedPathPoly != null) {
            selectedPathPoly.remove();
        }
        selectedPath = event.getPath();
        selectedPathPoly = selectedPath.addTo(map);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConfirmPath(ConfirmPath event) {
        updateState(StateKey.PASSENGER_SELECT_DRIVER);
        // TODO: Load driver selection fragment and call replaceStateFragment.
    }

    private void replaceStateFragment(Fragment target) {
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.state_container, target);
        t.commit();
    }

    private BitmapDescriptor loadbmp(int vectorid) {
        BitmapLoader loader = new BitmapLoader();
        return loader.load(getApplicationContext(), vectorid);
    }

    private void updateState(StateKey state) {
        StateRepositoryFactory f = new StateRepositoryFactory();
        StateRepository r = f.get(getApplicationContext());

        if (locationMarker != null) {
            r.saveOrigin(locationMarker.getPosition());
        }
        if (destinationMarker != null) {
            r.saveDestination(destinationMarker.getPosition());
        }
        if (selectedPath != null) {
            r.savePath(selectedPath);
        }

        r.save(state);
    }

    private void loadState() {
        StateRepositoryFactory f = new StateRepositoryFactory();
        StateRepository r = f.get(getApplicationContext());

        LatLng destination = r.getDestination();
        if (destination != null) {
            UpdateDestination d = new UpdateDestination(destination);
            onUpdateDestination(d);
        }

        Path path = r.getPath();
        if (path != null) {
            ShowPath showpath = new ShowPath(path);
            onShowPath(showpath);
        }
    }

    private void cleanRecords() {
        cleanSavedState();
        if (destinationMarker != null) {
            destinationMarker.remove();
            destinationMarker = null;
        }
        if (selectedPathPoly != null) {
            selectedPathPoly.remove();
            selectedPathPoly = null;
            selectedPath = null;
        }
    }

    private void cleanSavedState() {
        StateRepositoryFactory f = new StateRepositoryFactory();
        StateRepository r = f.get(getApplicationContext());
        r.clear();
    }

}
