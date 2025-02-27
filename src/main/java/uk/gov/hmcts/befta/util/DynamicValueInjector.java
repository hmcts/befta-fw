package uk.gov.hmcts.befta.util;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.befta.TestAutomationAdapter;
import uk.gov.hmcts.befta.data.HttpTestData;
import uk.gov.hmcts.befta.data.RequestData;
import uk.gov.hmcts.befta.data.UserData;
import uk.gov.hmcts.befta.exception.FunctionalTestException;
import uk.gov.hmcts.befta.player.BackEndFunctionalTestScenarioContext;

public class DynamicValueInjector {

    private Logger logger = LoggerFactory.getLogger(DynamicValueInjector.class);

    private static final String DEFAULT_AUTO_VALUE = "[[DEFAULT_AUTO_VALUE]]";

    private static final String EMPTY_STRING_MARKER = "EMPTY_STRING";

    private final TestAutomationAdapter taAdapter;

    private BackEndFunctionalTestScenarioContext scenarioContext;
    private HttpTestData testData;

    public DynamicValueInjector(TestAutomationAdapter taAdapter, HttpTestData testData,
            BackEndFunctionalTestScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
        this.testData = testData;
        this.taAdapter = taAdapter;
    }

    public void injectDataFromContextBeforeApiCall() {
        injectValuesDetailsFromContextBeforeApiCall();
    }

    public void injectDataFromContextAfterApiCall() {
        injectValuesDetailsFromContextAfterApiCall();
    }

    private void injectValuesDetailsFromContextBeforeApiCall() {
        RequestData requestData = testData.getRequest();
        testData.setUri((String) processDynamicValuesIn(testData.getUri()));
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

    private void injectValuesDetailsFromContextAfterApiCall() {
        injectDynamicValuesInto("expectedResponse.body", testData.getExpectedResponse().getBody());
    }

    private Object getDynamicValueFor(String path, String key, Object value) {
        if (value == null || !(value instanceof String))
            return value;
        String valueString = (String) value;
        if (valueString.equalsIgnoreCase(DEFAULT_AUTO_VALUE)) {
            UserData theInvokingUser = scenarioContext.getTheInvokingUser();
            String s2sToken = null;
            if (key.equalsIgnoreCase("Authorization")) {
                return "Bearer " + theInvokingUser.getAccessToken();
            } else if (key.equalsIgnoreCase("ServiceAuthorization")) {
                if ((s2sToken = taAdapter.getNewS2SToken(testData.getS2sClientId())) != null) {
                    return s2sToken;
                }
            } else if (key.equalsIgnoreCase("uid") && theInvokingUser.getId() != null) {
                return theInvokingUser.getId();
            } else if (key.equalsIgnoreCase("cid")) {
                return calculateFormulaFromContext(scenarioContext,
                        "${[scenarioContext][childContexts][Standard_Full_Case_Creation_Data][testData][actualResponse][body][id]}");
            }
            throw new FunctionalTestException("Dynamic value for '" + path + "." + key + "' does not exist!");
        }

        return processDynamicValuesIn(valueString);
    }

    private Object processDynamicValuesIn(String input) {
        if (input == null || input.equals(""))
            return input;
        StringBuffer output = new StringBuffer();
        Object outputAsNumber = null;
        boolean outputIsString = false;
        int pos = 0, jumpTo;
        Object partValue = null;
        while (pos < input.length()) {
            partValue = null;
            jumpTo = -1;
            if (aFormulaIsStartingAt(input, pos)) {
                int closingAt = input.indexOf("}", pos + 2);
                if (closingAt < 0) {
                    throw new RuntimeException(
                            "'${' is not matched with a '}' for " + input + " at position: " + pos + ".");
                }
                String formulaPart = input.substring(pos, closingAt + 1);
                partValue = calculateFormulaFromContext(scenarioContext, formulaPart);
                jumpTo = closingAt + 1;

                // special case when complete input is a dynamic formula
                // : so check if it returned a complex object to return
                if (pos == 0 && jumpTo >= input.length()) {
                    return partValue;
                }
            } else if (anEnvVarIsStartingAt(input, pos)) {
                int closingAt = input.indexOf("}}", pos + 2);
                if (closingAt < 0) {
                    throw new FunctionalTestException(
                            "'{{' is not matched with a '}}' for " + input + " at position: " + pos + ".");
                }
                String envVarName = input.substring(pos + 2, closingAt);
                partValue = EnvironmentVariableUtils.getRequiredVariable(envVarName);
                jumpTo = closingAt + 2;
            }
            if (jumpTo > 0) {
                pos = jumpTo;
                if (partValue instanceof Number) {
                    outputAsNumber = partValue;
                } else {
                    outputIsString = true;
                }
                output.append(partValue);
            } else {
                outputIsString = true;
                output.append(input.charAt(pos++));
            }
        }
        return outputIsString ? output.toString() : outputAsNumber;
    }

    private boolean aFormulaIsStartingAt(String input, int pos) {
        if (pos >= input.length() - 1)
            return false;
        return input.substring(pos, pos + 2).equals("${");
    }

    private boolean anEnvVarIsStartingAt(String input, int pos) {
        if (pos >= input.length() - 1)
            return false;
        return input.substring(pos, pos + 2).equals("{{");
    }

    @SuppressWarnings("unchecked")
    private void injectDynamicValuesInto(String path, Map<String, Object> map) {
        if (map == null) {
            return;
        }
        map.forEach((key, value) -> {
            if (value instanceof String) {
                map.put(key, processDynamicValuesIn((String) value));
            } else if (value instanceof Map<?, ?>) {
                injectDynamicValuesInto(path + "." + key, (Map<String, Object>) value);
            } else if (isArray(value)) {
                ArrayList<Object> values = new ArrayList<>(Arrays.asList((Object[]) value));
                injectDynamicValuesInto(path + "." + key, values);
                map.put(key, values);
            } else if (value instanceof Iterable) {
                ArrayList<Object> values = StreamSupport.stream(((Iterable<Object>) value).spliterator(), false)
                        .collect(Collectors.toCollection(ArrayList::new));
                injectDynamicValuesInto(path + "." + key, values);
                map.put(key, values);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void injectDynamicValuesInto(String path, List<Object> objects) {
        if (objects == null) {
            return;
        }
        for (int i = 0; i < objects.size(); i++) {
            Object value = objects.get(i);
            if (value instanceof String) {
                objects.set(i, processDynamicValuesIn((String) value));
            } else if (value instanceof Map<?, ?>) {
                injectDynamicValuesInto(path + "[" + i + "]", (Map<String, Object>) value);
            } else if (isArray(value)) {
                ArrayList<Object> values = new ArrayList<>(Arrays.asList((Object[]) value));
                injectDynamicValuesInto(path + "[" + i + "]", values);
                objects.set(i, values);
            } else if (value instanceof Iterable) {
                ArrayList<Object> values = StreamSupport.stream(((Iterable<Object>) value).spliterator(), false)
                        .collect(Collectors.toCollection(ArrayList::new));
                injectDynamicValuesInto(path + "[" + i + "]", values);
                objects.set(i, values);
            }
        }
    }

    private Object calculateFormulaFromContext(Object container, String formula) {
        if (formula.trim().equals("${}") || formula.trim().equalsIgnoreCase("${" + EMPTY_STRING_MARKER + "}")) {
            return "";
        }
        String[] fields = formula.substring(3).split("\\]\\[|\\]\\}");

        logger.debug("length of fields: {}", fields.length);
        if (fields.length <= 1) {
            throw new FunctionalTestException("No processable field found in " + formula);
        }
        return calculateInContainer(container, fields, 1);
    }

    @SuppressWarnings("unchecked")
    private Object calculateInContainer(Object container, String[] fields, int fieldIndex) {
        Object value = null;
        if (isArray(container)) {
            value = ((Object[]) container)[Integer.parseInt(fields[fieldIndex])];
        } else if (container instanceof List<?>) {
            logger.debug("length of fields: {} , fieldIndex: {}", fields.length, fieldIndex);
            if (collectionIsNotNullAndNotEmpty((List<?>) container)) {
                value = ((List<?>) container).get(Integer.parseInt(fields[fieldIndex]));
            }
        } else if (container instanceof Map<?, ?>) {
            value = ((Map<?, ?>) container).get(fields[fieldIndex]);
        } else if (container instanceof Function<?, ?>) {
            value = ((Function<String, Object>) container).apply(fields[fieldIndex]);
        } else {
            try {
                value = ReflectionUtils.retrieveFieldInObject(container, fields[fieldIndex]);
            } catch (Exception e) {
                throw new FunctionalTestException("Unable to extract " + fields[fieldIndex] + " from " + container, e);
            }
        }
        if (fieldIndex == fields.length - 1) {
            return value;
        } else {
            logger.debug("length of fields: {} , fieldIndex + 1: {} , value: {}", fields.length, fieldIndex + 1, value);
            return calculateInContainer(value, fields, fieldIndex + 1);
        }

    }

    private boolean collectionIsNotNullAndNotEmpty(List<?> collection) {
        return Objects.nonNull(collection) && !collection.isEmpty();
    }

    private boolean isArray(Object object) {
        return object != null && object.getClass().isArray();
    }

}
