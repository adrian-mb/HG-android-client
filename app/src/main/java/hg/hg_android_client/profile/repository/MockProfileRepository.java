package hg.hg_android_client.profile.repository;

import hg.hg_android_client.model.Car;
import hg.hg_android_client.model.CreditCard;
import hg.hg_android_client.model.Profile;

public class MockProfileRepository implements ProfileRepository {

    @Override
    public Profile retrieve(String token) {
        return null;
    }

    @Override
    public Profile retrieveCached() {
        return null;
    }

    @Override
    public Profile update(String token, Profile user) {
        return null;
    }

}
