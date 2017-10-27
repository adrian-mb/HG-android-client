package hg.hg_android_client.profile.intent;

import android.content.Context;
import android.content.Intent;

import hg.hg_android_client.model.Profile;
import hg.hg_android_client.profile.ProfileService;

public class UpdateProfileIntent extends Intent {

    public static final String ACTION = "hg.hg_android_client.UPDATE_PROFILE";

    public static final String KEY_PROFILE = "PROFILE";

    public UpdateProfileIntent(Context context, String token, Profile profile) {
        super(context, ProfileService.class);
        setAction(ACTION);
        putExtra(KEY_PROFILE, profile);
        putExtra(ProfileService.KEY_TOKEN, token);
    }

}
