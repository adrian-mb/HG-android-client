package hg.hg_android_client.mainscreen.driver_idle;

import hg.hg_android_client.mainscreen.select_path.Location;
import hg.hg_android_client.model.Profile;

public class Passenger {
    private Profile profile;
    private Location location;

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Profile getProfile() {
        return profile;
    }

    public Location getLocation() {
        return location;
    }

}
