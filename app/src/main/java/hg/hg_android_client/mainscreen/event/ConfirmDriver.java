package hg.hg_android_client.mainscreen.event;

import hg.hg_android_client.mainscreen.select_driver.Driver;

public class ConfirmDriver {

    private Driver driver;

    public ConfirmDriver(Driver driver) {
        this.driver = driver;
    }

    public Driver getDriver() {
        return driver;
    }

}
