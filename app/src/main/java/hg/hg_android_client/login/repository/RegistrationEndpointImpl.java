package hg.hg_android_client.login.repository;

import android.content.Context;

import org.springframework.http.HttpHeaders;

import hg.hg_android_client.R;
import hg.hg_android_client.util.LlevameEndpoint;

public class RegistrationEndpointImpl extends LlevameEndpoint implements RegistrationEndpoint {

    private static final String KEY_ENDPOINT = "endpoint.registration";

    private final String ENDPOINT;
    private final String ERROR_BAD_REQUEST;
    private final String ERROR_UNEXPECTED;

    private static final class Request {
        private final String username;
        private final String password;
        private final String email;

        Request(String username, String password, String email) {
            this.username = username;
            this.password = password;
            this.email = email;
        }

        public String getUsername() {
            return username;
        }
        public String getPassword() {
            return password;
        }
        public String getEmail() {
            return email;
        }
    }

    RegistrationEndpointImpl(Context context) {
        super(context);
        ENDPOINT = getEndpoint(KEY_ENDPOINT);
        ERROR_BAD_REQUEST = context.getString(R.string.endpoint_registration_bad_request);
        ERROR_UNEXPECTED = context.getString(R.string.endpoint_unexpected_error);
    }

    @Override
    public Response request(String username, String password, String usermail) {
        String request = toJson(new Request(username, password, usermail));

        HttpHeaders headers = new HttpHeaderBuilder().forJson().build();

        try {
            post(ENDPOINT, request, headers, String.class);
        } catch(MalformedRequestException e) {
            return Response.error(ERROR_BAD_REQUEST);
        } catch(UnexpectedException e) {
            return Response.error(ERROR_UNEXPECTED);
        }

        return Response.success();
    }

}
