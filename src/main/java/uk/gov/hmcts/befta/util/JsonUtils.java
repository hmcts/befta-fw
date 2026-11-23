package uk.gov.hmcts.befta.util;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

import uk.gov.hmcts.befta.exception.FunctionalTestException;

public class JsonUtils {

    private static final ObjectMapper mapper = JsonMapper.builderWithJackson2Defaults().build();

    public static <T> T readObjectFromJsonResource(String resource, Class<T> objectType)
            throws IOException {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        return readOrWrite(() -> mapper.readValue(inputStream, objectType));
    }

    public static <T> T readObjectFromJsonFile(String file, Class<T> objectType)
            throws IOException {
        return readOrWrite(() -> mapper.readValue(new File(file), objectType));
    }

    public static <T> T readObjectFromJsonText(String jsonText, Class<T> objectType)
            throws IOException {
        if (jsonText == null) {
            return null;
        }
        return readOrWrite(() -> mapper.readValue(jsonText, objectType));
    }

    public static String getJsonFromObject(Object object) throws IOException {
        return readOrWrite(() -> mapper.writeValueAsString(object));
    }

    public static String getPrettyJsonFromObject(Object object) throws IOException {
        return readOrWrite(() -> mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object));
    }

    public static void writeJsonToFile(String filePath, Object object) throws IOException {
        readOrWrite(() -> {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), object);
            return null;
        });
    }

    private static <T> T readOrWrite(Supplier<T> operation) throws IOException {
        try {
            return operation.get();
        } catch (JacksonException exception) {
            throw new IOException(exception.getMessage(), exception);
        }
    }

    public static Object deepCopy(Object original) {
        try {
            if (original == null)
                return null;
            String jsonString = mapper.writeValueAsString(original);
            return mapper.readValue(jsonString, original.getClass());
        } catch (Exception e) {
            throw new FunctionalTestException("Unable to deep copy object.", e);
        }
    }
}
