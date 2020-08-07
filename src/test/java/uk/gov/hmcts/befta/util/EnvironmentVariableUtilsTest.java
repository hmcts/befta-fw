package uk.gov.hmcts.befta.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

public class EnvironmentVariableUtilsTest {

    private static final String ENV_VAR_NAME = "ENV_VAR";
    private static final String RETURN_VALUE = "VALUE";
    private static final String KEY = "[[$ENV_VAR]]";
    private static final String ERROR_MESSAGE = "Environment variable `ENV_VAR` is required";
    private static final String NULL_VALUE = null;
    private static final String INVALID_KEY = "[$ENV_VAR]";


    @Test
    public void shouldResolveVariableWithKeyMatchingPattern() {
        // Mock scope
        try (MockedStatic <EnvironmentVariableUtils>mocked = mockStatic(EnvironmentVariableUtils.class)) {
            // Mocking
            mocked.when(() -> EnvironmentVariableUtils.resolvePossibleVariable(KEY)).thenReturn(RETURN_VALUE);
            // Mocked behavior
            assertEquals(RETURN_VALUE, EnvironmentVariableUtils.resolvePossibleVariable(KEY));
            // Verifying mocks.
            mocked.verify(times(1), () -> EnvironmentVariableUtils.resolvePossibleVariable(KEY));
            mocked.verifyNoMoreInteractions();
        }
    }

    @Test
    public void shouldReturnSameValueWithKeyNotMatchingPattern() {
        // Mock scope
        try (MockedStatic <EnvironmentVariableUtils>mocked = mockStatic(EnvironmentVariableUtils.class)) {
            // Mocking
            mocked.when(() -> EnvironmentVariableUtils.resolvePossibleVariable(INVALID_KEY)).thenReturn(INVALID_KEY);
            // Mocked behavior
            assertEquals(INVALID_KEY, EnvironmentVariableUtils.resolvePossibleVariable(INVALID_KEY));
            // Verifying mocks.
            mocked.verify(times(1), () -> EnvironmentVariableUtils.resolvePossibleVariable(INVALID_KEY));
        }
    }

    @Test
    public void shouldThrowExceptionWhenResolvingKeyWithoutValue() {
        // Mock scope
        try (MockedStatic <EnvironmentVariableUtils>mocked = mockStatic(EnvironmentVariableUtils.class)) {
            // Mocking
            mocked.when(() -> EnvironmentVariableUtils.resolvePossibleVariable(KEY)).thenThrow(new NullPointerException(ERROR_MESSAGE));;
            // Mocked behavior
            Assertions.assertThrows(NullPointerException.class, () -> {
                EnvironmentVariableUtils.resolvePossibleVariable(KEY);
              });            // Verifying mocks.
            mocked.verify(times(1), () -> EnvironmentVariableUtils.resolvePossibleVariable(KEY));
        }
    }

    @Test
    public void shouldReturnRequiredVariableWhenExists() {
        // Mock scope
        try (MockedStatic <EnvironmentVariableUtils>mocked = mockStatic(EnvironmentVariableUtils.class)) {
            // Mocking
            mocked.when(() -> EnvironmentVariableUtils.getRequiredVariable(ENV_VAR_NAME)).thenReturn(RETURN_VALUE);
            // Mocked behavior
            assertEquals(RETURN_VALUE, EnvironmentVariableUtils.getRequiredVariable(ENV_VAR_NAME));
            // Verifying mocks.
            mocked.verify(times(1), () -> EnvironmentVariableUtils.getRequiredVariable(ENV_VAR_NAME));
        }
    }

    @Test
    public void shouldThrowExceptionWhenRequiredVariableDoesNotExist() {
        // Mock scope
        try (MockedStatic <EnvironmentVariableUtils>mocked = mockStatic(EnvironmentVariableUtils.class)) {
            // Mocking
            mocked.when(() -> EnvironmentVariableUtils.getRequiredVariable(ENV_VAR_NAME)).thenThrow(new NullPointerException(ERROR_MESSAGE));;
            // Mocked behavior
            Assertions.assertThrows(NullPointerException.class, () -> {
                EnvironmentVariableUtils.getRequiredVariable(ENV_VAR_NAME);
              });            // Verifying mocks.
            mocked.verify(times(1), () -> EnvironmentVariableUtils.getRequiredVariable(ENV_VAR_NAME));
        }
    }

    @Test
    public void shouldReturnOptionalVariableWhenExists() {
        // Mock scope
        try (MockedStatic <EnvironmentVariableUtils>mocked = mockStatic(EnvironmentVariableUtils.class)) {
            // Mocking
            mocked.when(() -> EnvironmentVariableUtils.getOptionalVariable(ENV_VAR_NAME)).thenReturn(RETURN_VALUE);
            // Mocked behavior
            assertEquals(RETURN_VALUE, EnvironmentVariableUtils.getOptionalVariable(ENV_VAR_NAME));
            // Verifying mocks.
            mocked.verify(times(1), () -> EnvironmentVariableUtils.getOptionalVariable(ENV_VAR_NAME));
        }
    }

    @Test
    public void shouldReturnNullWhenOptionalVariableDoesNotExist() {
        // Mock scope
        try (MockedStatic <EnvironmentVariableUtils>mocked = mockStatic(EnvironmentVariableUtils.class)) {
            // Mocking
            mocked.when(() -> EnvironmentVariableUtils.getOptionalVariable(ENV_VAR_NAME)).thenReturn(NULL_VALUE);
            // Mocked behavior
            assertEquals(NULL_VALUE, EnvironmentVariableUtils.getOptionalVariable(ENV_VAR_NAME));
            // Verifying mocks.
            mocked.verify(times(1), () -> EnvironmentVariableUtils.getOptionalVariable(ENV_VAR_NAME));
        }
    }

}
