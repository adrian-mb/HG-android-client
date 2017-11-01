package hg.hg_android_client.mainscreen.event;

import java.util.List;

import hg.hg_android_client.mainscreen.select_driver.Driver;

public class DriversAroundResponse {

    private List<Driver> drivers;

    public DriversAroundResponse(List<Driver> drivers) {
        this.drivers = drivers;
    }

    public List<Driver> getDrivers() {
        return drivers;
    }

}
