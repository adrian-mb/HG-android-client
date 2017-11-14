package hg.hg_android_client.login.repository;

import android.content.Context;

import org.springframework.http.HttpHeaders;

import hg.hg_android_client.util.LlevameEndpoint;

public class FacebookLoginEndpoint extends LlevameEndpoint {

    private final String KEY_ENDPOINT = "endpoint.facebooklogin";
    private final String ENDPOINT;

    private static final class Request {
        private String facebookAuthToken;

        public Request(String facebookAuthToken) {
            this.facebookAuthToken = facebookAuthToken;
        }

        public String getFacebookAuthToken() {
            return facebookAuthToken;
        }
    }

    private static final class ServerResponse {
        private String token;

        public void setToken(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }
    }

    public FacebookLoginEndpoint(Context context) {
        super(context);
        ENDPOINT = getEndpoint(KEY_ENDPOINT);
    }

    public LoginEndpoint.Response login(String facebookAuthToken) {
        // TODO: Move strings to strings.xml
        String request = toJson(new Request(facebookAuthToken));
        HttpHeaders headers = new HttpHeaderBuilder().forJson().build();
        try {
            ServerResponse r = post(ENDPOINT, request, headers, ServerResponse.class);
            return LoginEndpoint.Response.success(r.getToken());
        } catch (UnauthorizedAccessException e) {
            return LoginEndpoint.Response.error("Wrong Username or Password");
        } catch (UnexpectedException e) {
            return LoginEndpoint.Response.error("Unexpected Error");
        }
    }

}
