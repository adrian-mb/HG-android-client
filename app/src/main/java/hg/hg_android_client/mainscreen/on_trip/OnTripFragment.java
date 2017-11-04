package hg.hg_android_client.mainscreen.on_trip;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.greenrobot.eventbus.EventBus;

import hg.hg_android_client.R;
import hg.hg_android_client.mainscreen.event.FinishTrip;

public class OnTripFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View view = inflater.inflate(R.layout.fragment_on_trip, container, false);
        initializeFinishTrip(view);
        return view;
    }

    private void initializeFinishTrip(View layout) {
        Button b = (Button) layout.findViewById(R.id.button_finish_trip);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener confirm = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        EventBus.getDefault().post(new FinishTrip());
                    }
                };
                DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                };

                AlertDialog.Builder b = new AlertDialog.Builder(getContext());
                b.setTitle(getString(R.string.finish_trip));
                b.setMessage(getString(R.string.are_you_sure));
                b.setPositiveButton(getString(R.string.OK), confirm);
                b.setNegativeButton(getString(R.string.button_cancel), cancel);
                b.show();
            }
        });
    }

}
