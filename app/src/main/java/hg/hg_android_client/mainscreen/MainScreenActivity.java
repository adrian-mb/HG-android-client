package hg.hg_android_client.mainscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import hg.hg_android_client.R;
import hg.hg_android_client.login.LoginActivity;
import hg.hg_android_client.login.event.LogoutSuccess;
import hg.hg_android_client.login.intent.LogoutIntent;
import hg.hg_android_client.util.LlevameActivity;

// TODO: Fix placeholder logout button.
public class MainScreenActivity extends LlevameActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void logoutOnClick(View view) {
        hideKeyboard();
        String title = "Logging Out";
        String message = "Please wait...";
        showDialog(title, message);
        Intent i = new LogoutIntent(getApplicationContext());
        startService(i);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogoutSuccess(LogoutSuccess event) {
        dismissDialog();
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
    }

}
