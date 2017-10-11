package hg.hg_android_client.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import hg.hg_android_client.R;
import hg.hg_android_client.login.LoginActivity;
import hg.hg_android_client.login.event.LogoutSuccess;
import hg.hg_android_client.login.intent.LogoutIntent;
import hg.hg_android_client.login.repository.TokenRepository;
import hg.hg_android_client.login.repository.TokenRepositoryFactory;
import hg.hg_android_client.model.Car;
import hg.hg_android_client.model.CreditCard;
import hg.hg_android_client.model.Driver;
import hg.hg_android_client.model.Passenger;
import hg.hg_android_client.model.User;
import hg.hg_android_client.profile.event.UpdateSuccess;
import hg.hg_android_client.profile.intent.UpdateProfileIntent;
import hg.hg_android_client.profile.repository.ProfileRepository;
import hg.hg_android_client.profile.repository.ProfileRepositoryFactory;
import hg.hg_android_client.util.EditableFieldComponent;
import hg.hg_android_client.util.LlevameActivity;
import hg.hg_android_client.util.UiReader;

public class ProfileActivity extends LlevameActivity {

    public static final String KEY_CAR = "KEY_CAR";
    public static final String KEY_CARD = "KEY_CARD";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        User user = retrieveCachedUser();

        if (user == null) {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
        }

        initializeComponents(user);
        if (savedInstanceState == null) {
            initializeFragments(user);
        }
    }

    private User retrieveCachedUser() {
        ProfileRepositoryFactory f = new ProfileRepositoryFactory();
        ProfileRepository r = f.getRepository();
        return r.retrieveCached();
    }

    private void initializeComponents(User user) {
        // TODO: Mark required fields that are missing.

        UiReader reader = new UiReader(this);
        View parent = findViewById(R.id.profile_parent);

        int[] id = {
                R.id.first_name,
                R.id.last_name,
                R.id.birthdate,
                R.id.location
        };

        int[] labels = {
                R.string.profile_name,
                R.string.profile_last_name,
                R.string.profile_birthdate,
                R.string.profile_location
        };

        String[] values = new String[4];
        if (user != null) {
            values[0] = user.getFirstName();
            values[1] = user.getLastName();
            values[2] = user.getBirthdate();
            values[3] = user.getLocation();
        }

        for (int i = 0; i < id.length; i++) {
            EditableFieldComponent component = new EditableFieldComponent(parent, id[i]);
            component.setLabel(reader.readString(labels[i]));
            if (values[i] != null) {
                component.setText(values[i]);
            }
        }
    }

    private void initializeFragments(User user) {
        Bundle arguments = new Bundle();
        Fragment fragment;

        if (User.Role.DRIVER.equals(user.getRole())) {
            checkRadio(R.id.radio_driver);
            arguments.putSerializable(KEY_CAR, ((Driver)user).getCar());
            fragment = new DriverProfileFragment();
        } else if (User.Role.PASSENGER.equals(user.getRole())) {
            checkRadio(R.id.radio_passenger);
            arguments.putSerializable(KEY_CARD, ((Passenger)user).getCreditCard());
            fragment = new PassengerProfileFragment();
        } else {
            fragment = new NoneSelectedFragment();
        }

        fragment.setArguments(arguments);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit();
    }

    private void checkRadio(int id) {
        RadioButton r = (RadioButton) findViewById(id);
        r.setChecked(true);
    }

    public void onDriverSelected(View view) {
        updateFragmentContainer(new DriverProfileFragment());
    }

    public void onPassengerSelected(View view) {
        updateFragmentContainer(new PassengerProfileFragment());
    }

    private void updateFragmentContainer(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    public void updateProfileOnClick(View view) {
        // TODO: Implement scrolling for profile activity.
        hideKeyboard();
        String title = getResources().getString(R.string.profile_updating);
        String message = getResources().getString(R.string.profile_updating_message);
        showDialog(title, message);
        Intent i = new UpdateProfileIntent(this, getToken(), createUser());
        this.startService(i);
    }

    private String getToken() {
        TokenRepositoryFactory f = new TokenRepositoryFactory();
        TokenRepository r = f.getRepository(this.getApplicationContext());
        return r.getToken();
    }

    private User createUser() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);

        View parent = findViewById(R.id.profile_parent);

        String firstname = readComponent(parent, R.id.first_name);
        String lastname = readComponent(parent, R.id.last_name);
        String location = readComponent(parent, R.id.location);
        String birthdate = readComponent(parent, R.id.birthdate);

        RadioGroup g = (RadioGroup) findViewById(R.id.role_radios);
        int selectedId = g.getCheckedRadioButtonId();

        if (selectedId == R.id.radio_driver) {
            Car car = ((DriverProfileFragment) fragment).getCar();
            return new Driver(firstname, lastname, location, birthdate, car);
        } else if (selectedId == R.id.radio_passenger) {
            CreditCard card = ((PassengerProfileFragment) fragment).getCreditCard();
            return new Passenger(firstname, lastname, location, birthdate, card);
        } else {
            // TODO: Validate whether all the data is complete; we should not hit this else.
            return null;
        }
    }

    private String readComponent(View parent, int id) {
        return new EditableFieldComponent(parent, id).getText();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateSuccess(UpdateSuccess event) {
        dismissDialog();
        String message = getString(R.string.profile_update_success);
        String buttonMessage = getString(R.string.OK);

        AlertDialog.OnClickListener handler = new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        };

        displayConfirmationDialog(message, buttonMessage, handler);
    }


    // TODO: Fix placeholder log out button.
    public void logoutOnClick(View view) {
        // TODO: Handle logout better (create toolbar, make code common).
        // TODO: Move strings to xml.
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
