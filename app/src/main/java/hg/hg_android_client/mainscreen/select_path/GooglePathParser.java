package hg.hg_android_client.mainscreen.select_path;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GooglePathParser {

    List<Path> parse(String response) throws IOException {
        List<Path> results = new ArrayList<>();

        JsonNode root = parseTree(response);
        ArrayNode routes = extractRoutes(root);

        for (JsonNode route : routes) {
            Path path = processRoute(route);
            results.add(path);
        }

        return results;
    }

    private JsonNode parseTree(String response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(response);
    }

    private ArrayNode extractRoutes(JsonNode root) {
        return (ArrayNode) root.get("routes");
    }

    private Path processRoute(JsonNode route) {
        JsonNode leg = extractFirstLeg(route);
        Path path = new Path();
        path.add(extractLocation(leg, "start_location"));

        ArrayNode steps = (ArrayNode) leg.get("steps");
        for (JsonNode step : steps) {
            LatLng target = extractLocation(step, "end_location");
            path.add(target);
        }

        return path;
    }

    private JsonNode extractFirstLeg(JsonNode route) {
        return route.get("legs").get(0);
    }

    private LatLng extractLocation(JsonNode node, String tag) {
        JsonNode location = node.get(tag);
        double lat = location.get("lat").asDouble();
        double lng = location.get("lng").asDouble();
        return new LatLng(lat, lng);
    }

}
