package hg.hg_android_client.util;

import android.content.Context;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LlevameEndpoint {

    private static final String LOGGER_TAG = "LlevameEndpoint";

    private static final String CONFIGURATION_FILE = "endpoint.properties";
    private static final String KEY_BASE = "endpoint.base";

    private Properties configuration;

    protected LlevameEndpoint(Context context) {
        this.configuration = loadProperties(context);
    }

    protected String getEndpoint(String key) {
        String base = configuration.getProperty(KEY_BASE);
        return base + configuration.getProperty(key);
    }

    private Properties loadProperties(Context context) {
        Properties properties = new Properties();
        try {
            InputStream istream = context.getAssets().open(CONFIGURATION_FILE);
            properties.load(istream);
        } catch (IOException e) {
        }
        return properties;
    }

    protected <T> String toJson(T object) {
        JsonTransform t = new JsonTransform();
        return t.toJson(object);
    }

    protected <T> T fromJson(String json, Class<T> objectType) {
        JsonTransform t = new JsonTransform();
        return t.fromJson(json, objectType);
    }

    protected <T> T delete(
            String endpoint,
            HttpHeaders headers,
            Class<T> responseType) {
        return send(endpoint, "", headers, responseType, HttpMethod.DELETE);
    }

    protected <T> T get(
            String endpoint,
            String params,
            HttpHeaders headers,
            Class<T> responseType) {
        return send(endpoint + "?" + params, "", headers, responseType, HttpMethod.GET);
    }

    protected <T> T post(
            String endpoint,
            String requestBody,
            HttpHeaders headers,
            Class<T> responseType) {
        return send(endpoint, requestBody, headers, responseType, HttpMethod.POST);
    }

    protected <T> T send(
            String endpoint,
            String requestBody,
            HttpHeaders headers,
            Class<T> responseType,
            HttpMethod method) {

        RestTemplate template = new RestTemplate();
        template.setErrorHandler(new ErrorHandler());
        template.getMessageConverters().add(new StringHttpMessageConverter());
        template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        Logger.getLogger(LOGGER_TAG).log(Level.INFO, new StringBuilder()
                .append(method.toString())
                .append(" ")
                .append(endpoint)
                .append(" ")
                .append(requestBody)
                .toString());

        ResponseEntity<T> response = template.exchange(
                endpoint, method, request, responseType);

        try {
            return response.getBody();
        } catch(Exception e) {
            throw new UnexpectedException();
        }
    }

    protected static class UnauthorizedAccessException extends RuntimeException {
    }

    protected static class MalformedRequestException extends RuntimeException {
    }

    protected static class UnexpectedException extends RuntimeException {
    }

    private class ErrorHandler implements ResponseErrorHandler {
        private ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();

        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            return errorHandler.hasError(response);
        }

        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            Logger.getLogger(LOGGER_TAG).log(Level.SEVERE, response.getStatusText());

            if (HttpStatus.UNAUTHORIZED.equals(response.getStatusCode())) {
                throw new UnauthorizedAccessException();
            }
            if (HttpStatus.BAD_REQUEST.equals(response.getStatusCode())) {
                throw new MalformedRequestException();
            }
            throw new UnexpectedException();
        }
    }

    public static class HttpHeaderBuilder {

        private static final String AUTH_HEADER = "Authorization";

        HttpHeaders headers;

        public HttpHeaderBuilder() {
            headers = new HttpHeaders();
        }

        public HttpHeaderBuilder forJson() {
            headers.setContentType(MediaType.APPLICATION_JSON);
            return this;
        }

        public HttpHeaderBuilder withAuthToken(String token) {
            headers.add(AUTH_HEADER, bearerToken(token));
            return this;
        }

        public HttpHeaders build() {
            return headers;
        }

        private String bearerToken(String token) {
            return "BEARER " + token;
        }

    }

}
