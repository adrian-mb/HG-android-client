package hg.hg_android_client.mainscreen.select_path;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hg.hg_android_client.R;
import hg.hg_android_client.util.LlevameEndpoint;

public class GooglePathRepository extends LlevameEndpoint implements PathRepository {

    private static final String BASE =
            "https://maps.googleapis.com/maps/api/directions/json";

    private String apiKey;

    protected GooglePathRepository(Context context) {
        super(context);
        apiKey = context.getString(R.string.google_dirs_key);
    }

    public List<Path> query(String token, LatLng origin, LatLng destination) {
        HttpHeaders headers = new HttpHeaderBuilder().forJson().build();
        String params = getParams(apiKey, origin, destination);

        try {
            String response = get(BASE, params, headers, String.class);
            return handleResponse(response);
        } catch(Exception e) {
            // TODO: Do something;
            return null;
        }
    }

    private String getParams(String key, LatLng origin, LatLng destination) {
        StringBuilder b = new StringBuilder();

        b.append("origin=").append(toParams(origin)).append("&");
        b.append("destination=").append(toParams(destination)).append("&");
        b.append("alternatives=true&");
        b.append("key=").append(key);

        return b.toString();
    }

    private String toParams(LatLng point) {
        StringBuilder b = new StringBuilder();
        b.append(point.latitude);
        b.append(",");
        b.append(point.longitude);
        return b.toString();
    }

    private List<Path> handleResponse(String response) {
        try {
            GooglePathParser parser = new GooglePathParser();
            return parser.parse(response);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

}
