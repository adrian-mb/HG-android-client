package hg.hg_android_client.mainscreen.event;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class UpdateLocation {

    private LatLng location;

    public UpdateLocation(LatLng location) {
        this.location = location;
    }

    public UpdateLocation(Location location) {
        this.location = new LatLng(location.getLatitude(), location.getLongitude());
    }

    public LatLng getLocation() {
        return location;
    }

}
