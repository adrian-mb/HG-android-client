package hg.hg_android_client.mainscreen.driver_confirm;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.EventLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import org.greenrobot.eventbus.EventBus;

import hg.hg_android_client.R;
import hg.hg_android_client.mainscreen.event.ConfirmTrip;
import hg.hg_android_client.mainscreen.event.DeclineTripRequest;
import hg.hg_android_client.mainscreen.event.FocusOnDestination;
import hg.hg_android_client.mainscreen.event.FocusOnPassenger;

public class DriverConfirmFragment extends Fragment {

    public static final String KEY_REQUEST_ID = "KEY_REQUEST_ID";

    private static final long TIMEOUT = 50000;
    private static final long DELTA = 500;

    private CountDownTimer timer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View view = inflater.inflate(R.layout.fragment_driver_confirm, container, false);
        initializeCounter(view);
        initializeFindPassenger(view);
        initializeFindDestination(view);
        initializeAccept(view);
        initializeDecline(view);
        return view;
    }

    private void initializeCounter(View layout) {
        final ProgressBar progressBar = (ProgressBar) layout.findViewById(R.id.timeout);
        timer = new CountDownTimer(TIMEOUT, DELTA) {
            @Override
            public void onTick(long left) {
                progressBar.setProgress((int) (100*left/TIMEOUT));
            }
            @Override
            public void onFinish() {
                decline();
            }
        }.start();
    }

    private void initializeFindPassenger(View layout) {
        Button b = (Button) layout.findViewById(R.id.button_find_passenger);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new FocusOnPassenger());
            }
        });
        b.callOnClick();
    }

    private void initializeFindDestination(View layout) {
        Button b = (Button) layout.findViewById(R.id.button_find_destination);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new FocusOnDestination());
            }
        });
    }

    private void initializeAccept(View view) {
        Button b = (Button) view.findViewById(R.id.button_accept);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.cancel();
                long requestId = getArguments().getLong(KEY_REQUEST_ID);
                EventBus.getDefault().post(new ConfirmTrip(requestId));
            }
        });
    }

    private void initializeDecline(View view) {
        Button b = (Button) view.findViewById(R.id.button_decline);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decline();
            }
        });
    }

    private void decline() {
        long requestId = getArguments().getLong(KEY_REQUEST_ID);
        EventBus.getDefault().post(new DeclineTripRequest(requestId));
    }

}
