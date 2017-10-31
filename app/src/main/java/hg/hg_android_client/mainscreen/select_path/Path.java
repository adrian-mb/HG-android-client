package hg.hg_android_client.mainscreen.select_path;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.LinkedList;
import java.util.List;

import hg.hg_android_client.util.JsonTransform;

public class Path {

    private List<Location> serializable;
    private List<LatLng> points;

    public Path() {
        this.points = new LinkedList<>();
        this.serializable = new LinkedList<>();
    }

    public Path(List<Location> original) {
        this.points = new LinkedList<>();
        this.serializable = original;
        for (Location current : original) {
            this.points.add(current.toLatLng());
        }
    }

    public void add(LatLng point) {
        points.add(point);
        serializable.add(new Location(point));
    }

    public Polyline addTo(GoogleMap map) {
        PolylineOptions options = new PolylineOptions();
        options.addAll(points);
        return map.addPolyline(options);
    }

    public String toJson() {
        JsonTransform t = new JsonTransform();
        return t.toJson(serializable);
    }

    public static Path fromJson(String json) {
        JsonTransform t = new JsonTransform();
        List<Location> serializable = t.fromJson(json, new TypeReference<List<Location>>(){});
        return new Path(serializable);
    }

}
