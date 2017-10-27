package hg.hg_android_client.login.repository;

import android.content.Context;

public class RegistrationEndpointFactory {

    public RegistrationEndpoint getEndpoint(Context context) {
        return new RegistrationEndpointImpl(context);
    }

}
