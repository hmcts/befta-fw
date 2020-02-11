package uk.gov.hmcts.befta.data;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static uk.gov.hmcts.common.CommonAssertions.*;

public class JsonStoreHttpTestDataSourceTest {

    private static final String[] TEST_DATA_RESOURCE_PACKAGES = { "framework-test-data" };

    private JsonStoreHttpTestDataSource dataSource;

    @Before
    public void setUp() {
        dataSource = new JsonStoreHttpTestDataSource(TEST_DATA_RESOURCE_PACKAGES);
    }
    
    @Test
    public void shouldGetBasicDataForTestCallSuccessfully() {
        HttpTestData result = dataSource.getDataForTestCall("Simple-Data-Without-Inheritance");

        applyCommonAssertionsOnBasicData(result);
    }

    @Test
    public void shouldGetInheritedDataForTestCallAndExtendSuccessfully() {
        HttpTestData result = dataSource.getDataForTestCall("Simple-Data-With-Inheritance");

        applyCommonAssertionsOnExtendedData(result);
    }

    @Test
    public void shouldGetInheritedDataForTestCallAndOverrideSuccessfully() {
        HttpTestData result = dataSource.getDataForTestCall("Simple-Data-With-Overrides");

        applyCommonAssertionsOnOverriddenData(result);
    }

    @Test
    public void shouldReturnNullWhenGettingDataForTestDataIdNotFound() {
        HttpTestData result = dataSource.getDataForTestCall("Non-Existing-Id");

        assertNull(result);
    }
}

