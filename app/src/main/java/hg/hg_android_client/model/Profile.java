package hg.hg_android_client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import hg.hg_android_client.model.jackson.ProfileDeserializer;
import hg.hg_android_client.util.CommonUtil;

@JsonDeserialize(using = ProfileDeserializer.class)
public class Profile implements Serializable {

    private String firstname;
    private String lastname;
    private String country;
    private String birthdate;
    private UserType type;

    private List<Car> cars;

    private ProfileSpecifics specifics;

    Profile() {
        this.cars = new ArrayList<>();
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
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

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
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

    public void setType(UserType type) {
        this.type = type;
    }

    public void addCar(Car car) {
        if (car != null) {
            this.cars.add(car);
        }
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public boolean isDriver() {
        return UserType.DRIVER.equals(type);
    }

    public boolean isPassenger() {
        return UserType.PASSENGER.equals(type);
    }

    public Car getFirstCar() {
        if (cars != null && cars.size() > 0) {
            return cars.get(0);
        } else {
            return null;
        }
    }

    public boolean isComplete() {
        String nonnullables[] = {
                firstname,
                lastname,
                country,
                birthdate
        };

        boolean basicsComplete = true;
        for (int i = 0; basicsComplete && i < nonnullables.length; i++) {
            basicsComplete = !CommonUtil.empty(nonnullables[i]);
        }

        return basicsComplete && subprofileComplete();
    }

    private boolean subprofileComplete() {
        return specifics != null && specifics.detailsComplete();
    }

    public enum UserType {
        DRIVER("DRIVER"),
        PASSENGER("PASSENGER");

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
