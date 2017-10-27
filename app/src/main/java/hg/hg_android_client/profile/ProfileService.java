package hg.hg_android_client.profile;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

import hg.hg_android_client.model.Profile;
import hg.hg_android_client.profile.event.RetrieveSuccess;
import hg.hg_android_client.profile.event.UpdateSuccess;
import hg.hg_android_client.profile.intent.RetrieveProfileIntent;
import hg.hg_android_client.profile.intent.UpdateProfileIntent;
import hg.hg_android_client.profile.repository.ProfileRepositoryFactory;

public class ProfileService extends IntentService {

    private static final String SERVICE_NAME = "LlevameProfileService";

    public static final String KEY_TOKEN = "TOKEN";

    public ProfileService() {
        super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }

        String action = intent.getAction();

        switch(action) {
            case RetrieveProfileIntent.ACTION:
                retrieveProfile(takeSessionToken(intent));
                break;
            case UpdateProfileIntent.ACTION:
                Profile profile = (Profile) intent
                        .getSerializableExtra(UpdateProfileIntent.KEY_PROFILE);
                updateProfile(takeSessionToken(intent), profile);
            default:
                break;
        }
    }

    private void retrieveProfile(String token) {
        Profile profile = new ProfileRepositoryFactory()
                .getRepository(getApplicationContext())
                .retrieve(token);

        if (profile != null) {
            RetrieveSuccess e = new RetrieveSuccess(profile);
            EventBus.getDefault().post(e);
        } else {
            // TODO: Handle error case.
        }
    }

    private void updateProfile(String token, Profile profile) {
        Profile updated = new ProfileRepositoryFactory()
                .getRepository(getApplicationContext())
                .update(token, profile);

        if (updated != null) {
            UpdateSuccess e = new UpdateSuccess();
            EventBus.getDefault().post(e);
        } else {
            // TODO: Handle error case.
        }
    }

    private String takeSessionToken(Intent intent) {
        return intent.getStringExtra(KEY_TOKEN);
    }

}
