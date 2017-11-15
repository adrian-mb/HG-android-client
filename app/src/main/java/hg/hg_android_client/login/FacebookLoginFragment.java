package hg.hg_android_client.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import hg.hg_android_client.R;
import hg.hg_android_client.login.event.AuthSuccess;
import hg.hg_android_client.login.event.DisplayLoginProgress;
import hg.hg_android_client.login.event.FacebookAuthSuccess;
import hg.hg_android_client.login.event.HideLoginProgress;
import hg.hg_android_client.login.intent.FacebookAuthenticationIntent;
import hg.hg_android_client.util.LlevameFragment;

public class FacebookLoginFragment extends LlevameFragment
        implements FacebookCallback<LoginResult> {

    private CallbackManager callbackManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View view = inflater.inflate(R.layout.fragment_facebook_login, container, false);
        initializeButton(findButton(view));
        return view;
    }

    private LoginButton findButton(View parentView) {
        return (LoginButton) parentView.findViewById(R.id.facebook_login_button);
    }

    private void initializeButton(LoginButton button) {
        button.setFragment(this);
        button.registerCallback(callbackManager, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        EventBus.getDefault().post(new DisplayLoginProgress());

        Context context = getContext();

        FacebookAuthenticationIntent intent =
                new FacebookAuthenticationIntent(
                        context.getApplicationContext(),
                        loginResult.getAccessToken().getUserId(),
                        loginResult.getAccessToken().getToken());

        context.startService(intent);
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onError(FacebookException error) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFacebookAuthSuccessEvent(FacebookAuthSuccess event) {
        EventBus.getDefault().post(new HideLoginProgress());
        EventBus.getDefault().post(new AuthSuccess(event.getToken()));
    }

}
