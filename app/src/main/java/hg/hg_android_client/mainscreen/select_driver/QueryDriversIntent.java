package hg.hg_android_client.mainscreen.select_driver;

import android.content.Context;
import android.content.Intent;

import hg.hg_android_client.mainscreen.select_path.Location;

public class QueryDriversIntent extends Intent {

    private Intent wrapped;

    private static final String KEY_LOCATION = "KEY_LOCATION";

    public QueryDriversIntent(Intent wrapped) {
        this.wrapped = wrapped;
    }

    public QueryDriversIntent(Context context, Location location) {
        super(context, DriversQueryService.class);
        putExtra(KEY_LOCATION, doubleArray(location));
    }

    public Location getLocation() {
        double[] position;
        if (wrapped == null) {
            position = getDoubleArrayExtra(KEY_LOCATION);
        } else {
            position = wrapped.getDoubleArrayExtra(KEY_LOCATION);
        }
        return new Location(position[0], position[1]);
    }

    private double[] doubleArray(Location location) {
        double position[] = new double[2];
        position[0] = location.getLatitude();
        position[1] = location.getLongitude();
        return position;
    }

}
