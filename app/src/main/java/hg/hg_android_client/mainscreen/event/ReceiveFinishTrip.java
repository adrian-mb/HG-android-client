package hg.hg_android_client.mainscreen.event;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ReceiveFinishTrip {
    private Cost cost;
    private Address start;
    private Address end;
    private double distance;

    public Cost getCost() {
        return cost;
    }

    @JsonIgnore
    public String getCostString() {
        return cost.toString();
    }

    public Address getStart() {
        return start;
    }

    public Address getEnd() {
        return end;
    }

    public double getDistance() {
        return distance;
    }

    public void setCost(Cost cost) {
        this.cost = cost;
    }

    public void setStart(Address start) {
        this.start = start;
    }

    public void setEnd(Address end) {
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

    private static class Address {
        private String street;
        private LocationDescriptor location;

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public LocationDescriptor getLocation() {
            return location;
        }

        public void setLocation(LocationDescriptor location) {
            this.location = location;
        }

    }

    private static class LocationDescriptor {
        private double lat;
        private double lon;

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }

    }

}
