package hg.hg_android_client.model;

import java.io.Serializable;
import java.util.List;

public class DriverSpecifics implements ProfileSpecifics, Serializable {

    private Profile parent;

    public DriverSpecifics(Profile parent) {
        this.parent = parent;
    }

    @Override
    public boolean detailsComplete() {
        List<Car> cars = parent.getCars();
        return cars != null && cars.size() > 0 && cars.get(0).detailsComplete();
    }

}
