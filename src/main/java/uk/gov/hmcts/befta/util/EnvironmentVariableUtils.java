package uk.gov.hmcts.befta.util;

import org.apache.commons.lang3.Validate;

public class EnvironmentVariableUtils {

    public static String resolvePossibleVariable(String key) {
        if (key.startsWith("[[$")) {
            String envKey = key.substring(3, key.length() - 2);
            String envValue = getRequiredVariable(envKey);
            return envValue;
        }
        return key;
    }

    public static String getRequiredVariable(String name) {
        return Validate.notNull(System.getenv(name), "Environment variable `%s` is required", name);
    }

    public static String getOptionalVariable(String name) {
        return System.getenv(name);
    }
}
