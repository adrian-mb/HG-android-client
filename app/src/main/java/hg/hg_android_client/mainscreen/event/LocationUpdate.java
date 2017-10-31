package hg.hg_android_client.mainscreen.event;

import android.location.Location;

public class LocationUpdate {

    private Location location;

    public LocationUpdate(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

}
