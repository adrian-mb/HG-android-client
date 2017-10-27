package hg.hg_android_client.profile.repository;

import android.content.Context;

public class ProfileRepositoryFactory {

    public ProfileRepository getRepository(Context context) {
        return new ProfileRepositoryImpl(context);
    }

}
