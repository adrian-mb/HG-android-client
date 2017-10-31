package hg.hg_android_client.util;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;

import org.greenrobot.eventbus.EventBus;

public class LlevameFragment extends Fragment {

    private ProgressDialog progressDialog;

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    protected void showDialog(String title, String message) {
        dismissDialog();
        progressDialog = ProgressDialog.show(getContext(), title, message);
    }

    protected void dismissDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissDialog();
    }

}
