package hg.hg_android_client.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import hg.hg_android_client.R;
import hg.hg_android_client.login.LoginActivity;
import hg.hg_android_client.login.event.LogoutSuccess;
import hg.hg_android_client.login.intent.LogoutIntent;
import hg.hg_android_client.mainscreen.services.TripService;

public class LlevameActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        dismissDialog();
    }

    protected void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected void showDialog(String title, String message) {
        dismissDialog();
        progressDialog = ProgressDialog.show(this, title, message);
    }

    protected void showDialog(
            String title,
            String message,
            DialogInterface.OnClickListener canceller) {
        dismissDialog();
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setButton(-1, getString(R.string.button_cancel), canceller);
        progressDialog.show();
    }

    protected boolean isDialogShowing() {
        return progressDialog != null && progressDialog.isShowing();
    }

    protected void dismissDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    protected void displayConfirmationDialog(
            String message,
            String buttonMessage,
            AlertDialog.OnClickListener handler) {
        displayConfirmationDialog(null, message, buttonMessage, handler);
    }

    protected void displayConfirmationDialog(
            String title,
            String message,
            String buttonMessage,
            AlertDialog.OnClickListener handler) {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, buttonMessage, handler);
        dialog.show();
    }

    protected void logout() {
        Intent logout = new LogoutIntent(getApplicationContext());
        startService(logout);
    }

    protected void stopTripService() {
        Intent i = new Intent(getApplicationContext(), TripService.class);
        i.setAction(TripService.ACTION_STOP);
        startService(i);
    }

    protected void handleLogout() {
        hideKeyboard();
        String title = getString(R.string.logging_out_title);
        String message = getString(R.string.logging_out_mesage);
        showDialog(title, message);
        Intent i = new LogoutIntent(getApplicationContext());
        startService(i);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    protected void onLogoutSuccess(LogoutSuccess event) {
        dismissDialog();
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissDialog();
    }

}
