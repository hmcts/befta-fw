package uk.gov.hmcts.befta.data;

import static org.junit.Assert.assertNull;
import static uk.gov.hmcts.common.CommonAssertions.applyCommonAssertionsOnBasicData;
import static uk.gov.hmcts.common.CommonAssertions.applyCommonAssertionsOnExtendedData;
import static uk.gov.hmcts.common.CommonAssertions.applyCommonAssertionsOnOverriddenData;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JsonStoreHttpTestDataSourceTest {

    private static final String[] TEST_DATA_RESOURCE_PACKAGES = { "framework-test-data" };

    private JsonStoreHttpTestDataSource dataSource;

    @BeforeEach
    public void setUp() {
        dataSource = new JsonStoreHttpTestDataSource(TEST_DATA_RESOURCE_PACKAGES);
    }
    

    @AfterEach
    public void clearUp() {
    	dataSource = null;
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

