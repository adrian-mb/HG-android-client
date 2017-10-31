package hg.hg_android_client.mainscreen.select_path;

import com.google.android.gms.maps.model.LatLng;

public class Location {
    private double latitude;
    private double longitude;

    public Location() {
        // Required for Jackson serialization.
    }

    public Location(LatLng coordinates) {
        this.latitude = coordinates.latitude;
        this.longitude = coordinates.longitude;
    }

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public LatLng toLatLng() {
        return new LatLng(getLatitude(), getLongitude());
    }

}
