package uk.gov.hmcts.befta.util;

import org.apache.commons.lang3.Validate;
import org.junit.Assert;

public class EnvUtils {

    public static String resolvePossibleEnvironmentVariable(String key) {
        if (key.startsWith("[[$")) {
            String envKey = key.substring(3, key.length() - 2);
            String envValue = require(envKey);
            String errorMessage = "Specified environment variable '" + envValue + "' not found";
            Assert.assertNotNull(errorMessage, envValue);
            return envValue;
        }
        return key;
    }

    public static String require(String name) {
        return Validate.notNull(System.getenv(name), "Environment variable `%s` is required", name);
    }
}
