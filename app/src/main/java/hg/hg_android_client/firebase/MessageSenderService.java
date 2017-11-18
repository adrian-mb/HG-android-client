package hg.hg_android_client.firebase;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

import hg.hg_android_client.login.event.AuthFailure;
import hg.hg_android_client.login.event.AuthSuccess;
import hg.hg_android_client.login.event.FacebookAuthSuccess;
import hg.hg_android_client.login.event.LogoutSuccess;
import hg.hg_android_client.login.event.RegistrationFailed;
import hg.hg_android_client.login.event.RegistrationSuccess;
import hg.hg_android_client.login.intent.FacebookAuthenticationIntent;
import hg.hg_android_client.login.intent.LoginIntent;
import hg.hg_android_client.login.intent.LogoutIntent;
import hg.hg_android_client.login.intent.RegistrationIntent;
import hg.hg_android_client.login.repository.FacebookLoginEndpoint;
import hg.hg_android_client.login.repository.LoginEndpoint;
import hg.hg_android_client.login.repository.LoginEndpointFactory;
import hg.hg_android_client.login.repository.RegistrationEndpoint;
import hg.hg_android_client.login.repository.RegistrationEndpointFactory;
import hg.hg_android_client.login.repository.TokenRepository;
import hg.hg_android_client.login.repository.TokenRepositoryFactory;
import hg.hg_android_client.mainscreen.repository.StateRepository;
import hg.hg_android_client.mainscreen.repository.StateRepositoryFactory;

public class MessageSenderService extends IntentService {

    private static final String SERVICE_NAME = "MessageSenderService";

    public static final String KEY_PAYLOAD = "KEY_PAYLOAD";
    public static final String KEY_TYPE = "KEY_TYPE";
    public static final String KEY_TARGET = "KEY_TARGET";


    public MessageSenderService() {
        super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }

        final String type = intent.getStringExtra(KEY_TYPE);
        final String payload = intent.getStringExtra(KEY_PAYLOAD);
        final Long target = intent.getLongExtra(KEY_TARGET, 0);

        final MessagingEndpoint endpoint = new MessagingEndpoint(getApplicationContext());

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... args) {
                endpoint.send(getAuthToken(), target, type, payload);;
                return null;
            }
        }.execute();
    }

    private String getAuthToken() {
        TokenRepositoryFactory f = new TokenRepositoryFactory();
        TokenRepository r = f.getRepository(getApplicationContext());
        return r.getToken();
    }

}
