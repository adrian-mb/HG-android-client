package hg.hg_android_client.model.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.List;

import hg.hg_android_client.model.Car;
import hg.hg_android_client.model.Profile;
import hg.hg_android_client.model.ProfileBuilder;
import hg.hg_android_client.util.CommonUtil;

public class ProfileDeserializer extends StdDeserializer<Profile> {

    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_ID_ALT = "id";
    private static final String KEY_FIRST_NAME = "firstName";
    private static final String KEY_LAST_NAME = "lastName";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_BIRTHDATE = "birthdate";

    private static final String KEY_TYPE = "type";
    private static final String TYPE_DRIVER = "driver";
    private static final String TYPE_PASSENGER = "passenger";

    private static final String KEY_CARS = "cars";
    private static final String KEY_MODEL = "model";
    private static final String KEY_PATENT = "patent";
    private static final String KEY_CAR_ID = "id";

    public ProfileDeserializer() {
        this(null);
    }

    public ProfileDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Profile deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        JsonNode node = jp.getCodec().readTree(jp);

        Long userId = extractLong(node, KEY_USER_ID);
        if (userId == null) {
            userId = extractLong(node, KEY_USER_ID_ALT);
        }
        if (Integer.valueOf(0).equals(userId)) {
            userId = null;
        }

        ProfileBuilder builder = new ProfileBuilder()
                .withUserId(userId)
                .withFirstName(extract(node, KEY_FIRST_NAME))
                .withLastName(extract(node, KEY_LAST_NAME))
                .withCountry(extract(node, KEY_COUNTRY))
                .withBirthdate(extract(node, KEY_BIRTHDATE));

        JsonNode type = node.get(KEY_TYPE);

        if (type != null) {
            String value = type.textValue();
            if (TYPE_DRIVER.equalsIgnoreCase(value)) {
                buildDriver(builder, node);
            } else if (TYPE_PASSENGER.equalsIgnoreCase(value)) {
                buildPassenger(builder, node);
            }
        }

        return builder.build();
    }

    private void buildDriver(ProfileBuilder builder, JsonNode node) {
        ProfileBuilder.DriverProfileBuilder b = builder.withDriverCharacter();

        ArrayNode nodecars = (ArrayNode) node.get(KEY_CARS);
        if (nodecars != null) {
            for (JsonNode carnode : nodecars) {
                Car car = new Car(
                        extractInteger(carnode, KEY_CAR_ID),
                        extract(carnode, KEY_PATENT),
                        extract(carnode, KEY_MODEL));
                b.withAdditionalCar(car);
            }
        }
    }

    private void buildPassenger(ProfileBuilder builder, JsonNode node) {
        builder.withPassengerCharacter();
    }

    private Integer extractInteger(JsonNode node, String key) {
        JsonNode n = node.get(key);
        return n == null ? null : n.intValue();
    }

    private Long extractLong(JsonNode node, String key) {
        JsonNode n = node.get(key);
        return n == null ? null : n.longValue();
    }

    private String extract(JsonNode node, String key) {
        JsonNode n = node.get(key);
        return n == null ? null : n.textValue();
    }

}
