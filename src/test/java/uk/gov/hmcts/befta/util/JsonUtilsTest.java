package uk.gov.hmcts.befta.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import uk.gov.hmcts.befta.data.HttpTestData;
import uk.gov.hmcts.befta.data.RequestData;


@RunWith(PowerMockRunner.class)
@PrepareForTest(JsonUtilsTest.class)
public class JsonUtilsTest {

    private static final String TEST_RESOURCE = "framework-test-data/features/F-000/S-000.td.json";

    @Test
    public void shouldReadObjectFromJsonResource() throws IOException {
        final HttpTestData result = JsonUtils.readObjectFromJsonResource(TEST_RESOURCE, HttpTestData.class);

        assertEquals("S-000", result.get_guid_());
        assertEquals("[[DYNAMIC]]", result.getRequest().getPathVariables().get("uid"));
        assertEquals(200, result.getExpectedResponse().getResponseCode());
    }

    @Test
    public void shouldReadObjectFromJsonFile() throws IOException {
        URL url = ClassLoader.getSystemResource(TEST_RESOURCE);

        final HttpTestData result = JsonUtils.readObjectFromJsonFile(url.getPath(), HttpTestData.class);

        assertEquals("S-000", result.get_guid_());
        assertEquals("[[DYNAMIC]]", result.getRequest().getPathVariables().get("uid"));
        assertEquals(200, result.getExpectedResponse().getResponseCode());
    }

    @Test
    public void shouldReadObjectFromJsonText() throws IOException {
        final String testValue = "{\"key1\":\"value1\",\"key2\":\"value2\"}";

        final Map result = JsonUtils.readObjectFromJsonText(testValue, Map.class);

        assertEquals(2, result.keySet().size());
        assertEquals("value1", result.get("key1"));
        assertEquals("value2", result.get("key2"));
    }

    @Test
    public void shouldGetJsonFromObject() throws IOException {
        RequestData testObject = new RequestData();
        testObject.setBody(new HashMap<String, Object>(){{
            put("key1", "value1");
            put("key2", "value2");
        }});

        final String expectedResult = "{\"headers\":null,\"pathVariables\":null,\"queryParams\":null," +
                "\"body\":{\"key1\":\"value1\",\"key2\":\"value2\"},\"multipart\":false}";

        final String result = JsonUtils.getJsonFromObject(testObject);

        assertEquals(expectedResult, result);
    }

    @Test
    public void shouldGetPrettyJsonFromObject() throws IOException {
        RequestData testObject = new RequestData();
        testObject.setBody(new HashMap<String, Object>(){{
            put("key1", "value1");
            put("key2", "value2");
        }});

        final String expectedResult = "{\n" +
                "  \"headers\" : null,\n" +
                "  \"pathVariables\" : null,\n" +
                "  \"queryParams\" : null,\n" +
                "  \"body\" : {\n" +
                "    \"key1\" : \"value1\",\n" +
                "    \"key2\" : \"value2\"\n" +
                "  },\n" +
                "  \"multipart\" : false\n" +
                "}";

        final String result = JsonUtils.getPrettyJsonFromObject(testObject);

        assertEquals(expectedResult, result);
    }
}
