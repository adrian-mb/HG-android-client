package hg.hg_android_client.firebase;

import android.content.Context;

import org.springframework.http.HttpHeaders;

import hg.hg_android_client.util.LlevameEndpoint;

public class MessagingEndpoint extends LlevameEndpoint {

    private static final String ENDPOINT_KEY = "endpoint.notifications";
    private static final String ENDPOINT_UPDATE_KEY = "endpoint.notifications.token";

    private final String ENDPOINT;
    private final String ENDPOINT_UPDATE;

    public MessagingEndpoint(Context context) {
        super(context);
        ENDPOINT = getEndpoint(ENDPOINT_KEY);
        ENDPOINT_UPDATE = getEndpoint(ENDPOINT_UPDATE_KEY);
    }

    public void updateToken(String authToken, String registrationToken) {
        TokenUpdate up = new TokenUpdate(registrationToken);
        String request = toJson(up);

        HttpHeaders headers = new HttpHeaderBuilder()
                .withAuthToken(authToken)
                .forJson()
                .build();

        try {
            String response = post(ENDPOINT_UPDATE, request, headers, String.class);
        } catch (Exception e) {
            // TODO: Handle this.
        }
    }

    public void send(String token, Long targetId, String type, String payload) {
        Message m = new Message(targetId, type, payload);
        String request = toJson(m);

        HttpHeaders headers = new HttpHeaderBuilder()
                .withAuthToken(token)
                .forJson()
                .build();

        try {
            String response = post(ENDPOINT, request, headers, String.class);
        } catch (Exception e) {
            // TODO: See what to do here.
        }
    }

    private static class Message {
        private Long userId;
        private String type;
        private String payload;

        public Message(Long to, String type, String payload) {
            this.userId = to;
            this.type = type;
            this.payload = payload;
        }

        public Long getUserId() {
            return userId;
        }

        public String getType() {
            return type;
        }

        public String getPayload() {
            return payload;
        }

    }

    private static final class TokenUpdate {
        private String token;

        public TokenUpdate(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }
    }

}
