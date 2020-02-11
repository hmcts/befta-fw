package uk.gov.hmcts.befta.util;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest(EnvironmentVariableUtils.class)
public class EnvironmentVariableUtilsTest {

    private static final String ENV_VAR_NAME = "ENV_VAR";
    private static final String RETURN_VALUE = "VALUE";
    private static final String KEY = "[[$ENV_VAR]]";
    private static final String ERROR_MESSAGE = "Environment variable `ENV_VAR` is required";

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void prepareTest() {
        PowerMockito.mockStatic(System.class);
    }

    @Test
    public void shouldResolveVariableWithKeyMatchingPattern() {
        mockGetEnv(ENV_VAR_NAME, RETURN_VALUE);

        final String result = EnvironmentVariableUtils.resolvePossibleVariable(KEY);

        assertEquals(RETURN_VALUE, result);
    }

    @Test
    public void shouldReturnSameValueWithKeyNotMatchingPattern() {
        mockGetEnv(ENV_VAR_NAME, RETURN_VALUE);
        final String invalidKey = "[$ENV_VAR]";

        final String result = EnvironmentVariableUtils.resolvePossibleVariable(invalidKey);

        assertEquals(invalidKey, result);
    }

    @Test
    public void shouldThrowExceptionWhenResolvingKeyWithoutValue() {
        mockGetEnv(ENV_VAR_NAME, null);

        exceptionRule.expect(NullPointerException.class);
        exceptionRule.expectMessage(ERROR_MESSAGE);

        EnvironmentVariableUtils.resolvePossibleVariable(KEY);
    }

    @Test
    public void shouldReturnRequiredVariableWhenExists() {
        mockGetEnv(ENV_VAR_NAME, RETURN_VALUE);

        final String result = EnvironmentVariableUtils.getRequiredVariable(ENV_VAR_NAME);

        assertEquals(RETURN_VALUE, result);
    }

    @Test
    public void shouldThrowExceptionWhenRequiredVariableDoesNotExist() {
        mockGetEnv(ENV_VAR_NAME, null);

        exceptionRule.expect(NullPointerException.class);
        exceptionRule.expectMessage(ERROR_MESSAGE);

        EnvironmentVariableUtils.getRequiredVariable(ENV_VAR_NAME);
    }

    @Test
    public void shouldReturnOptionalVariableWhenExists() {
        mockGetEnv(ENV_VAR_NAME, RETURN_VALUE);

        final String result = EnvironmentVariableUtils.getOptionalVariable(ENV_VAR_NAME);

        assertEquals(RETURN_VALUE, result);
    }

    @Test
    public void shouldReturnNullWhenOptionalVariableDoesNotExist() {
        mockGetEnv(ENV_VAR_NAME, null);

        final String result = EnvironmentVariableUtils.getOptionalVariable(ENV_VAR_NAME);

        assertEquals(null, result);
    }

    private void mockGetEnv(String name, String value) {
        when(System.getenv(name)).thenReturn(value);
    }
}
