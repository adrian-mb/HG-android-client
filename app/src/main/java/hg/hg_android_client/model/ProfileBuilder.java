package hg.hg_android_client.model;

public class ProfileBuilder {

    Profile built;

    public ProfileBuilder() {
        built = new Profile();
    }

    public ProfileBuilder withFirstName(String firstname) {
        built.setFirstname(firstname);
        return this;
    }

    public ProfileBuilder withLastName(String lastname) {
        built.setLastname(lastname);
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
        built.setType(Profile.UserType.DRIVER);
        return new DriverProfileBuilder();
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

}
