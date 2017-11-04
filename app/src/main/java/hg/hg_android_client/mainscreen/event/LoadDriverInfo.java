package hg.hg_android_client.mainscreen.event;

import hg.hg_android_client.mainscreen.driver_idle.Passenger;
import hg.hg_android_client.mainscreen.select_driver.Driver;

public class LoadDriverInfo {

    private Driver driver;

    public LoadDriverInfo(Driver driver) {
        this.driver = driver;
    }

    public Driver getDriver() {
        return driver;
    }

}
