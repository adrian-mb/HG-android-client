package hg.hg_android_client.mainscreen.select_destination;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;

import hg.hg_android_client.R;
import hg.hg_android_client.mainscreen.event.SendSelectMessage;
import hg.hg_android_client.mainscreen.event.UpdateDestination;

public class SelectDestinationFragment extends Fragment implements
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
        EventBus.getDefault().post(new UpdateDestination(position));
    }
}
