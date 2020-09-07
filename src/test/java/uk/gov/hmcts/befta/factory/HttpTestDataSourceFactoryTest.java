/**
 * 
 */
package uk.gov.hmcts.befta.factory;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsInstanceOf;
import org.junit.jupiter.api.Test;

import uk.gov.hmcts.befta.data.HttpTestDataSource;
import uk.gov.hmcts.befta.data.JsonStoreHttpTestDataSource;

/**
 * @author korneleehenry
 *
 */
class HttpTestDataSourceFactoryTest {

    private static final String RESOURCE_INHERITANCE = "framework-test-data/json-store-test-data";

	/**
	 * Test method for {@link uk.gov.hmcts.befta.factory.HttpTestDataSourceFactory#createHttpTestDataSource(java.lang.String[])}.
	 */
	@Test
	void testCreateHttpTestDataSource() {
		String[] resourcePaths = { RESOURCE_INHERITANCE };
		HttpTestDataSource actual = HttpTestDataSourceFactory.createHttpTestDataSource(resourcePaths);
		MatcherAssert.assertThat(actual, IsInstanceOf.instanceOf(JsonStoreHttpTestDataSource.class));
	}

}
