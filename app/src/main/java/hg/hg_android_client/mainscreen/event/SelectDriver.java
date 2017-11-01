package hg.hg_android_client.mainscreen.event;

import hg.hg_android_client.mainscreen.select_driver.Driver;

public class SelectDriver {

    private Driver selected;

    public SelectDriver(Driver driver) {
        this.selected = driver;
    }

    public Driver getDriver() {
        return selected;
    }

}
