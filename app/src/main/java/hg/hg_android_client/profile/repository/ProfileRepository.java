package hg.hg_android_client.profile.repository;

import hg.hg_android_client.model.Profile;

public interface ProfileRepository {

    Profile retrieve(String token);

    Profile retrieveCached();

    Profile update(String token, Profile profile);

}
