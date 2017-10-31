package hg.hg_android_client.mainscreen.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;

import hg.hg_android_client.mainscreen.state.StateKey;
import hg.hg_android_client.mainscreen.select_path.Location;
import hg.hg_android_client.mainscreen.select_path.Path;
import hg.hg_android_client.model.Profile;
import hg.hg_android_client.profile.repository.ProfileRepository;
import hg.hg_android_client.profile.repository.ProfileRepositoryFactory;
import hg.hg_android_client.util.JsonTransform;

public class StateRepositoryImpl implements StateRepository {

    private static final String KEY_STATE_PREFERENCES = "KEY_STATE_PREFERENCES";

    private static final String KEY_STATE = "KEY_STATE";

    private static final String KEY_ORIGIN = "KEY_ORIGIN";
    private static final String KEY_DESTINATION = "KEY_DESTINATION";
    private static final String KEY_PATH = "KEY_PATH";

    private Profile profile;
    private SharedPreferences storage;

    public StateRepositoryImpl(Context context) {
        profile = retrieveCachedProfile(context);
        storage = context.getSharedPreferences(KEY_STATE_PREFERENCES, Context.MODE_PRIVATE);
    }

    private Profile retrieveCachedProfile(Context context) {
        ProfileRepositoryFactory f = new ProfileRepositoryFactory();
        ProfileRepository r = f.getRepository(context);
        return r.retrieveCached();
    }

    @Override
    public void save(StateKey state) {
        storage.edit().putString(KEY_STATE, state.name()).commit();
    }

    @Override
    public void saveOrigin(LatLng origin) {
        Location location = new Location(origin.latitude, origin.longitude);
        storage.edit().putString(KEY_ORIGIN, serialize(location)).commit();
    }

    @Override
    public void saveDestination(LatLng destination) {
        Location location = new Location(destination.latitude, destination.longitude);
        storage.edit().putString(KEY_DESTINATION, serialize(location)).commit();
    }

    @Override
    public void savePath(Path path) {
        String json = path.toJson();
        storage.edit().putString(KEY_PATH, json).commit();
    }

    @Override
    public LatLng getOrigin() {
        return getLatLng(KEY_ORIGIN);
    }

    @Override
    public LatLng getDestination() {
        return getLatLng(KEY_DESTINATION);
    }

    @Override
    public Path getPath() {
        if (storage.contains(KEY_PATH)) {
            String json = storage.getString(KEY_PATH, null);
            return Path.fromJson(json);
        } else {
            return null;
        }
    }

    private LatLng getLatLng(String key) {
        if (storage.contains(key)) {
            String json = storage.getString(key, null);
            Location location = deserialize(json, Location.class);
            return new LatLng(location.getLatitude(), location.getLongitude());
        } else {
            return null;
        }
    }

    @Override
    public void clear() {
        storage.edit()
                .remove(KEY_STATE)
                .remove(KEY_ORIGIN)
                .remove(KEY_DESTINATION)
                .remove(KEY_PATH)
                .commit();
    }

    @Override
    public StateKey getKey() {
        String value = storage.getString(KEY_STATE, null);
        return value == null ? fromProfile() : StateKey.valueOf(value);
    }

    private StateKey fromProfile() {
        if (profile.isDriver()) {
            return StateKey.DRIVER_WAIT_FOR_TRIP_REQUEST;
        } else if (profile.isPassenger()) {
            return StateKey.PASSENGER_SELECT_DESTINATION;
        } else {
            return null;
        }
    }

    private String serialize(Object object) {
        JsonTransform t = new JsonTransform();
        return t.toJson(object);
    }

    private <T> T deserialize(String json, Class<T> type) {
        JsonTransform t = new JsonTransform();
        return t.fromJson(json, type);
    }

}
