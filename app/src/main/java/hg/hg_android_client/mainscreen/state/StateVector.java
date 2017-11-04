package hg.hg_android_client.mainscreen.state;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;

import hg.hg_android_client.mainscreen.driver_idle.Passenger;
import hg.hg_android_client.mainscreen.event.LoadDriverInfo;
import hg.hg_android_client.mainscreen.event.LoadPassengerInfo;
import hg.hg_android_client.mainscreen.event.UpdateLocation;
import hg.hg_android_client.mainscreen.event.ShowPath;
import hg.hg_android_client.mainscreen.event.UpdateDestination;
import hg.hg_android_client.mainscreen.repository.StateRepository;
import hg.hg_android_client.mainscreen.repository.StateRepositoryFactory;
import hg.hg_android_client.mainscreen.select_driver.Driver;
import hg.hg_android_client.mainscreen.select_path.Path;

public class StateVector {

    private StateKey key;

    private LatLng origin;
    private LatLng destination;

    private Path path;

    private Driver driver;
    private Passenger passenger;

    private StateVector() {
    }

    public static StateVector load(Context context) {
        StateRepository r = new StateRepositoryFactory().get(context);
        StateVector v = new StateVector();
        v.setKey(r.getKey());
        v.setOrigin(r.getOrigin());
        v.setDestination(r.getDestination());
        v.setPath(r.getPath());
        v.setDriver(r.getDriver());
        v.setPassenger(r.getPassenger());
        return v;
    }

    public void setKey(StateKey key) {
        this.key = key;
    }

    public void setOrigin(LatLng origin) {
        this.origin = origin;
    }

    public void setDestination(LatLng destination) {
        this.destination = destination;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public boolean isPassengerPickingDestination() {
        return StateKey.PASSENGER_SELECT_DESTINATION.equals(key);
    }

    public boolean isPassengerPickingPath() {
        return StateKey.PASSENGER_SELECT_PATH.equals(key);
    }

    public boolean isPassengerPickingDriver() {
        return StateKey.PASSENGER_SELECT_DRIVER.equals(key);
    }

    public boolean isPassengerWaitingConfirmation() {
        return StateKey.PASSENGER_WAIT_FOR_TRIP_CONFIRMATION.equals(key);
    }

    public boolean isDriverWaitingTripRequest() {
        return StateKey.DRIVER_WAIT_FOR_TRIP_REQUEST.equals(key);
    }

    public boolean isDriverEvaluatingTripRequest() {
        return StateKey.DRIVER_CONFIRM_TRIP.equals(key);
    }

    public boolean isDriverMeetingPassenger() {
        return StateKey.DRIVER_MEET_PASSENGER.equals(key);
    }

    public boolean isPassengerWaitingForDriver() {
        return StateKey.PASSENGER_WAIT_FOR_DRIVER.equals(key);
    }

    public boolean isOnTrip() {
        return StateKey.ON_TRIP.equals(key);
    }

    public void propagate() {
        if (origin != null) {
            UpdateLocation e = new UpdateLocation(origin);
            EventBus.getDefault().post(e);
        }
        if (destination != null) {
            UpdateDestination e = new UpdateDestination(destination);
            EventBus.getDefault().post(e);
        }
        if (path != null) {
            ShowPath e = new ShowPath(path);
            EventBus.getDefault().post(e);
        }
        if (passenger != null && isDriverMeetingPassenger()) {
            LoadPassengerInfo e = new LoadPassengerInfo(passenger);
            EventBus.getDefault().post(e);
        }
        if (driver != null && isPassengerWaitingForDriver()) {
            LoadDriverInfo e = new LoadDriverInfo(driver);
            EventBus.getDefault().post(e);
        }
    }

    public void persist(Context context) {
        StateRepository r = new StateRepositoryFactory().get(context);
        r.save(key);
        r.saveOrigin(origin);
        r.saveDestination(destination);
        r.savePath(path);
        r.saveDriver(driver);
        r.savePassenger(passenger);
    }

    public void clear(Context context) {
        new StateRepositoryFactory().get(context).clear();
    }

}
