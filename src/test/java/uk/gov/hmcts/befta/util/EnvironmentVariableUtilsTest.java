package uk.gov.hmcts.befta.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

public class EnvironmentVariableUtilsTest {

    private static final String ENV_VAR_NAME = "ENV_VAR";
    private static final String RETURN_VALUE = "VALUE";
    private static final String KEY = "[[$ENV_VAR]]";
    private static final String ERROR_MESSAGE = "Environment variable `ENV_VAR` is required";
    private static final String NULL_VALUE = null;
    private static final String INVALID_KEY = "[$ENV_VAR]";
    private MockedStatic<EnvironmentVariableUtils> environmentVariableUtilsMock = null;

    @BeforeEach
    public void prepareMockedObjectUnderTest() {
        try {
        	environmentVariableUtilsMock = mockStatic(EnvironmentVariableUtils.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void closeMockedObjectUnderTest() {
        try {
        	environmentVariableUtilsMock.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void shouldResolveVariableWithKeyMatchingPattern() {
        // Mocking
    	environmentVariableUtilsMock.when(() -> EnvironmentVariableUtils.resolvePossibleVariable(KEY)).thenReturn(RETURN_VALUE);
        // Mocked behavior
        assertEquals(RETURN_VALUE, EnvironmentVariableUtils.resolvePossibleVariable(KEY));
    }

    @Test
    public void shouldReturnSameValueWithKeyNotMatchingPattern() {
        // Mocking
    	environmentVariableUtilsMock.when(() -> EnvironmentVariableUtils.resolvePossibleVariable(INVALID_KEY)).thenReturn(INVALID_KEY);
        // Mocked behavior
        assertEquals(INVALID_KEY, EnvironmentVariableUtils.resolvePossibleVariable(INVALID_KEY));
    }

    @Test
    public void shouldThrowExceptionWhenResolvingKeyWithoutValue() {
        // Mocking
    	environmentVariableUtilsMock.when(() -> EnvironmentVariableUtils.resolvePossibleVariable(KEY)).thenThrow(new NullPointerException(ERROR_MESSAGE));;
        // Mocked behavior
        Assertions.assertThrows(NullPointerException.class, () -> {
            EnvironmentVariableUtils.resolvePossibleVariable(KEY);
          });            // Verifying mocks.
    }

    @Test
    public void shouldReturnRequiredVariableWhenExists() {
        // Mocking
    	environmentVariableUtilsMock.when(() -> EnvironmentVariableUtils.getRequiredVariable(ENV_VAR_NAME)).thenReturn(RETURN_VALUE);
        // Mocked behavior
        assertEquals(RETURN_VALUE, EnvironmentVariableUtils.getRequiredVariable(ENV_VAR_NAME));
    }

    @Test
    public void shouldThrowExceptionWhenRequiredVariableDoesNotExist() {
    	environmentVariableUtilsMock.when(() -> EnvironmentVariableUtils.getRequiredVariable(ENV_VAR_NAME)).thenThrow(new NullPointerException(ERROR_MESSAGE));;
        // Mocked behavior
        Assertions.assertThrows(NullPointerException.class, () -> {
            EnvironmentVariableUtils.getRequiredVariable(ENV_VAR_NAME);
          });            // Verifying mocks.
    }

    @Test
    public void shouldReturnOptionalVariableWhenExists() {
        // Mocking
    	environmentVariableUtilsMock.when(() -> EnvironmentVariableUtils.getOptionalVariable(ENV_VAR_NAME)).thenReturn(RETURN_VALUE);
        // Mocked behavior
        assertEquals(RETURN_VALUE, EnvironmentVariableUtils.getOptionalVariable(ENV_VAR_NAME));
    }

    @Test
    public void shouldReturnNullWhenOptionalVariableDoesNotExist() {
        // Mocking
    	environmentVariableUtilsMock.when(() -> EnvironmentVariableUtils.getOptionalVariable(ENV_VAR_NAME)).thenReturn(NULL_VALUE);
        // Mocked behavior
        assertEquals(NULL_VALUE, EnvironmentVariableUtils.getOptionalVariable(ENV_VAR_NAME));
    }

}
