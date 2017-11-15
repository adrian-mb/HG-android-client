package hg.hg_android_client.mainscreen.select_path;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public interface PathRepository {
    public List<Path> query(String token, LatLng origin, LatLng destination);
}
