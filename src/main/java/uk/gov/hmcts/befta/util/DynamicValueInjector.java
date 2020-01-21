package uk.gov.hmcts.befta.util;

import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.gov.hmcts.befta.BeftaMain;
import uk.gov.hmcts.befta.TestAutomationAdapter;
import uk.gov.hmcts.befta.data.HttpTestData;
import uk.gov.hmcts.befta.data.RequestData;
import uk.gov.hmcts.befta.data.UserData;
import uk.gov.hmcts.befta.exception.FunctionalTestException;
import uk.gov.hmcts.befta.player.BackEndFunctionalTestScenarioContext;

public class DynamicValueInjector {

    private static final String DYNAMIC_CONTENT_PLACEHOLDER = "[[DYNAMIC]]";
    private static final String DYNAMIC_REGEX_DATA = "\\]\\[|\\]\\}|\\]\\{";
    private static final String DYNAMIC_REGEX_ENVIRONMENT_VARIABLE = "\\}\\{|\\}\\}|\\}\\[";
    private final TestAutomationAdapter taAdapter;

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
                return calculateFormulaFromContext(scenarioContext,
                        "${[scenarioContext][childContexts][Standard_Full_Case_Creation_Data][testData][actualResponse][body][id]}");
            }
            throw new FunctionalTestException("Dynamic value for '" + path + "." + key + "' does not exist!");
        } else if (isPartFormula((String) value)) {
            Object retrievedValue = new Object();
            return getFormulaOrEnvironmentVariables((String) value);
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
                Object resolvedValue = new Object();
                if (isPartFormula((String) value)) {
                    resolvedValue = getFormulaOrEnvironmentVariables((String) value);
                }
                map.put(key, resolvedValue);
            } else if (isArray(value)) {
                injectDynamicValuesInto(path + "." + key, (Object[]) value);
            } else if (value instanceof Map<?, ?>) {
                injectDynamicValuesInto(path + "." + key, (Map<String, Object>) value);
            }
        });

    }

    private Object getFormulaOrEnvironmentVariables(String value) {
        String preText = Strings.EMPTY;
        String postText = Strings.EMPTY;
        String toAnalyse;
        if (!isFormula(value)) {
            preText = value.substring(0, value.indexOf("${"));
            postText = value.substring((value.lastIndexOf("}") + 1));
            toAnalyse = value.substring(value.indexOf("${"), (value.lastIndexOf("}") + 1));
        } else {
            toAnalyse = value;
        }

        Object retrievedValue = new Object();

        if (isFormula(toAnalyse)) {
            if (isData(value, DYNAMIC_REGEX_DATA) && isData(toAnalyse, DYNAMIC_REGEX_ENVIRONMENT_VARIABLE)) {
                retrievedValue = calculateFromContext(scenarioContext, toAnalyse);
            } else if (isData(toAnalyse, DYNAMIC_REGEX_DATA) && !isData(toAnalyse, DYNAMIC_REGEX_ENVIRONMENT_VARIABLE)) {
                retrievedValue = calculateFormulaFromContext(scenarioContext, toAnalyse);
            } else if (isData(toAnalyse, DYNAMIC_REGEX_ENVIRONMENT_VARIABLE) && !isData(toAnalyse, DYNAMIC_REGEX_DATA)) {
                retrievedValue = calculateFromContextForEnvVariable(toAnalyse);
            }
        }
        return preText + retrievedValue + postText;
    }

    @SuppressWarnings("unchecked")
    private void injectDynamicValuesInto(String path, Object[] objects) {
        if (objects == null) {
            return;
        }
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] instanceof String) {
                if (isFormula((String) objects[i])) {
                    objects[i] = calculateFormulaFromContext(scenarioContext, (String) objects[i]);
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

    private boolean isPartFormula(String valueString) {
        return valueString != null && valueString.contains("${") && valueString.contains("}");
    }

    private boolean isData(String valueString, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(valueString);
        return m.find();
    }

    private Object calculateFormulaFromContext(Object container, String formula) {
        String[] fields = formula.substring(3).split(DYNAMIC_REGEX_DATA);
        return calculateInContainer(container, fields, 1);
    }

    private Object calculateFromContext(Object container, String formula) {
        ArrayList<String> dataItems = new ArrayList<>();
        ArrayList<String> envItems = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < formula.length(); i++) {
            if (formula.length() != (i + 1)) {
                String s = formula.substring(i, (i + 2));
                if (s.matches("\\}\\[|\\}\\}|\\}\\{")) {
                    envItems.add(formula.substring(count, i).replaceAll("\\]\\{|\\]\\}|\\}\\[|\\}\\{|\\$\\{\\{|\\$\\{\\[", ""));
                    count = i;
                } else if (s.matches("\\]\\{|\\]\\}|\\]\\[")) {
                    dataItems.add(formula.substring(count, i).replaceAll("\\]\\{|\\]\\}|\\}\\[|\\}\\{|\\$\\{\\{|\\$\\{\\[|\\]\\[", ""));
                    count = i;
                }
            }
        }

        String responseString = retrieveValues(container, formula, dataItems, envItems);
        return responseString;
    }

    private String retrieveValues(Object container, String formula, ArrayList<String> dataItems, ArrayList<String> envItems) {
        HashMap<String, String> environmentVariableHashMap = new HashMap<>();
        for (int i = 0; i < envItems.size(); i++) {
            environmentVariableHashMap.put(envItems.get(i), calculateFromEnvVariable(envItems.get(i)));
        }

        String[] stringArray = new String[dataItems.size()];
        for (int i = 0; i < dataItems.size(); i++) {
            stringArray[i] = dataItems.get(i);
        }
        Object response = calculateInContainer(container, stringArray, 1);

        String responseString = Strings.EMPTY;
        for (Map.Entry<String, String> entry : environmentVariableHashMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            responseString += formula.replaceAll(key, value.toString());

        }

        responseString = responseString.replace((responseString.substring(responseString.indexOf("["), (responseString.lastIndexOf("]") + 1))), response.toString());
        responseString = responseString.replaceAll("\\$|\\{|\\}", "");
        return responseString;
    }

    private Object calculateFromContextForEnvVariable(String formula) {
        String[] fields = formula.substring(3).split(DYNAMIC_REGEX_ENVIRONMENT_VARIABLE);
        return calculateEnvironmentVariables(fields, 0);
    }

    private String calculateFromEnvVariable(String value) {
        return BeftaMain.getConfig().getEnvironmentVariable(value);
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

    private String calculateEnvironmentVariables(String[] fields, int fieldIndex) {
        String value = Strings.EMPTY;
        for (int i = 0; i < fields.length; i++) {
            value += calculateFromEnvVariable(fields[fieldIndex]);
        }
        return value;
    }

    private boolean isArray(Object object) {
        return object != null && object.getClass().isArray();
    }

}
