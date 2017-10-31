package hg.hg_android_client.mainscreen.state;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;

import hg.hg_android_client.mainscreen.event.LocationUpdate;
import hg.hg_android_client.mainscreen.event.ShowPath;
import hg.hg_android_client.mainscreen.event.UpdateDestination;
import hg.hg_android_client.mainscreen.repository.StateRepository;
import hg.hg_android_client.mainscreen.repository.StateRepositoryFactory;
import hg.hg_android_client.mainscreen.select_path.Location;
import hg.hg_android_client.mainscreen.select_path.Path;

public class StateVector {

    private StateKey key;

    private LatLng origin;
    private LatLng destination;

    private Path path;

    private StateVector() {
    }

    public static StateVector load(Context context) {
        StateRepository r = new StateRepositoryFactory().get(context);
        StateVector v = new StateVector();
        v.setKey(r.getKey());
        v.setOrigin(r.getOrigin());
        v.setDestination(r.getDestination());
        v.setPath(r.getPath());
        return v;
    }

    public void setKey(StateKey key) {
        this.key = key;
    }

    public void setOrigin(LatLng origin) {
        this.origin = origin;
    }

    public void setDestination(LatLng destination) {
        this.destination = destination;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public void propagate() {
        if (destination != null) {
            UpdateDestination e = new UpdateDestination(destination);
            EventBus.getDefault().post(e);
        }
        if (path != null) {
            ShowPath e = new ShowPath(path);
            EventBus.getDefault().post(e);
        }
    }

    public void persist(Context context) {
        StateRepository r = new StateRepositoryFactory().get(context);
        r.save(key);
        r.saveOrigin(origin);
        r.saveDestination(destination);
        r.savePath(path);
    }

}
