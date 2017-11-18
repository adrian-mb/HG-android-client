package hg.hg_android_client.login.repository;

import android.content.Context;
import android.content.SharedPreferences;

import org.springframework.http.HttpHeaders;

import hg.hg_android_client.util.LlevameEndpoint;

public class TokenRepositoryImpl extends LlevameEndpoint
        implements TokenRepository {

    private SharedPreferences preferences;

    private static final String KEY_TOKEN = "TOKEN";

    private static final String ENDPOINT_KEY = "endpoint.login";
    private final String ENDPOINT_DELETE;

    public TokenRepositoryImpl(Context context, SharedPreferences preferences) {
        super(context);
        this.preferences = preferences;
        this.ENDPOINT_DELETE = getEndpoint(ENDPOINT_KEY);
    }

    @Override
    public String getToken() {
        return preferences.getString(KEY_TOKEN, null);
    }

    @Override
    public void updateToken(String token) {
        preferences.edit().putString(KEY_TOKEN, token).commit();
    }

    @Override
    public void deleteToken() {
        HttpHeaders headers = new HttpHeaderBuilder()
                .withAuthToken(getToken())
                .build();

        try {
            String response = delete(ENDPOINT_DELETE, headers, String.class);
            preferences.edit().remove(KEY_TOKEN).commit();
        } catch (Exception e) {
            // TODO: Handle this.
        }
    }

}
