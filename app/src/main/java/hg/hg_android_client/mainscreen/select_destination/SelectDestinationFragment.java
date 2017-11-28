package hg.hg_android_client.mainscreen.select_destination;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import hg.hg_android_client.R;
import hg.hg_android_client.login.repository.TokenRepository;
import hg.hg_android_client.login.repository.TokenRepositoryFactory;
import hg.hg_android_client.mainscreen.event.SendSelectMessage;
import hg.hg_android_client.mainscreen.event.UpdateDestination;
import hg.hg_android_client.mainscreen.repository.StateRepository;
import hg.hg_android_client.mainscreen.repository.StateRepositoryFactory;
import hg.hg_android_client.mainscreen.select_path.Location;
import hg.hg_android_client.util.LlevameFragment;

public class SelectDestinationFragment extends LlevameFragment implements
        GoogleMap.OnMapClickListener, View.OnClickListener {

    private LatLng destination;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        int layout = R.layout.fragment_select_destination;
        View view = inflater.inflate(layout, container, false);
        initializeConfirmButton(view);
        return view;
    }

    private void initializeConfirmButton(View layout) {
        Button confirm = (Button) layout.findViewById(R.id.confirm_destination);
        confirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (destination != null) {
            EventBus.getDefault().post(new SendSelectMessage());
        } else {
            CharSequence text = getString(R.string.select_destination_first);
            Toast.makeText(this.getContext(), text, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapClick(LatLng position) {
        destination = position;
        estimateCost(destination);
        EventBus.getDefault().post(new UpdateDestination(position));
    }

    private void estimateCost(final LatLng destination) {
        showDialog("Estimating Cost", "Please wait...");
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                EstimateEndpoint e = new EstimateEndpoint(getContext());
                String cost = e.estimate(getToken(), getPosition(), new Location(destination));
                EventBus.getDefault().post(new CostEstimate(cost));
                return null;
            }
        }.execute();
    }

    private void updateEstimate(String cost) {
        TextView estimate = (TextView) getView().findViewById(R.id.text_estimate);
        estimate.setText("Estimated cost: " + cost + "; tap confirm to continue");
    }

    private String getToken() {
        TokenRepositoryFactory f = new TokenRepositoryFactory();
        TokenRepository r = f.getRepository(getContext());
        return r.getToken();
    }

    private Location getPosition() {
        StateRepositoryFactory f = new StateRepositoryFactory();
        StateRepository r = f.get(getContext());
        return new Location(r.getOrigin());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateEstimate(CostEstimate estimate) {
        dismissDialog();
        updateEstimate(estimate.getEstimate());
    }

    private static final class CostEstimate {
        private String estimate;

        public CostEstimate(String estimate) {
            this.estimate = estimate;
        }

        public String getEstimate() {
            return estimate;
        }

    }

}
