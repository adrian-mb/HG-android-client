package hg.hg_android_client.mainscreen.event;

import hg.hg_android_client.mainscreen.driver_idle.Passenger;

public class LoadPassengerInfo {

    private Passenger passenger;

    public LoadPassengerInfo(Passenger passenger) {
        this.passenger = passenger;
    }

    public Passenger getPassenger() {
        return passenger;
    }

}
