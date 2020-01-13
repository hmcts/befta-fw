package uk.gov.hmcts.befta.util;

import java.util.List;
import java.util.Map;

import ch.qos.logback.core.net.*;
import io.cucumber.java.*;
import org.slf4j.*;
import uk.gov.hmcts.befta.*;
import uk.gov.hmcts.befta.data.*;
import uk.gov.hmcts.befta.exception.FunctionalTestException;
import uk.gov.hmcts.befta.player.BackEndFunctionalTestScenarioContext;

public class DynamicValueInjector {

    private static final String DYNAMIC_CONTENT_PLACEHOLDER = "[[DYNAMIC]]";
    private final TestAutomationAdapter taAdapter;
    private Logger logger = LoggerFactory.getLogger(DynamicValueInjector.class);

    private BackEndFunctionalTestScenarioContext scenarioContext;
    private HttpTestData testData;

    public DynamicValueInjector(TestAutomationAdapter taAdapter, HttpTestData testData,
            BackEndFunctionalTestScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
        this.testData = testData;
        this.taAdapter = taAdapter;
    }

    public void injectDataFromContext() {
        injectRequestDetailsFromContext();
    }

    private void injectRequestDetailsFromContext() {
        RequestData requestData = testData.getRequest();
        Map<String, Object> requestHeaders = requestData.getHeaders();
        if (requestHeaders != null) {
            requestHeaders.forEach((header, value) -> requestHeaders.put(header,
                    getDynamicValueFor("request.headers", header, value)));
        }

        Map<String, Object> pathVariables = requestData.getPathVariables();
        if (pathVariables != null) {
            pathVariables.forEach(
                    (key, value) -> pathVariables.put(key, getDynamicValueFor("request.pathVariables", key, value)));
        }

        Map<String, Object> queryParams = requestData.getQueryParams();
        if (queryParams != null) {
            queryParams.forEach(
                    (key, value) -> queryParams.put(key, getDynamicValueFor("request.queryParams", key, value)));
        }
        injectDynamicValuesInto("request.body", requestData.getBody());
    }

    private Object getDynamicValueFor(String path, String key, Object value) {
        if (value == null || !(value instanceof String))
            return value;

        String valueString = (String) value;
        if (valueString.equalsIgnoreCase(DYNAMIC_CONTENT_PLACEHOLDER)) {
            UserData theInvokingUser = scenarioContext.getTheInvokingUser();
            String s2sToken = null;
            if (key.equalsIgnoreCase("Authorization")) {
                return "Bearer " + theInvokingUser.getAccessToken();
            } else if (key.equalsIgnoreCase("ServiceAuthorization")) {
                if ((s2sToken = taAdapter.getNewS2SToken()) != null) {
                    return s2sToken;
                }
            } else if (key.equalsIgnoreCase("uid") && theInvokingUser.getId() != null) {
                return theInvokingUser.getId();
            } else if (key.equalsIgnoreCase("cid")) {
                return calculateFromContext(scenarioContext,
                        "${[scenarioContext][childContexts][Standard_Full_Case_Creation_Data][testData][actualResponse][body][id]}");
            }
            throw new FunctionalTestException("Dynamic value for '" + path + "." + key + "' does not exist!");
        } else if (isFormula(valueString)) {
            return calculateFromContext(scenarioContext, valueString);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    private void injectDynamicValuesInto(String path, Map<String, Object> map) {
        if (map == null) {
            return;
        }
        map.forEach((key, value) -> {
            if (value instanceof String) {
                if (isFormula((String) value)) {
                    map.put(key, calculateFromContext(scenarioContext, (String) value));
                }
            } else if (isArray(value)) {
                injectDynamicValuesInto(path + "." + key, (Object[]) value);
            } else if (value instanceof Map<?, ?>) {
                injectDynamicValuesInto(path + "." + key, (Map<String, Object>) value);

                ((Map<String, Object>) value).forEach((keyA, valueA) -> {
                    if (isUrl(valueA.toString())){
                        Object envVariable = calculateFromEnvVariable (valueA.toString());
                        ((Map<String, Object>) value).replace(keyA, valueA, envVariable);
                    }
                });
            }
        });

    }

    @SuppressWarnings("unchecked")
    private void injectDynamicValuesInto(String path, Object[] objects) {
        if (objects == null) {
            return;
        }
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] instanceof String) {
                if (isFormula((String) objects[i])) {
                    objects[i] = calculateFromContext(scenarioContext, (String) objects[i]);
                }
            } else if (objects[i] instanceof Map<?, ?>) {
                injectDynamicValuesInto(path + "[" + i + "]", (Map<String, Object>) objects[i]);
            } else if (isArray(objects[i])) {
                injectDynamicValuesInto(path + "[" + i + "]", (Object[]) objects[i]);
            }
        }
    }

    private boolean isFormula(String valueString) {
        return valueString != null && valueString.startsWith("${") && valueString.endsWith("}");
    }

    private boolean isUrl(String valueString) {
        return valueString != null && valueString.contains("${[") && valueString.contains("]}");
    }

    private Object calculateFromContext(Object container, String formula) {
        String[] fields = formula.substring(3).split("\\]\\[|\\]\\}");
        return calculateInContainer(container, fields, 1);
    }

    private String calculateFromEnvVariable(String value) {
        String envVariable = value.substring((value.indexOf("${[") + 3) , value.indexOf("]}"));
        String envVariableValue = BeftaMain.getConfig().getDocumentManagementUrl(envVariable);
        String valueSubString = value.substring((value.indexOf("]}") + 2));
        return envVariableValue + valueSubString;
    }

    private Object calculateInContainer(Object container, String[] fields, int fieldIndex) {
        Object value = null;
        if (isArray(container)) {
            value = ((Object[]) container)[Integer.parseInt(fields[fieldIndex])];
        } else if (container instanceof List<?>) {
            value = ((List<?>) container).get(Integer.parseInt(fields[fieldIndex]));
        } else if (container instanceof Map<?, ?>) {
            value = ((Map<?, ?>) container).get(fields[fieldIndex]);
        } else {
            try {
                value = ReflectionUtils.retrieveFieldInObject(container, fields[fieldIndex]);
            } catch (Exception e) {
                throw new FunctionalTestException(e.getMessage());
            }
        }
        if (fieldIndex == fields.length - 1) {
            return value;
        } else {
            return calculateInContainer(value, fields, fieldIndex + 1);
        }

    }

    private boolean isArray(Object object) {
        return object != null && object.getClass().isArray();
    }

}
