package hg.hg_android_client.mainscreen.select_driver;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import org.springframework.http.HttpHeaders;

import java.util.ArrayList;
import java.util.List;

import hg.hg_android_client.mainscreen.select_path.Location;
import hg.hg_android_client.model.Car;
import hg.hg_android_client.model.Profile;
import hg.hg_android_client.model.ProfileBuilder;
import hg.hg_android_client.util.LlevameEndpoint;

public class DriversAroundRepository extends LlevameEndpoint {

    private static final String KEY_ENDPOINT = "endpoint.drivers";

    private final String ENDPOINT;

    protected DriversAroundRepository(Context context) {
        super(context);
        ENDPOINT = getEndpoint(KEY_ENDPOINT);
    }

    public List<Driver> query(String token, Location location) {
        // TODO: Implement drivers query for it to work...

        List<Driver> drivers = new ArrayList<>();

        Profile profileA = new ProfileBuilder()
                .withCountry("Argentina")
                .withFirstName("Pity")
                .withLastName("Alvarez")
                .withDriverCharacter()
                .withAdditionalCar(new Car("ABC111", "Fiat Palio Rojo"))
                .build();

        Driver driverA = new Driver();
        driverA.setProfile(profileA);
        driverA.setLocation(new Location(new LatLng(-34.8034, -58.4490)));

        Profile profileB = new ProfileBuilder()
                .withCountry("Argentina")
                .withFirstName("Pato")
                .withLastName("Fontanet")
                .withDriverCharacter()
                .withAdditionalCar(new Car("ABC123", "Ford Focus Blanco"))
                .build();

        Driver driverB = new Driver();
        driverB.setProfile(profileB);
        driverB.setLocation(new Location(new LatLng(-34.8050, -58.4498)));

        Profile profileC = new ProfileBuilder()
                .withCountry("Argentina")
                .withFirstName("Foo")
                .withLastName("Bar")
                .withDriverCharacter()
                .withAdditionalCar(new Car("ABC133", "Fitito Rosa"))
                .build();

        Driver driverC = new Driver();
        driverC.setProfile(profileC);
        driverC.setLocation(new Location(-34.6176, -58.3680));

        drivers.add(driverA);
        drivers.add(driverB);
        drivers.add(driverC);

        return drivers;
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
