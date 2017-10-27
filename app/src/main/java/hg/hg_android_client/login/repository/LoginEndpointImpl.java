package hg.hg_android_client.login.repository;

import android.content.Context;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import hg.hg_android_client.R;
import hg.hg_android_client.util.LlevameEndpoint;

public class LoginEndpointImpl extends LlevameEndpoint implements LoginEndpoint {

    private static final String KEY_ENDPOINT = "endpoint.login";

    private final String ENDPOINT;
    private final String ERROR_BAD_AUTH;
    private final String ERROR_UNEXPECTED;

    private static class Request {
        private String username;
        private String password;

        Request(String username, String password) {
            this.username = username;
            this.password = password;
        }

        String getUsername() {
            return username;
        }

        String getPassword() {
            return password;
        }
    }

    private static class ServerResponse {
        private String token;
        public void setToken(String token) {
            this.token = token;
        }
        public String getToken() {
            return token;
        }
    }

    LoginEndpointImpl(Context context) {
        super(context);
        ERROR_BAD_AUTH = context.getString(R.string.endpoint_login_bad_auth);
        ERROR_UNEXPECTED = context.getString(R.string.endpoint_unexpected_error);
        ENDPOINT = getEndpoint(KEY_ENDPOINT);
    }

    public Response login(String username, String password) {
        String request = toJson(new Request(username, password));
        HttpHeaders headers = new HttpHeaderBuilder().forJson().build();
        try {
            ServerResponse response = post(ENDPOINT, request, headers, ServerResponse.class);
            return Response.success(response.getToken());
        } catch (UnauthorizedAccessException e) {
            return Response.error(ERROR_BAD_AUTH);
        } catch (UnexpectedException e) {
            return Response.error(ERROR_UNEXPECTED);
        }
    }


}
