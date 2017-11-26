package hg.hg_android_client.model;

import java.io.Serializable;

public class PassengerSpecifics implements ProfileSpecifics, Serializable {

    private Profile parent;

    public PassengerSpecifics(Profile parent) {
        this.parent = parent;
    }

    @Override
    public boolean detailsComplete() {
        CreditCard creditCard = parent.getCard();
        return creditCard != null && creditCard.isComplete();
    }

}
