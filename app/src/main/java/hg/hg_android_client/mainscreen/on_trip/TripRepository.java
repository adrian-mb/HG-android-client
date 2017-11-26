package hg.hg_android_client.mainscreen.on_trip;

import android.content.Context;

import org.springframework.http.HttpHeaders;

import hg.hg_android_client.mainscreen.select_path.Location;
import hg.hg_android_client.util.LlevameEndpoint;

public class TripRepository extends LlevameEndpoint {

    private static final String KEY_ENDPOINT_START = "endpoint.trip.start";
    private static final String KEY_ENDPOINT_CURRENT = "endpoint.trip.current";

    private final String ENDPOINT_START;
    private final String ENDPOINT_CURRENT;

    public TripRepository(Context context) {
        super(context);
        ENDPOINT_START = getEndpoint(KEY_ENDPOINT_START);
        ENDPOINT_CURRENT = getEndpoint(KEY_ENDPOINT_CURRENT);
    }

    public void startTrip(
            String token,
            Location origin,
            Location destination, long passenger) {

        HttpHeaders headers = new HttpHeaderBuilder()
                .withAuthToken(token)
                .forJson()
                .build();

        StartRequest requestObject =
                StartRequest.start(
                        origin,
                        destination,
                        passenger);

        String request = toJson(requestObject);

        try {
            String response = post(ENDPOINT_START, request, headers, String.class);
        } catch (Exception e) {
            // TODO: Do something here.
        }
    }

    public void cancelTrip(String token) {
        HttpHeaders headers = new HttpHeaderBuilder()
                .withAuthToken(token)
                .forJson()
                .build();

        try {
            String response = delete(ENDPOINT_CURRENT, headers, String.class);
        } catch (Exception e) {
            // TODO: Do something here.
        }
    }

    public void notifyInCar(String token) {
        HttpHeaders headers = new HttpHeaderBuilder()
                .withAuthToken(token)
                .forJson()
                .build();

        StatePatch requestObject = StatePatch.create("IN_CAR");
        String request = toJson(requestObject);

        try {
            String response = post(ENDPOINT_CURRENT, request, headers, String.class);
        } catch (Exception e) {
            // TODO: Do something here.
        }
    }

    public void endTrip(String token) {
        HttpHeaders headers = new HttpHeaderBuilder()
                .withAuthToken(token)
                .forJson()
                .build();

        StatePatch requestObject = StatePatch.create("END");
        String request = toJson(requestObject);

        try {
            String response = post(ENDPOINT_CURRENT, request, headers, String.class);
        } catch (Exception e) {
            // TODO: Do something here.
        }
    }

    private static final class StatePatch {
        private String state;

        public static final StatePatch create(String state) {
            StatePatch patch = new StatePatch();
            patch.setState(state);
            return patch;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }

    private static final class StartRequest {
        private long passenger;
        private LocationDescriptor start;
        private LocationDescriptor end;

        public static StartRequest start(
                Location origin,
                Location destination, long passenger) {
            StartRequest req = new StartRequest();
            req.start = LocationDescriptor.fromLocation(origin);
            req.end = LocationDescriptor.fromLocation(destination);
            req.passenger = passenger;
            return req;
        }

        public long getPassenger() {
            return passenger;
        }

        public LocationDescriptor getStart() {
            return start;
        }

        public LocationDescriptor getEnd() {
            return end;
        }

    }

    private static final class LocationDescriptor {
        private String street;
        private TripLocation location;

        public static LocationDescriptor fromLocation(Location location) {
            LocationDescriptor created = new LocationDescriptor();
            created.street = "";
            created.location = new TripLocation(location);
            return created;
        }

        public String getStreet() {
            return street;
        }

        public TripLocation getLocation() {
            return location;
        }

    }

    private static final class TripLocation {
        private double lat;
        private double lon;

        public TripLocation() {
        }

        public TripLocation(Location location) {
            this.lat = location.getLatitude();
            this.lon = location.getLongitude();
        }

        public double getLat() {
            return lat;
        }

        public double getLon() {
            return lon;
        }

    }

}
