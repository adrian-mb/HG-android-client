package hg.hg_android_client.mainscreen.event;

public class DeclineTripRequest {

    private long requestId;

    public DeclineTripRequest(long requestId) {
        this.requestId = requestId;
    }

    public long getRequestId() {
        return requestId;
    }

}
