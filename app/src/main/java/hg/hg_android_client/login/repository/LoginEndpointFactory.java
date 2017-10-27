package hg.hg_android_client.login.repository;

import android.content.Context;

public class LoginEndpointFactory {

    public LoginEndpoint getEndpoint(Context context) {
        return new LoginEndpointImpl(context);
    }

}
