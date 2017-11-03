package hg.hg_android_client.mainscreen.event;

public class ConfirmTrip {

    private long requestId;

    public ConfirmTrip(long requestId) {
        this.requestId = requestId;
    }

    public long getRequestId() {
        return requestId;
    }

}
