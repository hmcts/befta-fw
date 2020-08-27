package uk.gov.hmcts.befta.util;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;

public class EnvironmentVariableUtilsTest {

    private static final String ENV_VAR_NAME = "ENV_VAR";
    private static final String RETURN_VALUE = "VALUE";
    private static final String KEY = "[[$ENV_VAR]]";
    private static final String ERROR_MESSAGE = "Environment variable `ENV_VAR` is required";
    private static final String NULL_VALUE = null;
    private static final String INVALID_KEY = "[$ENV_VAR]";

    @Test
    @SetEnvironmentVariable(key = ENV_VAR_NAME, value = RETURN_VALUE)
    public void shouldResolveVariableWithKeyMatchingPattern() {
        assertEquals(RETURN_VALUE, EnvironmentVariableUtils.resolvePossibleVariable(KEY));
    }

    @Test
    public void shouldReturnSameValueWithKeyNotMatchingPattern() {
        assertEquals(INVALID_KEY, EnvironmentVariableUtils.resolvePossibleVariable(INVALID_KEY));
    }

    @Test
    public void shouldThrowExceptionWhenResolvingKeyWithoutValue() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            EnvironmentVariableUtils.resolvePossibleVariable(KEY);
          });            
    }

    @Test
    @SetEnvironmentVariable(key = ENV_VAR_NAME, value = RETURN_VALUE)
    public void shouldReturnRequiredVariableWhenExists() {
        assertEquals(RETURN_VALUE, EnvironmentVariableUtils.getRequiredVariable(ENV_VAR_NAME));
    }

    @Test
    public void shouldThrowExceptionWhenRequiredVariableDoesNotExist() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            EnvironmentVariableUtils.getRequiredVariable(ENV_VAR_NAME);
          });     
    }

    @Test
    @SetEnvironmentVariable(key = ENV_VAR_NAME, value = RETURN_VALUE)
    public void shouldReturnOptionalVariableWhenExists() {
        assertEquals(RETURN_VALUE, EnvironmentVariableUtils.getOptionalVariable(ENV_VAR_NAME));
    }

    @Test
    public void shouldReturnNullWhenOptionalVariableDoesNotExist() {
        assertEquals(NULL_VALUE, EnvironmentVariableUtils.getOptionalVariable(ENV_VAR_NAME));
    }

}
