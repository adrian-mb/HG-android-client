package hg.hg_android_client.mainscreen.select_destination;

import android.content.Context;

import org.springframework.http.HttpHeaders;

import java.text.DecimalFormat;

import hg.hg_android_client.mainscreen.select_path.Location;
import hg.hg_android_client.util.LlevameEndpoint;

public class EstimateEndpoint extends LlevameEndpoint {

    private static final String KEY_ENDPOINT = "endpoint.trip.estimate";

    private final String ENDPOINT;

    protected EstimateEndpoint(Context context) {
        super(context);
        ENDPOINT = getEndpoint(KEY_ENDPOINT);
    }

    public String estimate(String token, Location position, Location destination) {
        HttpHeaders headers = new HttpHeaderBuilder()
                .withAuthToken(token)
                .forJson()
                .build();

        Request request = new Request(position, destination);
        String payload = toJson(request);

        try {
            Response response = post(ENDPOINT, payload, headers, Response.class);
            return stringFromDouble(response.getValue()) + " " + response.getCurrency();
        } catch (Exception e) {
            // TODO: Do something maybe...
        }

        return null;
    }

    private String stringFromDouble(double value) {
        DecimalFormat f = new DecimalFormat("#.00");
        return f.format(value);
    }

    private static final class Request {
        private Loc start;
        private Loc end;

        public Request() {
        }

        public Request(Location start, Location end) {
            this.start = new Loc(start);
            this.end = new Loc(end);
        }

        public Loc getStart() {
            return start;
        }

        public void setStart(Loc start) {
            this.start = start;
        }

        public Loc getEnd() {
            return end;
        }

        public void setEnd(Loc end) {
            this.end = end;
        }

    }

    private static final class Response {
        String currency;
        double value;

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

    }

    private static final class Loc {
        private double lat;
        private double lon;

        public Loc() {
        }

        public Loc(Location location) {
            this.lat = location.getLatitude();
            this.lon = location.getLongitude();
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }

    }

}
