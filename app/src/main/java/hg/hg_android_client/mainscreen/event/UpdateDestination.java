package hg.hg_android_client.mainscreen.event;

import com.google.android.gms.maps.model.LatLng;

public class UpdateDestination {

    private LatLng position;

    public UpdateDestination(LatLng position) {
        this.position = position;
    }

    public LatLng getPosition() {
        return position;
    }

}
