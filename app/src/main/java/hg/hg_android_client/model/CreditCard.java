package hg.hg_android_client.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public class CreditCard implements Serializable {
    private String number;
    private String cvc;
    private String expirationDate;

    public static CreditCard empty() {
        return new CreditCard("", "", "");
    }

    public CreditCard(String number, String cvc, String expirationDate) {
        this.number = number;
        this.cvc = cvc;
        this.expirationDate = expirationDate;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }

    public String getCvc() {
        return cvc;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    @JsonIgnore
    public boolean isComplete() {
        return hasValue(number) && hasValue(cvc) && hasValue(expirationDate);
    }

    private boolean hasValue(String field) {
        return field != null && !"".equals(field.trim());
    }

}
