package hg.hg_android_client.mainscreen.event;

import hg.hg_android_client.mainscreen.driver_idle.Passenger;
import hg.hg_android_client.mainscreen.select_path.Location;
import hg.hg_android_client.mainscreen.select_path.Path;

public class TripRequest {
    private long requestId;
    private Passenger passenger;
    private Location destination;
    private Path suggestedPath;

    public TripRequest() {
    }

    public TripRequest(Passenger passenger, Location destination, Path suggestedPath) {
        this.passenger = passenger;
        this.destination = destination;
        this.suggestedPath = suggestedPath;
    }

    public long getRequestId() {
        return requestId;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public Location getDestination() {
        return destination;
    }

    public Path getSuggestedPath() {
        return suggestedPath;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }

    public void setSuggestedPath(Path path) {
        this.suggestedPath = path;
    }

}
