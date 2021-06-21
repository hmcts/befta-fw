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

    public static String getRequiredVariable(String name1, String name2) {
        String value = System.getenv(name1);
        if (value == null) {
            value = System.getenv(name2);
        }
        return Validate.notNull(value, "Either `%s` or `%s` is required in environment variables.", name1, name2);
    }

    public static String getOptionalVariable(String name) {
        return System.getenv(name);
    }
}
