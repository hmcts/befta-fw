/**
 * 
 */
package uk.gov.hmcts.befta.factory;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsInstanceOf;
import org.junit.jupiter.api.Test;

import uk.gov.hmcts.befta.player.BackEndFunctionalTestScenarioContext;

/**
 * @author korneleehenry
 *
 */
class BeftaScenarioContextFactoryTest {

	/**
	 * Test method for {@link uk.gov.hmcts.befta.factory.BeftaScenarioContextFactory#createBeftaScenarioContext()}.
	 */
	@Test
	void testCreateBeftaScenarioContext() {
    	MatcherAssert.assertThat(BeftaScenarioContextFactory.createBeftaScenarioContext(), IsInstanceOf.instanceOf(BackEndFunctionalTestScenarioContext.class));
	}

}
