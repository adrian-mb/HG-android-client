package hg.hg_android_client.profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.io.Serializable;

import hg.hg_android_client.R;
import hg.hg_android_client.model.CreditCard;
import hg.hg_android_client.util.UiReader;

public class PassengerProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View view = inflater.inflate(R.layout.fragment_passenger_profile, container, false);
        initializeComponents(view, retrieveCreditCardArgument());
        return view;
    }

    private void initializeComponents(View parent, CreditCard c) {
        View cardnumberContainer = parent.findViewById(R.id.card_number);
        View securitycodeContainer = parent.findViewById(R.id.security_code);
        View expirationContainer = parent.findViewById(R.id.expiration_date);

        updateTextView(cardnumberContainer, R.id.label, R.string.profile_card_number);
        updateTextView(securitycodeContainer, R.id.label, R.string.profile_security_code);
        updateTextView(expirationContainer, R.id.label, R.string.profile_expiration_date);

        updateEditText(cardnumberContainer, R.id.edit_field, c.getNumber());
        updateEditText(securitycodeContainer, R.id.edit_field, c.getCvc());
        updateEditText(expirationContainer, R.id.edit_field, c.getExpirationDate());
    }

    private CreditCard retrieveCreditCardArgument() {
        if (getArguments() != null) {
            Serializable s = getArguments().getSerializable(ProfileActivity.KEY_CARD);
            if (s != null) {
                return (CreditCard) s;
            }
        }

        return CreditCard.empty();
    }

    private void updateTextView(View container, int id, int stringId) {
        TextView textview = (TextView) container.findViewById(id);
        textview.setText(getResources().getString(stringId));
    }

    private void updateEditText(View container, int id, String value) {
        EditText e = (EditText) container.findViewById(id);
        e.setText(value);
    }

    public String getCardNumber() {
        return getComponentField(R.id.card_number);
    }

    public String getCardExpiration() {
        return getComponentField(R.id.expiration_date);
    }

    public String getCardSecurityCode() {
        return getComponentField(R.id.security_code);
    }

    public String getComponentField(int id) {
        View container = getView().findViewById(id);
        UiReader reader = new UiReader(container);
        return reader.readEditText(R.id.edit_field);
    }

    public CreditCard getCreditCard() {
        return new CreditCard(
                getCardNumber(),
                getCardSecurityCode(),
                getCardExpiration());
    }

}
