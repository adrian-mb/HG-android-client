package hg.hg_android_client.mainscreen.repository;

import com.google.android.gms.maps.model.LatLng;

import hg.hg_android_client.mainscreen.state.StateKey;
import hg.hg_android_client.mainscreen.select_path.Path;

public interface StateRepository {

    void save(StateKey state);

    void saveOrigin(LatLng origin);
    void saveDestination(LatLng destination);

    void savePath(Path path);

    LatLng getOrigin();
    LatLng getDestination();

    Path getPath();

    void clear();

    StateKey getKey();

}
