package uk.gov.hmcts.befta.util;

import com.github.rholder.retry.Attempt;
import com.github.rholder.retry.RetryListener;
import io.restassured.internal.RestAssuredResponseImpl;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@Slf4j
@Builder
@Getter
@ToString(exclude = "retryListener")
@SuppressWarnings("UnstableApiUsage")
public class Retryable {
    private static final String BEFTA_RETRY_MAX_ATTEMPTS_ENV_VAR = "BEFTA_RETRY_MAX_ATTEMPTS";
    private static final String BEFTA_RETRY_MAX_ATTEMPTS_PROPERTY = "befta.retry.maxAttempts";
    private static final String BEFTA_RETRY_STATUS_CODES_ENV_VAR = "BEFTA_RETRY_STATUS_CODES";
    private static final String BEFTA_RETRY_STATUS_CODES_PROPERTY = "befta.retry.statusCodes";
    private static final String BEFTA_RETRY_MAX_DELAY_ENV_VAR = "BEFTA_RETRY_MAX_DELAY";
    private static final String BEFTA_RETRY_MAX_DELAY_PROPERTY = "befta.retry.maxDelay";
    private static final String BEFTA_RETRY_RETRYABLE_EXCEPTIONS_ENV_VAR = "BEFTA_RETRY_RETRYABLE_EXCEPTIONS";
    private static final String BEFTA_RETRY_RETRYABLE_EXCEPTIONS_PROPERTY = "befta.retry.retryableExceptions";
    private static final String BEFTA_RETRY_NON_RETRYABLE_HTTP_METHODS_ENV_VAR =
            "BEFTA_RETRY_NON_RETRYABLE_HTTP_METHODS";
    private static final String BEFTA_RETRY_NON_RETRYABLE_HTTP_METHODS_PROPERTY = "befta.retry.nonRetryableHttpMethods";
    private static final String BEFTA_RETRY_ENABLE_LISTENER_ENV_VAR = "BEFTA_RETRY_ENABLE_LISTENER";
    private static final String BEFTA_RETRY_ENABLE_LISTENER_PROPERTY = "befta.retry.enable.listener";

    private static final int DEFAULT_MAX_ATTEMPTS = 1;
    private static final String DEFAULT_STATUS_CODES = "";
    private static final int DEFAULT_MAX_DELAY = 0;
    private static final String DEFAULT_RETRYABLE_EXCEPTIONS = "";
    private static final String DEFAULT_NON_RETRYABLE_HTTP_METHODS = "";

    private static final String resourceName = "retry-config.properties";

    public static final Retryable DEFAULT_RETRYABLE = Retryable.builder().build();
    public static final Retryable RETRYABLE_FROM_CONFIG = createFromConfiguration(resourceName);

    @Builder.Default
    private int maxAttempts = DEFAULT_MAX_ATTEMPTS;
    @Builder.Default
    private Set<Integer> statusCodes = new HashSet<>();
    @Builder.Default
    private Map<String, String> match = new HashMap<>();
    @Builder.Default
    private int delay = DEFAULT_MAX_DELAY;
    @Builder.Default
    private Set<Class<? extends Exception>> retryableExceptions = new HashSet<>();
    @Builder.Default
    private Set<String> nonRetryableHttpMethods = new HashSet<>(Collections.singleton("*"));
    private RetryListener retryListener;

    public static Retryable createFromConfiguration(String configFile) {
        if (configFile == null || configFile.isEmpty()) {
            return DEFAULT_RETRYABLE;
        }

        try (InputStream input = Retryable.class.getClassLoader().getResourceAsStream(configFile)) {
            if (input == null) {
                return DEFAULT_RETRYABLE;
            }

            Properties properties = new Properties();
            properties.load(input);

            String disableListener = getEnvironmentOrProperty(properties, BEFTA_RETRY_ENABLE_LISTENER_ENV_VAR,
                    BEFTA_RETRY_ENABLE_LISTENER_PROPERTY, "true");

            Retryable retryable = Retryable.builder()
                    .maxAttempts(Integer.parseInt(getEnvironmentOrProperty(properties, BEFTA_RETRY_MAX_ATTEMPTS_ENV_VAR,
                            BEFTA_RETRY_MAX_ATTEMPTS_PROPERTY, String.valueOf(DEFAULT_MAX_ATTEMPTS))))
                    .statusCodes(parseStatusCodes(getEnvironmentOrProperty(properties, BEFTA_RETRY_STATUS_CODES_ENV_VAR,
                            BEFTA_RETRY_STATUS_CODES_PROPERTY, DEFAULT_STATUS_CODES)))
                    .delay(Integer.parseInt(getEnvironmentOrProperty(properties, BEFTA_RETRY_MAX_DELAY_ENV_VAR,
                            BEFTA_RETRY_MAX_DELAY_PROPERTY, String.valueOf(DEFAULT_MAX_DELAY))))
                    .retryableExceptions(parseRetryableExceptions(getEnvironmentOrProperty(properties,
                            BEFTA_RETRY_RETRYABLE_EXCEPTIONS_ENV_VAR, BEFTA_RETRY_RETRYABLE_EXCEPTIONS_PROPERTY,
                            DEFAULT_RETRYABLE_EXCEPTIONS)))
                    .nonRetryableHttpMethods(parseHttpMethods(getEnvironmentOrProperty(properties,
                            BEFTA_RETRY_NON_RETRYABLE_HTTP_METHODS_ENV_VAR,
                            BEFTA_RETRY_NON_RETRYABLE_HTTP_METHODS_PROPERTY,
                            DEFAULT_NON_RETRYABLE_HTTP_METHODS)))
                    .retryListener(setRetryListener(Boolean.parseBoolean(disableListener)))
                    .build();

            log.info("""
                            Creating DEFAULT retry policy with the following configuration:
                              Max attempts: {}
                              Retry on status codes: {}
                              Retry on exceptions: {}
                              No retry on http methods: {}
                              Delay between retries: {}ms.""",
                    retryable.getMaxAttempts(), retryable.getStatusCodes(), retryable.getRetryableExceptions(),
                    retryable.getNonRetryableHttpMethods(), retryable.getDelay());

            return retryable;
        } catch (Exception e) {
            return DEFAULT_RETRYABLE;
        }
    }

    public static RetryListener setRetryListener(boolean isEnableListener) {
        log.info("Initializing retry listener...");
        return new RetryListener() {
            @Override
            public <V> void onRetry(Attempt<V> attempt) {
                if (isEnableListener) {
                    StringBuilder logMessage = new StringBuilder();
                    logMessage.append(String.format("Retry attempt: %d. ", attempt.getAttemptNumber()));

                    if (attempt.hasException()) {
                        logMessage.append(String.format("exception: '%s'. ", attempt.getExceptionCause()));
                    } else if (attempt.getResult() instanceof RestAssuredResponseImpl result) {
                        logMessage.append(String.format("result: '%s'. ", result.response().getStatusLine().trim()));
                    }

                    logMessage.append(String.format("%d ms delay since the first attempt",
                            attempt.getDelaySinceFirstAttempt()));

                    log.info(logMessage.toString());
                }
            }
        };
    }

    private static String getEnvironmentOrProperty(Properties properties, String envVarName, String propertyName,
                                                   String defaultValue) {
        String envVarValue = System.getenv(envVarName);
        return envVarValue != null ? envVarValue : properties.getProperty(propertyName, defaultValue);
    }

    private static Set<String> parseHttpMethods(final String httpMethods) {
        Set<String> methods = new HashSet<>();
        if (!httpMethods.isEmpty()) {
            String[] methodsArray = httpMethods.split(",");
            for (String method : methodsArray) {
                methods.add(method.trim());
            }
        }
        return methods;
    }

    private static Set<Integer> parseStatusCodes(final String statusCodes) {
        Set<Integer> codes = new HashSet<>();
        if (!statusCodes.isEmpty()) {
            String[] codesArray = statusCodes.split(",");
            for (String code : codesArray) {
                codes.add(Integer.parseInt(code.trim()));
            }
        }
        return codes;
    }

    @SuppressWarnings("unchecked")
    private static Set<Class<? extends Exception>> parseRetryableExceptions(final String exceptions) {
        Set<Class<? extends Exception>> exceptionSet = new HashSet<>();
        if (!exceptions.isEmpty()) {
            String[] exceptionsArray = exceptions.split(",");
            for (String exceptionClassName : exceptionsArray) {
                try {
                    Class<?> exceptionClass = Class.forName(exceptionClassName.trim());
                    if (Exception.class.isAssignableFrom(exceptionClass)) {
                        exceptionSet.add((Class<? extends Exception>) exceptionClass);
                    }
                } catch (Exception e) {
                    // Ignore  exception if the class is not found
                }
            }
        }
        return exceptionSet;
    }
}
