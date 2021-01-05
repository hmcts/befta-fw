/**
 * 
 */
package uk.gov.hmcts.befta.factory;

import uk.gov.hmcts.befta.TestAutomationAdapter;
import uk.gov.hmcts.befta.data.HttpTestData;
import uk.gov.hmcts.befta.player.BackEndFunctionalTestScenarioContext;
import uk.gov.hmcts.befta.util.DynamicValueInjector;

/**
 * @author korneleehenry
 *
 */
public class DynamicValueInjectorFactory {
	
	private DynamicValueInjectorFactory() {}
	
	public static DynamicValueInjector create(TestAutomationAdapter taAdapter, HttpTestData testData,
            BackEndFunctionalTestScenarioContext scenarioContext) {
		
		return new DynamicValueInjector(taAdapter, testData, scenarioContext);
	}

}
