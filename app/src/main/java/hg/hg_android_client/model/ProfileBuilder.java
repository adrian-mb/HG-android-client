package hg.hg_android_client.model;

public class ProfileBuilder {

    Profile built;

    public ProfileBuilder() {
        built = new Profile();
    }

    public ProfileBuilder withUserId(Long userId) {
        built.setUserId(userId);
        return this;
    }

    public ProfileBuilder withFirstName(String firstname) {
        built.setFirstName(firstname);
        return this;
    }

    public ProfileBuilder withLastName(String lastname) {
        built.setLastName(lastname);
        return this;
    }

    public ProfileBuilder withCountry(String country) {
        built.setCountry(country);
        return this;
    }

    public ProfileBuilder withBirthdate(String birthdate) {
        built.setBirthdate(birthdate);
        return this;
    }

    public DriverProfileBuilder withDriverCharacter() {
        built.makeDriver();
        return new DriverProfileBuilder();
    }

    public PassengerProfileBuilder withPassengerCharacter() {
        built.makePassenger();
        return new PassengerProfileBuilder();
    }

    public Profile build() {
        return built;
    }

    public class DriverProfileBuilder {

        public DriverProfileBuilder withAdditionalCar(Car car) {
            built.addCar(car);
            return this;
        }

        public Profile build() {
            return ProfileBuilder.this.build();
        }

    }

    public class PassengerProfileBuilder {

        public Profile build() {
            return ProfileBuilder.this.build();
        }

    }

}
