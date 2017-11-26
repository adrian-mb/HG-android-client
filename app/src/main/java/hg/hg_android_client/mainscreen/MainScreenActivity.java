package hg.hg_android_client.mainscreen;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
import hg.hg_android_client.firebase.SendMessageIntent;
import hg.hg_android_client.firebase.message.MessageType;
import hg.hg_android_client.login.repository.TokenRepository;
import hg.hg_android_client.login.repository.TokenRepositoryFactory;
import hg.hg_android_client.mainscreen.chat_client.ChatMessageRepository;
import hg.hg_android_client.mainscreen.driver_confirm.DriverConfirmFragment;
import hg.hg_android_client.mainscreen.driver_idle.DriverIdleFragment;
import hg.hg_android_client.mainscreen.driver_idle.Passenger;
import hg.hg_android_client.mainscreen.driver_meet_passenger.DriverMeetPassengerFragment;
import hg.hg_android_client.mainscreen.event.ReceiveFinishTrip;
import hg.hg_android_client.mainscreen.event.ReceiveInCar;
import hg.hg_android_client.mainscreen.event.SendCancelTrip;
import hg.hg_android_client.mainscreen.event.CancelTripSetup;
import hg.hg_android_client.mainscreen.event.ConfirmDriver;
import hg.hg_android_client.mainscreen.event.ConfirmPath;
import hg.hg_android_client.mainscreen.event.ConfirmTrip;
import hg.hg_android_client.mainscreen.event.DeclineTripRequest;
import hg.hg_android_client.mainscreen.event.DriverTripConfirmation;
import hg.hg_android_client.mainscreen.event.SendFinishTrip;
import hg.hg_android_client.mainscreen.event.FocusOnDestination;
import hg.hg_android_client.mainscreen.event.FocusOnPassenger;
import hg.hg_android_client.mainscreen.event.SendInCar;
import hg.hg_android_client.mainscreen.event.LoadDriverInfo;
import hg.hg_android_client.mainscreen.event.LoadPassengerInfo;
import hg.hg_android_client.mainscreen.event.ReceivedCancelTrip;
import hg.hg_android_client.mainscreen.event.SelectDriver;
import hg.hg_android_client.mainscreen.event.TripRequest;
import hg.hg_android_client.mainscreen.event.UpdateLocation;
import hg.hg_android_client.mainscreen.event.SendSelectMessage;
import hg.hg_android_client.mainscreen.event.ShowPath;
import hg.hg_android_client.mainscreen.event.UpdateDestination;
import hg.hg_android_client.mainscreen.on_trip.OnTripFragment;
import hg.hg_android_client.mainscreen.on_trip.TripRepository;
import hg.hg_android_client.mainscreen.select_destination.SelectDestinationFragment;
import hg.hg_android_client.mainscreen.select_driver.Driver;
import hg.hg_android_client.mainscreen.select_driver.SelectDriverFragment;
import hg.hg_android_client.mainscreen.select_path.Location;
import hg.hg_android_client.mainscreen.select_path.Path;
import hg.hg_android_client.mainscreen.select_path.SelectPathFragment;
import hg.hg_android_client.mainscreen.services.TripService;
import hg.hg_android_client.mainscreen.state.StateKey;
import hg.hg_android_client.mainscreen.state.StateVector;
import hg.hg_android_client.mainscreen.wait_for_driver.WaitForDriverFragment;
import hg.hg_android_client.model.Profile;
import hg.hg_android_client.profile.ProfileActivity;
import hg.hg_android_client.profile.repository.ProfileRepository;
import hg.hg_android_client.profile.repository.ProfileRepositoryFactory;
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
    private Marker passengerMarker;

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
    public void onDestroy() {
        super.onDestroy();
        if (statevector != null) {
            if (statevector.isPassengerWaitingConfirmation()) {
                cancelDriverSelection();
            } else if (statevector.isDriverEvaluatingTripRequest()) {
                declineTripRequest();
            }
        }
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
        } else if (statevector.isDriverWaitingTripRequest()) {
            initializeDriverWaitingTripRequest();
        } else if (statevector.isDriverMeetingPassenger()) {
            initializeDriverMeetPassenger();
        } else if (statevector.isPassengerWaitingForDriver()) {
            initializePassengerWaitForDriver();
        } else if (statevector.isOnTrip()) {
            initializeOnTrip();
        }

        statevector.propagate();
    }

    private void initializeDriverWaitingTripRequest() {
        Fragment f = new DriverIdleFragment();
        replaceStateFragment(f);
    }

    private void initializeDriverConfirmation(long lastRequestId) {
        Bundle args = new Bundle();
        args.putLong(DriverConfirmFragment.KEY_REQUEST_ID, lastRequestId);
        Fragment f = new DriverConfirmFragment();
        f.setArguments(args);
        replaceStateFragment(f);
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

    private void initializeDriverMeetPassenger() {
        DriverMeetPassengerFragment f = new DriverMeetPassengerFragment();
        replaceStateFragment(f);
    }

    private void initializePassengerWaitForDriver() {
        Fragment f = new WaitForDriverFragment();
        replaceStateFragment(f);
    }

    private void cleanChatLog() {
        ChatMessageRepository r = new ChatMessageRepository(getApplicationContext());
        r.clean();
    }

    private void initializeOnTrip() {
        Fragment f = new OnTripFragment();
        replaceStateFragment(f);
    }

    private void replaceStateFragment(Fragment target) {
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.state_container, target);
        t.commit();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateDestination(UpdateDestination event) {
        LatLng destination = event.getPosition();
        updateDestinationMarker(destination);
        statevector.setDestination(destination);
        statevector.persist(getApplicationContext());
    }

    private void updateDestinationMarker(LatLng destination) {
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
        updateDriverMarker(driver);
    }

    private void removeDriverMarker() {
        if (driverMarker != null) {
            driverMarker.remove();
            driverMarker = null;
        }
    }

    private void updateDriverMarker(Driver driver) {
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
        String message = getString(R.string.please_wait);
        showDialog(title, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                cancelDriverSelection();
            }
        });
        sendDriverSelectedNotification(event);
    }

    private void sendDriverSelectedNotification(ConfirmDriver event) {
        Context context = getApplicationContext();
        MessageType type = MessageType.PASSENGER_SELECT_DRIVER;
        Long target = event.getDriver().getUserId();
        Location location = new Location(statevector.getPosition());
        Location destination = new Location(statevector.getDestination());
        Path path = statevector.getPath();
        Passenger passenger = Passenger.from(getCachedProfile(), location);
        TripRequest payload = new TripRequest(passenger, destination, path);
        statevector.setLastRequestId(payload.getRequestId());
        SendMessageIntent intent = new SendMessageIntent(context, target, type, payload);
        startService(intent);
    }

    private Profile getCachedProfile() {
        ProfileRepositoryFactory f = new ProfileRepositoryFactory();
        ProfileRepository r = f.getRepository(getApplicationContext());
        return r.retrieveCached();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDriverTripConfirmation(DriverTripConfirmation event) {
        long requestId = event.getRequestId();
        if (statevector.isLastRequestId(event.getRequestId())) {
            statevector.setKey(StateKey.PASSENGER_WAIT_FOR_DRIVER);
            statevector.persist(getApplicationContext());
            dismissDialog();
            cleanChatLog();
            initializePassengerWaitForDriver();
        } else {
            // TODO: Send cancel or something.
        }
    }

    private void cancelDriverSelection() {
        sendCancelTripNotification();
        statevector.setDriver(null);
        statevector.setKey(StateKey.PASSENGER_SELECT_DRIVER);
        dismissDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTripRequest(TripRequest event) {
        if (statevector.isDriverWaitingTripRequest()) {
            statevector.setLastRequestId(event.getRequestId());
            statevector.persist(getApplicationContext());
            String passengerName = event.getPassenger().getName();
            LatLng passengerPosition = event.getPassenger().getLocation().toLatLng();
            LatLng passengerDestination = event.getDestination().toLatLng();

            statevector.setKey(StateKey.DRIVER_CONFIRM_TRIP);
            statevector.setPassenger(event.getPassenger());
            statevector.setDestination(passengerDestination);
            statevector.setPath(event.getPath());
            updateDestinationMarker(passengerDestination);
            updatePassengerMarker(passengerPosition, passengerName);
            selectedPath = event.getPath().addTo(map);
            initializeDriverConfirmation(event.getRequestId());
        } else {
            // TODO: What should we do in this case?
            // Send decline, or busy, or something?
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadPassengerInfo(LoadPassengerInfo event) {
        String name = event.getPassenger().getName();
        LatLng position = event.getPassenger().getLocation().toLatLng();
        updatePassengerMarker(position, name);
    }

    private void updatePassengerMarker(LatLng position, String name) {
        if (passengerMarker == null) {
            MarkerOptions options = new MarkerOptions();
            options.position(position);
            options.icon(loadbmp(R.drawable.ic_person_black_24dp));
            options.anchor(0.5f, 0.5f);
            passengerMarker = map.addMarker(options);
        } else {
            passengerMarker.setPosition(position);
        }
        passengerMarker.setTitle(name);
        passengerMarker.showInfoWindow();
    }

    private void removePassengerMarker() {
        if (passengerMarker != null) {
            passengerMarker.remove();
            passengerMarker = null;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadDriverInfo(LoadDriverInfo event) {
        updateDriverMarker(event.getDriver());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeclineTripRequest(DeclineTripRequest event) {
        if (statevector.isLastRequestId(event.getRequestId())) {
            if (statevector.isDriverEvaluatingTripRequest()) {
                declineTripRequest();
                statevector.clearLastRequestId();
                initializeFragment();
            } else if (statevector.isPassengerWaitingConfirmation()) {
                String drivername = driverMarker.getTitle();
                goBackOnPassengerWaitingState();
                String message = String.format(getString(R.string.driver_declined), drivername);
                displayToast(message);
            }
        }
    }

    private void declineTripRequest() {
        sendDeclineNotification();
        cleanState();
    }

    private void sendDeclineNotification() {
        Context context = getApplicationContext();
        MessageType type = MessageType.DRIVER_DECLINE_TRIP;
        Passenger passenger = statevector.getPassenger();
        Long target = passenger.getUserId();
        DeclineTripRequest payload = new DeclineTripRequest(statevector.getLastRequestId());
        SendMessageIntent intent = new SendMessageIntent(context, target, type, payload);
        startService(intent);
    }

    private void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConfirmTripRequest(ConfirmTrip event) {
        if (statevector.isLastRequestId(event.getRequestId())) {
            statevector.setKey(StateKey.DRIVER_MEET_PASSENGER);
            statevector.persist(getApplicationContext());
            sendDriverConfirmationNotification();
            sendStartTrip();
            cleanChatLog();
            initializeDriverMeetPassenger();
        }
    }

    public void sendStartTrip() {
        // TODO: Create a service or something...
        new AsyncTask<StateVector, Void, Void>() {
            @Override
            protected Void doInBackground(StateVector... args) {
                StateVector v = args[0];

                String token = retrieveAuthToken();

                Passenger passenger = v.getPassenger();
                Long passengerId = passenger.getUserId();
                Location origin = passenger.getLocation();
                Location destination = new Location(v.getDestination());

                TripRepository repo = new TripRepository(getApplicationContext());
                repo.startTrip(token, origin, destination, passengerId);
                return null;
            }
        }.execute(statevector);
    }

    public void cancelTrip() {
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                TripRepository repo = new TripRepository(getApplicationContext());
                repo.cancelTrip(retrieveAuthToken());
                return null;
            }
        }.execute();
    }

    public void finishTrip() {
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                TripRepository repo = new TripRepository(getApplicationContext());
                repo.endTrip(retrieveAuthToken());
                return null;
            }
        }.execute();
    }

    private String retrieveAuthToken() {
        TokenRepositoryFactory f = new TokenRepositoryFactory();
        TokenRepository r = f.getRepository(getApplicationContext());
        return r.getToken();
    }

    private void sendDriverConfirmationNotification() {
        Context context = getApplicationContext();
        MessageType type = MessageType.DRIVER_ACCEPT_TRIP;
        Passenger passenger = statevector.getPassenger();
        Long target = passenger.getUserId();
        ConfirmTrip payload = new ConfirmTrip(statevector.getLastRequestId());
        SendMessageIntent intent = new SendMessageIntent(context, target, type, payload);
        startService(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSendCancelTrip(SendCancelTrip event) {
        sendCancelTripNotification();
        if (statevector.isPassengerWaitingForDriver()) {
            goBackOnPassengerWaitingState();
        } else if (statevector.isDriverMeetingPassenger()) {
            cleanState();
        }
        cancelTrip();
        initializeFragment();
    }

    private void sendCancelTripNotification() {
        Context context = getApplicationContext();
        MessageType type = MessageType.CANCEL_TRIP;
        Long target = null;
        if (statevector.getPassenger() != null) {
            Passenger passenger = statevector.getPassenger();
            target = passenger.getUserId();
        } else if (statevector.getDriver() != null) {
            Driver driver = statevector.getDriver();
            target = driver.getUserId();
        }
        ReceivedCancelTrip payload = new ReceivedCancelTrip(statevector.getLastRequestId());
        SendMessageIntent intent = new SendMessageIntent(context, target, type, payload);
        startService(intent);
    }

    private void goBackOnPassengerWaitingState() {
        dismissDialog();
        statevector.setKey(StateKey.PASSENGER_SELECT_DRIVER);
        statevector.setDriver(null);
        statevector.persist(getApplicationContext());
        removeDriverMarker();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivedCancelTrip(ReceivedCancelTrip event) {
        String message = null;
        if (statevector.isLastRequestId(event.getRequestId())) {
            statevector.clearLastRequestId();
            if (statevector.isPassengerWaitingForDriver()) {
                String drivername = driverMarker.getTitle();
                goBackOnPassengerWaitingState();
                message = String.format(getString(R.string.driver_cancelled), drivername);
            } else if (statevector.isDriverMeetingPassenger()) {
                message = getString(R.string.passenger_cancelled_trip);
                cleanState();
            }
        }
        initializeFragment();
        displayToast(message);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFocusOnPassenger(FocusOnPassenger event) {
        if (passengerMarker != null) {
            map.moveCamera(CameraUpdateFactory.newLatLng(passengerMarker.getPosition()));
            passengerMarker.showInfoWindow();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFocusOnDestination(FocusOnDestination event) {
        if (destinationMarker != null) {
            map.moveCamera(CameraUpdateFactory.newLatLng(destinationMarker.getPosition()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSendInCar(SendInCar event) {
        sendInCarNotification();
        removeDriverMarker();
        statevector.setKey(StateKey.ON_TRIP);
        statevector.persist(getApplicationContext());
        sendInCarToServer();
        initializeOnTrip();
    }

    private void sendInCarToServer() {
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                TripRepository repo = new TripRepository(getApplicationContext());
                repo.notifyInCar(retrieveAuthToken());
                return null;
            }
        }.execute();
    }

    private void sendInCarNotification() {
        Context context = getApplicationContext();
        MessageType type = MessageType.IN_CAR;
        Driver driver = statevector.getDriver();
        Long target = driver.getUserId();
        ReceiveInCar payload = new ReceiveInCar(statevector.getLastRequestId());
        SendMessageIntent intent = new SendMessageIntent(context, target, type, payload);
        startService(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveInCar(ReceiveInCar event) {
        if (statevector.isLastRequestId(event.getRequestId())) {
            statevector.setKey(StateKey.ON_TRIP);
            statevector.persist(getApplicationContext());
            removePassengerMarker();
            initializeOnTrip();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSendFinishTrip(SendFinishTrip event) {
        event.setRequestId(statevector.getLastRequestId());
        displayConfirmationDialog("Finishing Trip", "Please wait...");
        finishTrip();
        // TODO: Actual notification response will come from server!
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveFinishTrip(ReceiveFinishTrip event) {
        dismissDialog();
        ProfileRepositoryFactory f = new ProfileRepositoryFactory();
        ProfileRepository r = f.getRepository(getApplicationContext());
        Profile p = r.retrieveCached();

        String title = "Trip Finished";
        String message = "";

        String cost = event.getCostString();

        if (p.isPassenger()) {
            message = cost + " will be debited from your credit card.";
        } else if (p.isDriver()) {
            message = cost + " has been charged to the passenger and added to your balance.";
        }

        displayConfirmationDialog(title, message, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });

        cleanState();
        initializeFragment();
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
        removePassengerMarker();
        removeDriverMarker();

        cleanChatLog();

        initializeLocation();
    }

}
