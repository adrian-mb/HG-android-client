package hg.hg_android_client.profile.repository;

import android.content.Context;
import android.content.SharedPreferences;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import hg.hg_android_client.model.Profile;
import hg.hg_android_client.util.LlevameEndpoint;

public class ProfileRepositoryImpl extends LlevameEndpoint implements ProfileRepository {

    // TODO Implement JSON response management.
    // TODO Create some user factory.

    private final String KEY_ENDPOINT = "endpoint.profile";
    private final String ENDPOINT;

    private final String KEY_PROFILE_PREFERENCES = "KEY_PROFILE_STORAGE";
    private final String KEY_PROFILE = "CACHED_PROFILE";

    private SharedPreferences storage;

    protected ProfileRepositoryImpl(Context context) {
        super(context);
        ENDPOINT = getEndpoint(KEY_ENDPOINT);
        initializeStorage(context);
    }

    private void initializeStorage(Context context) {
        storage = context.getSharedPreferences(KEY_PROFILE_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public Profile retrieve(String token) {
        HttpHeaders headers = new HttpHeaderBuilder()
                .forJson().withAuthToken(token).build();
        try {
            Profile profile = get(ENDPOINT, "", headers, Profile.class);
            cacheUser(profile);
            return profile;
        } catch(Exception e) {
            return null;
        }
    }

    @Override
    public Profile retrieveCached() {
        String cachedJsonUser = storage.getString(KEY_PROFILE, "");
        return fromJson(cachedJsonUser, Profile.class);
    }

    @Override
    public Profile update(String token, Profile user) {
        HttpHeaders headers = new HttpHeaderBuilder()
                .forJson().withAuthToken(token).build();

        String request = toJson(user);

        try {
            Profile updated = post(ENDPOINT, request, headers, Profile.class);
            return updated;
        } catch (UnauthorizedAccessException e) {
            return null;
        } catch (Exception e) {
            // TODO Handle this case.
            return null;
        }
    }

    private void cacheUser(Profile profile) {
        storage.edit().putString(KEY_PROFILE, toJson(profile)).commit();
    }

}
