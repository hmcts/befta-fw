package uk.gov.hmcts.befta.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Collection;

import io.cucumber.java.Scenario;
import uk.gov.hmcts.befta.data.HttpTestData;
import uk.gov.hmcts.befta.data.JsonStoreHttpTestDataSource;

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
    private HttpTestData s103TestData;

    @Mock
    private Scenario scenario;

    private static final String VALID_TAG_ID = "S-133";

    @Before
    public void setUp() throws Exception {
        whenNew(JsonStoreHttpTestDataSource.class).withAnyArguments().thenReturn(dataSource);
        context = new BackEndFunctionalTestScenarioContext();
        when(dataSource.getDataForTestCall(VALID_TAG_ID)).thenReturn(s103TestData);
        when(dataSource.getDataForTestCall("S-133,S-456")).thenReturn(s103TestData);
    }

    @Test
    public void shouldGetCurrentScenarioTagForCorrectPrefixOnly() {
        final Collection<String> tags = new ArrayList<String>() {
            private static final long serialVersionUID = 1L;
            {
                add("@A-133");
                add("S-987");
                add("@S-133");
                add("@F-103");
            }
        };
        when(scenario.getSourceTagNames()).thenReturn(tags);
        context.initializeTestDataFor(scenario);

        String result = context.getCurrentScenarioTag();

        assertEquals(VALID_TAG_ID, result);
    }

    @Test
    public void shouldGetCurrentScenarioTagWithMultipleScenarios() {
        final Collection<String> tags = new ArrayList<String>() {
            private static final long serialVersionUID = 1L;
            {
                add("@S-133");
                add("@S-456");
            }
        };
        when(scenario.getSourceTagNames()).thenReturn(tags);
        context.initializeTestDataFor(scenario);

        String result = context.getCurrentScenarioTag();

        assertEquals("S-133,S-456", result);
    }

    @Test
    public void shouldAddChildContextUsingGuidAsReference() {
        // ARRANGE
        final String childGuid = "CHILD_GUID";
        BackEndFunctionalTestScenarioContext testChildContext
                = Mockito.spy(BackEndFunctionalTestScenarioContext.class);
        when(testChildContext.getGuid()).thenReturn(childGuid);

        // ACT
        context.addChildContext(testChildContext);

        // ASSERT
        assertTrue(context.getChildContexts().containsKey(childGuid));
        assertEquals(testChildContext, context.getChildContexts().get(childGuid));
        verify(testChildContext, times(1)).setReference(childGuid);
    }

    @Test
    public void shouldAddChildContextByReference() {
        // ARRANGE
        final String testReference = "TEST_REFERENCE";
        BackEndFunctionalTestScenarioContext testChildContext
                = Mockito.spy(BackEndFunctionalTestScenarioContext.class);

        // ACT
        context.addChildContextByReference(testReference, testChildContext);

        // ASSERT
        assertTrue(context.getChildContexts().containsKey(testReference));
        assertEquals(testChildContext, context.getChildContexts().get(testReference));
        verify(testChildContext, times(1)).setReference(testReference);
    }

    @Test
    public void shouldReturnBlankGuidIfNoTestData() {
        // ARRANGE
        context = new BackEndFunctionalTestScenarioContext();

        // ACT
        String result = context.getGuid();

        // ASSERT
        assertEquals("", result);
    }

    @Test
    public void shouldGetGuidFromTestData() throws Exception {
        // ARRANGE
        final String testGuid = "TEST_GUID";
        when(s103TestData.get_guid_()).thenReturn(testGuid);
        whenNew(HttpTestData.class).withArguments(ArgumentMatchers.any(HttpTestData.class)).thenReturn(s103TestData);
        context.initializeTestDataFor(VALID_TAG_ID);

        // ACT
        String result = context.getGuid();

        // ASSERT
        assertEquals(testGuid, result);
    }

    @Test
    public void shouldGetReferenceFromGuidIfReferenceNotSet() throws Exception {
        // ARRANGE
        final String testGuid = "TEST_GUID";
        when(s103TestData.get_guid_()).thenReturn(testGuid);
        whenNew(HttpTestData.class).withArguments(ArgumentMatchers.any(HttpTestData.class)).thenReturn(s103TestData);
        context.initializeTestDataFor(VALID_TAG_ID);

        // ACT
        String result = context.getReference();

        // ASSERT
        assertEquals(testGuid, result);
    }

    @Test
    public void shouldGetReferenceIfReferenceIsSet() {
        // ARRANGE
        final String testReference = "TEST_REFERENCE";
        context.setReference(testReference);

        // ACT
        String result = context.getReference();

        // ASSERT
        assertEquals(testReference, result);
    }
}
