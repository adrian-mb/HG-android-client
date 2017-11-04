package hg.hg_android_client.mainscreen.wait_for_driver;

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
import hg.hg_android_client.mainscreen.chat_client.ChatDialog;
import hg.hg_android_client.mainscreen.event.CancelTrip;
import hg.hg_android_client.mainscreen.event.GetInCar;

public class WaitForDriverFragment extends Fragment {

    private ChatDialog chat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View view = inflater.inflate(R.layout.fragment_wait_for_driver, container, false);
        initializeCancelTrip(view);
        initializeImInCar(view);
        initializeOpenChat(view);
        return view;
    }

    private void initializeOpenChat(View layout) {
        chat = new ChatDialog(getContext());
        Button b = (Button) layout.findViewById(R.id.button_chat);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chat.show();
            }
        });
    }

    private void initializeImInCar(View layout) {
        Button b = (Button) layout.findViewById(R.id.button_im_in);
        b.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener confirm = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        EventBus.getDefault().post(new GetInCar());
                    }
                };
                DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                };

                AlertDialog.Builder b = new AlertDialog.Builder(getContext());
                b.setTitle(getString(R.string.about_to_start_trip));
                b.setMessage(getString(R.string.about_to_start_trip_message));
                b.setPositiveButton(getString(R.string.OK), confirm);
                b.setNegativeButton(getString(R.string.button_cancel), cancel);
                b.show();
            }
        });
    }

    private void initializeCancelTrip(View layout) {
        Button b = (Button) layout.findViewById(R.id.button_cancel_trip);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener confirm = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        EventBus.getDefault().post(new CancelTrip());
                    }
                };
                DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                };

                AlertDialog.Builder b = new AlertDialog.Builder(getContext());
                b.setTitle(getString(R.string.about_to_cancel_trip));
                b.setMessage(getString(R.string.are_you_sure));
                b.setPositiveButton(getString(R.string.OK), confirm);
                b.setNegativeButton(getString(R.string.button_cancel), cancel);
                b.show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chat.isShowing()) {
            chat.dismiss();
            chat = null;
        }
    }

}
