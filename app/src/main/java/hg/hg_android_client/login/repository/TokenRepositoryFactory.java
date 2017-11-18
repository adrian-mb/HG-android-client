package hg.hg_android_client.login.repository;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenRepositoryFactory {

    private static final String KEY_TOKEN_PREFERENCES = "KEY_TOKEN_PREFERENCES";

    public TokenRepository getRepository(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                KEY_TOKEN_PREFERENCES, Context.MODE_PRIVATE);
        return new TokenRepositoryImpl(context, preferences);
    }

}
