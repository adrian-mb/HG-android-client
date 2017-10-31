package hg.hg_android_client.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import hg.hg_android_client.model.jackson.ProfileDeserializer;
import hg.hg_android_client.util.CommonUtil;

@JsonDeserialize(using = ProfileDeserializer.class)
public class Profile implements Serializable {

    private String firstName;
    private String lastName;
    private String country;
    private String birthdate;
    private UserType type;

    private List<Car> cars;

    private ProfileSpecifics specifics;

    Profile() {
        this.cars = new ArrayList<>();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCountry() {
        return country;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getType() {
        return type == null ? null : type.toString();
    }

    public List<Car> getCars() {
        return cars;
    }

    public void setFirstName(String firstname) {
        this.firstName = firstname;
    }

    public void setLastName(String lastname) {
        this.lastName = lastname;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public void setType(String string) {
        if (string != null) {
            this.type = UserType.valueOf(string.toUpperCase());
        } else {
            this.type = null;
        }
    }

    public void makeDriver() {
        this.type = UserType.DRIVER;
        this.specifics = new DriverSpecifics(this);
    }

    public void makePassenger() {
        this.type = UserType.PASSENGER;
        this.specifics = new PassengerSpecifics(this);
    }

    public void addCar(Car car) {
        if (car != null) {
            this.cars.add(car);
        }
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    @JsonIgnore
    public boolean isDriver() {
        return UserType.DRIVER.equals(type);
    }

    @JsonIgnore
    public boolean isPassenger() {
        return UserType.PASSENGER.equals(type);
    }

    @JsonIgnore
    public Car getFirstCar() {
        if (cars != null && cars.size() > 0) {
            return cars.get(0);
        } else {
            return null;
        }
    }

    @JsonIgnore
    public boolean isComplete() {
        String nonnullables[] = {
                firstName,
                lastName,
                country,
                birthdate
        };

        boolean basicsComplete = true;
        for (int i = 0; basicsComplete && i < nonnullables.length; i++) {
            basicsComplete = !CommonUtil.empty(nonnullables[i]);
        }

        return basicsComplete && subprofileComplete();
    }

    @JsonIgnore
    private boolean subprofileComplete() {
        return specifics != null && specifics.detailsComplete();
    }

    public enum UserType {
        DRIVER("driver"),
        PASSENGER("passenger");

        private String stringRepresentation;

        private UserType(String stringRepresentation) {
            this.stringRepresentation = stringRepresentation;
        }

        @Override
        public String toString() {
            return stringRepresentation;
        }
    }

}
