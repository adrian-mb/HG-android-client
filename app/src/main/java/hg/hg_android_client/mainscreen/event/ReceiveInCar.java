package hg.hg_android_client.mainscreen.event;

public class ReceiveInCar {
    private long requestId;

    public ReceiveInCar() {
    }

    public ReceiveInCar(Long requestId) {
        this.requestId = requestId;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

}
