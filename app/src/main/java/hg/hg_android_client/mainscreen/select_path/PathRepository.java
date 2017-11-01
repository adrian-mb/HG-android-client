package hg.hg_android_client.mainscreen.select_path;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import org.springframework.http.HttpHeaders;

import java.util.ArrayList;
import java.util.List;

import hg.hg_android_client.util.LlevameEndpoint;

public class PathRepository extends LlevameEndpoint {

    private static final String KEY_ENDPOINT_PATH = "endpoint.path";

    private final String ENDPOINT;

    public PathRepository(Context context) {
        super(context);
        ENDPOINT = getEndpoint(KEY_ENDPOINT_PATH);
    }

    public List<Path> query(String token, LatLng origin, LatLng destination) {
        // TODO Implement path query for this to work...
        // Response r = getKey(ENDPOINT, toJson(request), headers, Response.class);
        // return transformPaths(r.getPaths());

        Path p1 = new Path();
        p1.add(origin);
        p1.add(destination);

        Path p2 = new Path();
        p2.add(origin);
        p2.add(destination);

        List<Path> l = new ArrayList<>();
        l.add(p1);
        l.add(p2);
        return l;
    }

    private List<Path> transformPaths(List<List<Location>> paths) {
        List<Path> transformed = new ArrayList<>();
        for (List<Location> current : paths) {
            Path path = new Path(current);
            transformed.add(path);
        }
        return transformed;
    }

    private Request createRequest(LatLng origin, LatLng destination) {
        Location pOrigin = new Location(origin);
        Location pDestination = new Location(destination);
        return new Request(pOrigin, pDestination);
    }

    private static final class Request {
        private Location origin;
        private Location destination;

        Request(Location origin, Location destination) {
            this.origin = origin;
            this.destination = destination;
        }

        public Location getOrigin() {
            return origin;
        }

        public Location getDestination() {
            return destination;
        }

    }

    private static final class Response {
        private List<List<Location>> paths;

        public void setPaths(List<List<Location>> paths) {
            this.paths = paths;
        }

        public List<List<Location>> getPaths() {
            return paths;
        }

    }

}
