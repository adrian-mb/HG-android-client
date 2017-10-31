package hg.hg_android_client.mainscreen.select_path;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.maps.model.LatLng;

public class PathQueryIntent extends Intent {

    private Intent wrapped;

    private static final String KEY_ORIGIN = "KEY_ORIGIN";
    private static final String KEY_DESTINATION = "KEY_DESTINATION";

    public PathQueryIntent(Intent wrapped) {
        this.wrapped = wrapped;
    }

    public PathQueryIntent(Context context, LatLng origin, LatLng destination) {
        super(context, PathFindingService.class);
        putExtra(KEY_ORIGIN, doubleArray(origin));
        putExtra(KEY_DESTINATION, doubleArray(destination));
    }

    private double[] doubleArray(LatLng position) {
        double array[] = new double[2];
        array[0] = position.latitude;
        array[1] = position.longitude;
        return array;
    }

    public LatLng getOrigin() {
        return takePoint(KEY_ORIGIN);
    }

    public LatLng getDestination() {
        return takePoint(KEY_DESTINATION);
    }

    public LatLng takePoint(String key) {
        double point[];
        if (wrapped != null) {
            point = wrapped.getDoubleArrayExtra(key);
        } else {
            point = getDoubleArrayExtra(key);
        }
        return new LatLng(point[0], point[1]);
    }

}
