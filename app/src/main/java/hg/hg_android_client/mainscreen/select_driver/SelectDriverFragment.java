package hg.hg_android_client.mainscreen.select_driver;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import hg.hg_android_client.R;
import hg.hg_android_client.mainscreen.event.CancelTripSetup;
import hg.hg_android_client.mainscreen.event.DriversAroundResponse;
import hg.hg_android_client.mainscreen.event.SelectDriver;
import hg.hg_android_client.mainscreen.repository.StateRepository;
import hg.hg_android_client.mainscreen.repository.StateRepositoryFactory;
import hg.hg_android_client.mainscreen.select_path.Location;
import hg.hg_android_client.model.Car;
import hg.hg_android_client.model.Profile;
import hg.hg_android_client.util.ListAdapter;
import hg.hg_android_client.util.LlevameFragment;

public class SelectDriverFragment extends LlevameFragment {

    private ListView drivers;
    private Driver selected;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View layout = inflater.inflate(R.layout.fragment_select_driver, container, false);
        initializeCancelButton(layout);
        initializeConfirmButton(layout);
        initializeRefreshButton(layout);
        initializeDriverList(layout);
        return layout;
    }

    private void initializeCancelButton(View layout) {
        Button button = (Button) layout.findViewById(R.id.button_cancel);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new CancelTripSetup());
            }
        });
    }

    private void initializeConfirmButton(View layout) {
        Button button = (Button) layout.findViewById(R.id.button_confirm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (false) {
                    // TODO: Test whether driver is selected
                    // TODO: Post confirmation
                } else {
                    String message = getString(R.string.please_select_driver);
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initializeRefreshButton(View layout) {
        Button button = (Button) layout.findViewById(R.id.button_refresh);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshDrivers();
            }
        });
    }

    private void initializeDriverList(View layout) {
        drivers = (ListView) layout.findViewById(R.id.driver_list);
        refreshDrivers();
    }

    private void refreshDrivers() {
        String title = getString(R.string.finding_drivers_title);
        String message = getString(R.string.please_wait);
        showDialog(title, message);

        QueryDriversIntent intent = new QueryDriversIntent(getContext(), loadLocation());
        getContext().startService(intent);
    }

    private Location loadLocation() {
        StateRepositoryFactory f = new StateRepositoryFactory();
        StateRepository r = f.get(getContext());
        return new Location(r.getOrigin());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDriversAroundResponse(DriversAroundResponse event) {
        dismissDialog();
        DriverListAdapter adapter = new DriverListAdapter(getContext(), event.getDrivers());
        drivers.setAdapter(adapter);
        drivers.setOnItemClickListener(new OnDriverClickListener());
    }

    private static final class DriverListAdapter extends ListAdapter<Driver> {

        public DriverListAdapter(@NonNull Context context, List<Driver> elements) {
            super(context, elements, R.layout.drivers_list_item);
        }

        @Override
        protected void handleItem(View view, Driver current, int position) {
            fillDriverName(view, current);
            fillCarDetails(view, current);
        }

        private void fillDriverName(View layout, Driver driver) {
            TextView driverName = (TextView) layout.findViewById(R.id.driver_full_name);
            driverName.setText(takeFullName(driver));
        }

        private void fillCarDetails(View layout, Driver driver) {
            TextView carDetails = (TextView) layout.findViewById(R.id.driver_car);
            carDetails.setText(takeCarDetails(driver));
        }

        private String takeFullName(Driver driver) {
            Profile p = driver.getProfile();
            return p.getFullName();
        }

        private String takeCarDetails(Driver driver) {
            Car p = driver.getProfile().getFirstCar();
            return p.getModel() + " " + p.getPatent();
        }

    }

    private final class OnDriverClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            selected = (Driver) adapterView.getItemAtPosition(position);
            SelectDriver event = new SelectDriver(selected);
            EventBus.getDefault().post(event);
        }
    }

}
