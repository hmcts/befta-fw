package uk.gov.hmcts.befta.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import uk.gov.hmcts.befta.data.RequestData;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;


@RunWith(PowerMockRunner.class)
@PrepareForTest(JsonUtilsTest.class)
public class JsonUtilsTest {

    private static final String ENV_VAR_NAME = "ENV_VAR";

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void shouldReadObjectFromJsonText() throws IOException {
        final String testValue = "{\"key1\":\"value1\",\"key2\":\"value2\"}";

        final Map result = JsonUtils.readObjectFromJsonText(testValue, Map.class);

        assertEquals(2, result.keySet().size());
        assertEquals("value1", result.get("key1"));
        assertEquals("value2", result.get("key2"));
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
                "  }\n" +
                "}";

        final String result = JsonUtils.getPrettyJsonFromObject(testObject);

        assertEquals(expectedResult, result);
    }
}
