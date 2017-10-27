package hg.hg_android_client.model;

import java.io.Serializable;

public class PassengerSpecifics implements ProfileSpecifics, Serializable {

    Profile parent;

    public PassengerSpecifics(Profile parent) {
        this.parent = parent;
    }

    @Override
    public boolean detailsComplete() {
        return true;
    }

}
