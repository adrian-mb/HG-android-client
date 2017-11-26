package hg.hg_android_client.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import hg.hg_android_client.R;
import hg.hg_android_client.login.repository.TokenRepository;
import hg.hg_android_client.login.repository.TokenRepositoryFactory;
import hg.hg_android_client.mainscreen.MainScreenActivity;
import hg.hg_android_client.model.Car;
import hg.hg_android_client.model.CreditCard;
import hg.hg_android_client.model.Profile;
import hg.hg_android_client.model.ProfileBuilder;
import hg.hg_android_client.profile.event.UpdateSuccess;
import hg.hg_android_client.profile.intent.UpdateProfileIntent;
import hg.hg_android_client.profile.repository.ProfileRepository;
import hg.hg_android_client.profile.repository.ProfileRepositoryFactory;
import hg.hg_android_client.util.CommonUtil;
import hg.hg_android_client.util.EditableFieldComponent;
import hg.hg_android_client.util.LlevameActivity;
import hg.hg_android_client.util.UiReader;

public class ProfileActivity extends LlevameActivity {

    public static final String KEY_CAR = "KEY_CAR";
    public static final String KEY_CARD = "KEY_CARD";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Profile user = retrieveCachedProfile();

        if (user == null) {
            logout();
        }

        initializeComponents(user);
        if (savedInstanceState == null) {
            initializeFragments(user);
        }
    }

    private Profile retrieveCachedProfile() {
        ProfileRepositoryFactory f = new ProfileRepositoryFactory();
        ProfileRepository r = f.getRepository(getApplicationContext());
        return r.retrieveCached();
    }

    private void initializeComponents(Profile profile) {
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
        if (profile != null) {
            values[0] = profile.getFirstName();
            values[1] = profile.getLastName();
            values[2] = profile.getBirthdate();
            values[3] = profile.getCountry();
        }

        for (int i = 0; i < id.length; i++) {
            EditableFieldComponent component = new EditableFieldComponent(parent, id[i]);
            component.setLabel(reader.readString(labels[i]));
            if (values[i] != null) {
                component.setText(values[i]);
            }
        }
    }

    private void initializeFragments(Profile profile) {
        Bundle arguments = new Bundle();
        Fragment fragment = null;

        if (profile == null || CommonUtil.empty(profile.getType())) {
            fragment = new NoneSelectedFragment();
        } else if (profile.isDriver()) {
            checkRadio(R.id.radio_driver);
            disableRadio(R.id.radio_passenger);
            arguments.putSerializable(KEY_CAR, profile.getFirstCar());
            fragment = new DriverProfileFragment();
        } else if (profile.isPassenger()) {
            checkRadio(R.id.radio_passenger);
            disableRadio(R.id.radio_driver);
            arguments.putSerializable(KEY_CARD, null);
            fragment = new PassengerProfileFragment();
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

    private void disableRadio(int id) {
        RadioButton r = (RadioButton) findViewById(id);
        r.setClickable(false);
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

    public void handleUpdateProfile() {
        // TODO: Implement scrolling for profile activity.
        hideKeyboard();

        Profile update = createProfile();

        if (update.isComplete()) {
            String title = getString(R.string.profile_updating);
            String message = getString(R.string.profile_updating_message);
            showDialog(title, message);
            Intent i = new UpdateProfileIntent(this, getToken(), update);
            this.startService(i);
        } else {
            displayIncompleteProfile();
        }
    }

    private String getToken() {
        TokenRepositoryFactory f = new TokenRepositoryFactory();
        TokenRepository r = f.getRepository(this.getApplicationContext());
        return r.getToken();
    }

    private Profile createProfile() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);

        View parent = findViewById(R.id.profile_parent);

        ProfileBuilder builder = new ProfileBuilder()
                .withFirstName(readComponent(parent, R.id.first_name))
                .withLastName(readComponent(parent, R.id.last_name))
                .withCountry(readComponent(parent, R.id.location))
                .withBirthdate(readComponent(parent, R.id.birthdate));

        RadioGroup g = (RadioGroup) findViewById(R.id.role_radios);
        int selectedId = g.getCheckedRadioButtonId();

        if (selectedId == R.id.radio_driver) {
            Car car = ((DriverProfileFragment) fragment).getCar();
            builder.withDriverCharacter().withAdditionalCar(car);
        } else if (selectedId == R.id.radio_passenger) {
            CreditCard creditCard = ((PassengerProfileFragment) fragment).getCreditCard();
            builder.withPassengerCharacter().withCreditCard(creditCard);
        }

        return builder.build();
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
                Intent j = new Intent(getApplicationContext(), MainScreenActivity.class);
                startActivity(j);
            }
        };

        displayConfirmationDialog(message, buttonMessage, handler);
    }

    private void displayIncompleteProfile() {
        dismissDialog();
        String message = getString(R.string.profile_incomplete);
        String buttonMessage = getString(R.string.OK);

        AlertDialog.OnClickListener handler = new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        };

        displayConfirmationDialog(message, buttonMessage, handler);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_save_profile_action:
                handleUpdateProfile();
                return true;
            case R.id.menu_logout_action:
                handleLogout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
