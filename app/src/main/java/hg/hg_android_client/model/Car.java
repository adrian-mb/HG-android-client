package hg.hg_android_client.model;

import java.io.Serializable;

import hg.hg_android_client.util.CommonUtil;

public class Car implements Serializable {
    private final Integer id;
    private final String patent;
    private final String model;

    public static Car empty() {
        return new Car("", "");
    }

    public Car(Integer id, String patent, String model) {
        this.id = id;
        this.patent = patent;
        this.model = model;
    }

    public Car(String patent, String model) {
        this.id = null;
        this.patent = patent;
        this.model = model;
    }

    public Integer getId() {
        return id;
    }

    public String getPatent() {
        return patent;
    }

    public String getModel() {
        return model;
    }

    public boolean detailsComplete() {
        return !CommonUtil.empty(patent) && !CommonUtil.empty(model);
    }

}
