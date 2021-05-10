package uk.gov.hmcts.befta.util;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import uk.gov.hmcts.befta.TestAutomationConfig;

public class RestUtils {

    public static boolean getApiFlagValue(String pathVariable) {
        RestAssured.useRelaxedHTTPSValidation();

        RestAssured.baseURI = TestAutomationConfig.INSTANCE.getTestUrl();

        String path = "/" + EnvironmentVariableUtils.getRequiredVariable("EXTERNAL_FLAG_QUERY_PATH") + pathVariable;
        Response response = RestAssured.get(path);

        if (response.getStatusCode() == HttpStatus.SC_OK) {
            return response.getBody().as(Boolean.class);
        }
        return false;
    }
}
