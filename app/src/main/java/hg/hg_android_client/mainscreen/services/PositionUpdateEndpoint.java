package hg.hg_android_client.mainscreen.services;

import android.content.Context;

import org.springframework.http.HttpHeaders;

import hg.hg_android_client.mainscreen.select_path.Location;
import hg.hg_android_client.util.LlevameEndpoint;

public class PositionUpdateEndpoint extends LlevameEndpoint {

    private static final String ENDPOINT_KEY = "endpoint.position";

    private final String ENDPOINT;

    private static final class Request {
        private double latitude;
        private double longitude;

        public Request(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }

    protected PositionUpdateEndpoint(Context context) {
        super(context);
        ENDPOINT = getEndpoint(ENDPOINT_KEY);
    }

    public void updateLocation(String authToken, Location location) {
        HttpHeaders headers = new HttpHeaderBuilder()
                .withAuthToken(authToken)
                .forJson()
                .build();

        String request = toJson(new Request(
                location.getLatitude(),
                location.getLongitude()));

        try {
            String response = post(ENDPOINT, request, headers, String.class);
        } catch (Exception e) {
            // TODO: Handle error case
        }
    }

}
