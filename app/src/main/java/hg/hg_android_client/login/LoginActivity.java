package hg.hg_android_client.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import hg.hg_android_client.R;
import hg.hg_android_client.login.event.AuthFailure;
import hg.hg_android_client.login.event.AuthSuccess;
import hg.hg_android_client.login.event.DisplayLoginProgress;
import hg.hg_android_client.login.event.HideLoginProgress;
import hg.hg_android_client.login.intent.LoginIntent;
import hg.hg_android_client.login.repository.TokenRepository;
import hg.hg_android_client.login.repository.TokenRepositoryFactory;
import hg.hg_android_client.mainscreen.MainScreenActivity;
import hg.hg_android_client.model.User;
import hg.hg_android_client.profile.ProfileActivity;
import hg.hg_android_client.profile.event.RetrieveSuccess;
import hg.hg_android_client.profile.intent.RetrieveProfileIntent;
import hg.hg_android_client.profile.repository.ProfileRepository;
import hg.hg_android_client.profile.repository.ProfileRepositoryFactory;
import hg.hg_android_client.util.LlevameActivity;
import hg.hg_android_client.util.UiReader;

public class LoginActivity extends LlevameActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPreviousSession();
        setContentView(R.layout.activity_login);
    }

    private void checkPreviousSession() {
        TokenRepositoryFactory f = new TokenRepositoryFactory();
        TokenRepository r = f.getRepository(getApplicationContext());
        String token = r.getToken();
        if (token != null) {
            retrieveCachedProfile();
        }
    }

    private void displayProgressDialog() {
        UiReader reader = new UiReader(this);
        showDialog(reader.readString(R.string.login_progress_title),
                   reader.readString(R.string.login_progress_message));
    }

    public void signupOnClick(View view) {
        Intent i = new Intent(getApplicationContext(), RegistrationActivity.class);
        startActivity(i);
    }

    public void loginOnClick(View view) {
        hideKeyboard();
        displayProgressDialog();

        UiReader reader = new UiReader(this);

        String username = reader.readEditText(R.id.person_username);
        String password = reader.readEditText(R.id.person_password);

        Intent i = new LoginIntent(this, username, password);
        this.startService(i);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDisplayLoginProgress(DisplayLoginProgress e) {
        displayProgressDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHideLoginProgress(HideLoginProgress e) {
        dismissDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAuthSuccess(AuthSuccess event) {
        retrieveProfile(event.getToken());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAuthFailure(AuthFailure event) {
        dismissDialog();
        String message = event.getMessage();
        String buttonm = getString(R.string.OK);

        displayConfirmationDialog(
                message,
                buttonm,
                new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int i) {
                        d.dismiss();
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRetrieveSuccess(RetrieveSuccess event) {
        dismissDialog();
        User user = event.getUser();
        evaluateUserProfile(user);
    }

    private void retrieveProfile(String token) {
        Context context = getApplicationContext();
        RetrieveProfileIntent i = new RetrieveProfileIntent(context, token);
        this.startService(i);
    }

    private void retrieveCachedProfile() {
        ProfileRepositoryFactory f = new ProfileRepositoryFactory();
        ProfileRepository r = f.getRepository();
        User user = r.retrieveCached();
        evaluateUserProfile(user);
    }

    private void evaluateUserProfile(User user) {
        Intent i;

        if (user != null && user.isProfileComplete()) {
            i = new Intent(getApplicationContext(), MainScreenActivity.class);
        } else {
            i = new Intent(getApplicationContext(), ProfileActivity.class);
        }

        startActivity(i);
    }

}
