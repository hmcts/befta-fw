/**
 * 
 */
package uk.gov.hmcts.befta.factory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import uk.gov.hmcts.befta.TestAutomationAdapter;
import uk.gov.hmcts.befta.data.HttpTestData;
import uk.gov.hmcts.befta.player.BackEndFunctionalTestScenarioContext;
import uk.gov.hmcts.befta.util.DynamicValueInjector;


/**
 * @author korneleehenry
 *
 */
class DynamicValueInjectorFactoryTest {
	
	/**
	 * Test method for {@link uk.gov.hmcts.befta.factory.DynamicValueInjectorFactory#create(uk.gov.hmcts.befta.TestAutomationAdapter, uk.gov.hmcts.befta.data.HttpTestData, uk.gov.hmcts.befta.player.BackEndFunctionalTestScenarioContext)}.
	 */
	@Test
	void testCreateDynamicValueInjector() {
		BackEndFunctionalTestScenarioContext scenarioContext = mock(BackEndFunctionalTestScenarioContext.class);
		TestAutomationAdapter taAdapter = mock(TestAutomationAdapter.class);
		HttpTestData testData = mock(HttpTestData.class);
		DynamicValueInjector actual = DynamicValueInjectorFactory.create(taAdapter, testData, scenarioContext);
		assertNotNull(actual);
	}

}
