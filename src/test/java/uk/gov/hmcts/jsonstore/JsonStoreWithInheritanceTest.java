package uk.gov.hmcts.jsonstore;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import uk.gov.hmcts.befta.data.HttpTestData;
import uk.gov.hmcts.befta.data.HttpTestDataSource;
import uk.gov.hmcts.befta.data.JsonStoreHttpTestDataSource;
import uk.gov.hmcts.befta.util.MapVerificationResult;
import uk.gov.hmcts.befta.util.MapVerifier;


public class JsonStoreWithInheritanceTest {

    private static final String[] TEST_DATA_RESOURCE_PACKAGES = { "framework-test-data/features" };
    private static final HttpTestDataSource TEST_DATA_RESOURCE = new JsonStoreHttpTestDataSource(
            TEST_DATA_RESOURCE_PACKAGES);

    private static final String[] TEST_DATA_RESOURCE_PACKAGES_WITH_DUPLICATE_GUIDS = { "framework-test-data/features-with-duplicate-guids" };
    private static final HttpTestDataSource TEST_DATA_RESOURCE_WITH_DUPLICATE_GUIDS = new JsonStoreHttpTestDataSource(
            TEST_DATA_RESOURCE_PACKAGES_WITH_DUPLICATE_GUIDS);

    @Test(expected = RuntimeException.class)
    public void shouldHaveOnlyUniqueGUID() {
        TEST_DATA_RESOURCE_WITH_DUPLICATE_GUIDS.getDataForTestCall("F-050_Test_Data_Base");
    }

    @Test
    public void shouldHaveTheSameDataInBaseRequestAndExtensionResponseBodies() {
        HttpTestData base = TEST_DATA_RESOURCE.getDataForTestCall("F-050_Test_Data_Base");
        HttpTestData extension = TEST_DATA_RESOURCE.getDataForTestCall("S-301");

        @SuppressWarnings("unchecked")
        MapVerificationResult result = new MapVerifier("", 5).verifyMap(
                (Map<String, Object>) base.getRequest().getBody().get("data"),
                (Map<String, Object>) extension.getExpectedResponse().getBody().get("data"));

        Assert.assertArrayEquals(new Object[] { "an active profile in CCD" }, base.getSpecs().toArray());
        Assert.assertArrayEquals(new Object[] { "an active profile in CCD", "has the 200 return code" },
                extension.getSpecs().toArray());

        Assert.assertTrue(result.isVerified());
    }

    @Test
    public void shouldHaveTheSameDataInExtensionRequestAndExtensionResponseBodies() {
        HttpTestData extension = TEST_DATA_RESOURCE.getDataForTestCall("S-301");

        @SuppressWarnings("unchecked")
        MapVerificationResult result = new MapVerifier("", 5).verifyMap(
                (Map<String, Object>) extension.getRequest().getBody().get("data"),
                (Map<String, Object>) extension.getExpectedResponse().getBody().get("data"));

        Assert.assertTrue(result.isVerified());
    }
}
