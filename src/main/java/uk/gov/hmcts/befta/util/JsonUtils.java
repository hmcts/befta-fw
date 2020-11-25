package uk.gov.hmcts.befta.util;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import uk.gov.hmcts.befta.exception.FunctionalTestException;

public class JsonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T readObjectFromJsonResource(String resource, Class<T> objectType)
            throws JsonParseException, JsonMappingException, IOException {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        return mapper.readValue(inputStream, objectType);
    }

    public static <T> T readObjectFromJsonFile(String file, Class<T> objectType)
            throws JsonParseException, JsonMappingException, IOException {
        return mapper.readValue(new File(file), objectType);
    }

    public static <T> T readObjectFromJsonText(String jsonText, Class<T> objectType)
            throws JsonParseException, JsonMappingException, IOException {
        if (jsonText == null) {
            return null;
        }
        return mapper.readValue(jsonText, objectType);
    }

    public static String getJsonFromObject(Object object) throws JsonParseException, JsonMappingException, IOException {
        return mapper.writeValueAsString(object);
    }

    public static String getPrettyJsonFromObject(Object object) throws JsonParseException, JsonMappingException, IOException {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }

    public static void writeJsonToFile(String filePath, Object object) throws IOException, JsonGenerationException, JsonMappingException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), object);
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
