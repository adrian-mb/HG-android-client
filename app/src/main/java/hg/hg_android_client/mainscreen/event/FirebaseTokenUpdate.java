package hg.hg_android_client.mainscreen.event;

public class FirebaseTokenUpdate {

    private String token;

    public FirebaseTokenUpdate(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

}
