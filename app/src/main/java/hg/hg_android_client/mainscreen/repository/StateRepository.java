package hg.hg_android_client.mainscreen.repository;

import com.google.android.gms.maps.model.LatLng;

import hg.hg_android_client.mainscreen.select_driver.Driver;
import hg.hg_android_client.mainscreen.state.StateKey;
import hg.hg_android_client.mainscreen.select_path.Path;

public interface StateRepository {

    void save(StateKey state);

    void saveOrigin(LatLng origin);
    void saveDestination(LatLng destination);

    void savePath(Path path);

    void saveDriver(Driver driver);

    LatLng getOrigin();
    LatLng getDestination();

    Path getPath();

    Driver getDriver();

    void clear();

    StateKey getKey();

}
