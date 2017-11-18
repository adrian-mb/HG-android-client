package hg.hg_android_client.mainscreen.event;

public class ReceivedCancelTrip {
    private long requestId;

    public ReceivedCancelTrip() {
    }

    public ReceivedCancelTrip(Long requestId) {
        this.requestId = requestId;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

}
