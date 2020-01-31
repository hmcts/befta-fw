package uk.gov.hmcts.befta.player;

import io.cucumber.java.Scenario;
import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.QueryableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.SpecificationQuerier;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import uk.gov.hmcts.befta.BeftaMain;
import uk.gov.hmcts.befta.DefaultTestAutomationAdapter;
import uk.gov.hmcts.befta.data.*;
import uk.gov.hmcts.befta.exception.FunctionalTestException;
import uk.gov.hmcts.befta.util.*;

import java.io.IOException;
import java.util.*;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        BackEndFunctionalTestScenarioContext.class,
        JsonStoreHttpTestDataSource.class,
        Scenario.class
})
public class BackEndFunctionalTestScenarioContextTest {

    private BackEndFunctionalTestScenarioContext context;

    @Mock
    private JsonStoreHttpTestDataSource dataSource;

    @Mock
    private Scenario scenario;

    private static final Collection<String> VALID_TAGS = Collections.singletonList("@S-133");

    @Before
    public void setUp() throws Exception {
        whenNew(JsonStoreHttpTestDataSource.class).withAnyArguments().thenReturn(dataSource);
        context = new BackEndFunctionalTestScenarioContext();
    }

    @Test
    public void shouldInitializeTestDataForScenario() {
        when(scenario.getSourceTagNames()).thenReturn(VALID_TAGS);

        context.initializeTestDataFor(scenario);

        verify(dataSource).getDataForTestCall(eq("S-133"));
    }

    @Test
    public void shouldGetCurrentScenarioTagForCorrectPrefixOnly() {
        final Collection<String> tags = new ArrayList<String>(){{
            add("@A-133");
            add("S-987");
            add("@S-133");
            add("@F-103");
        }};
        when(scenario.getSourceTagNames()).thenReturn(tags);
        context.initializeTestDataFor(scenario);

        String result = context.getCurrentScenarioTag();

        assertEquals("S-133", result);
    }

    @Test
    public void shouldGetCurrentScenarioTagWithMultipleScenarios() {
        final Collection<String> tags = new ArrayList<String>(){{
            add("@S-133");
            add("@S-456");
        }};
        when(scenario.getSourceTagNames()).thenReturn(tags);
        context.initializeTestDataFor(scenario);

        String result = context.getCurrentScenarioTag();

        assertEquals("S-133,S-456", result);
    }
}
