package hg.hg_android_client.profile.event;

import hg.hg_android_client.model.Profile;

public class RetrieveSuccess {

    private Profile profile;

    public RetrieveSuccess(Profile user) {
        this.profile = user;
    }

    public Profile getProfile() {
        return profile;
    }

}
