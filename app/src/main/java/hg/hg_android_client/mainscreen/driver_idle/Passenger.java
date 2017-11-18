package hg.hg_android_client.mainscreen.driver_idle;

import hg.hg_android_client.mainscreen.select_path.Location;
import hg.hg_android_client.model.Profile;

public class Passenger {
    private long userId;
    private String name;
    private Location location;

    public static Passenger from(Profile profile, Location location) {
        Long userId = profile.getUserId();
        String name = profile.getFullName();

        Passenger p = new Passenger();
        p.setUserId(userId);
        p.setName(name);
        p.setLocation(location);

        return p;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

}
