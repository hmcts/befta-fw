/**
 * 
 */
package uk.gov.hmcts.befta.factory;

import uk.gov.hmcts.befta.TestAutomationAdapter;
import uk.gov.hmcts.befta.data.HttpTestData;
import uk.gov.hmcts.befta.player.BackEndFunctionalTestScenarioContext;
import uk.gov.hmcts.befta.util.DynamicValueInjector;

/**
 *  * Factory class for creating instances of DynamicValueInjector.
 *  * This class provides a method to create a DynamicValueInjector
 *  * using the provided TestAutomationAdapter, HttpTestData, and
 *  * BackEndFunctionalTestScenarioContext.
 *  *
 *  * This factory ensures that the creation of DynamicValueInjector
 *  * instances is centralized and consistent throughout the application.
 *  *
 *  * Usage:
 *  * DynamicValueInjector injector = DynamicValueInjectorFactory.create(taAdapter, testData, scenarioContext);
 *
 * Author: korneleehenry
 *
 */
public class DynamicValueInjectorFactory {
	
	private DynamicValueInjectorFactory() {}
	
	public static DynamicValueInjector create(TestAutomationAdapter taAdapter, HttpTestData testData,
            BackEndFunctionalTestScenarioContext scenarioContext) {
		
		return new DynamicValueInjector(taAdapter, testData, scenarioContext);
	}

}
