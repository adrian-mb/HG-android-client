package hg.hg_android_client.mainscreen.select_driver;

import android.content.Context;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.android.gms.maps.model.LatLng;

import org.springframework.http.HttpHeaders;

import java.util.ArrayList;
import java.util.List;

import hg.hg_android_client.mainscreen.select_path.Location;
import hg.hg_android_client.model.Car;
import hg.hg_android_client.model.Profile;
import hg.hg_android_client.model.ProfileBuilder;
import hg.hg_android_client.util.JsonTransform;
import hg.hg_android_client.util.LlevameEndpoint;

public class DriversAroundRepository extends LlevameEndpoint {

    private static final String KEY_ENDPOINT = "endpoint.drivers";

    private final String ENDPOINT;

    protected DriversAroundRepository(Context context) {
        super(context);
        ENDPOINT = getEndpoint(KEY_ENDPOINT);
    }

    public List<Driver> query(String token) {
        HttpHeaders headers = new HttpHeaderBuilder()
                .forJson()
                .withAuthToken(token)
                .build();

        try {
            String response = get(ENDPOINT, "", headers, String.class);
            TypeReference<List<Driver>> r = new TypeReference<List<Driver>>() {};
            JsonTransform t = new JsonTransform();
            return t.fromJson(response, r);
        } catch (Exception e) {
            return new ArrayList<>();
        }

    }

    private static final class Request {
        private Location location;

        Request(Location location) {
            this.location = location;
        }

        public Location getLocation() {
            return location;
        }
    }

    private static final class Response {
        private List<Profile> drivers;

        private Response() {
            this.drivers = new ArrayList<>();
        }

        public void setDrivers(List<Profile> drivers) {
            this.drivers.clear();
            this.drivers.addAll(drivers);
        }

        public List<Profile> getDrivers() {
            return drivers;
        }
    }

}
