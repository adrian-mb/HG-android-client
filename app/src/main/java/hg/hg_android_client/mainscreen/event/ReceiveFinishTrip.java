package hg.hg_android_client.mainscreen.event;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ReceiveFinishTrip {
    private Cost cost;
    private String start;
    private String end;
    private double distance;

    public Cost getCost() {
        return cost;
    }

    @JsonIgnore
    public String getCostString() {
        return cost.toString();
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public double getDistance() {
        return distance;
    }

    public void setCost(Cost cost) {
        this.cost = cost;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    private static class Cost {
        private String currency;
        private double value;

        public double getValue() {
            return value;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public void setValue(double value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value) + " " + currency;
        }

    }

}
