package uk.gov.hmcts.jsonstore;

import org.junit.Assert;
import org.junit.Test;
import uk.gov.hmcts.befta.data.HttpTestData;
import uk.gov.hmcts.befta.data.HttpTestDataSource;
import uk.gov.hmcts.befta.data.JsonStoreHttpTestDataSource;
import uk.gov.hmcts.befta.exception.InvalidTestDataException;
import uk.gov.hmcts.befta.util.MapVerificationResult;
import uk.gov.hmcts.befta.util.MapVerifier;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class JsonStoreWithInheritanceTest {

    private static final String[] TEST_DATA_RESOURCE_PACKAGES = {"framework-test-data/features"};
    private static final HttpTestDataSource TEST_DATA_RESOURCE = new JsonStoreHttpTestDataSource(
            TEST_DATA_RESOURCE_PACKAGES);

    private static final String[] TEST_DATA_RESOURCE_PACKAGES_WITH_DUPLICATE_GUIDS = {"framework-test-data-duplicate-guids/features-with-duplicate-guids"};
    private static final HttpTestDataSource TEST_DATA_RESOURCE_WITH_DUPLICATE_GUIDS = new JsonStoreHttpTestDataSource(
            TEST_DATA_RESOURCE_PACKAGES_WITH_DUPLICATE_GUIDS);

    @Test
    public void shouldHaveOnlyUniqueGUID() {
        final RuntimeException exception = assertThrows(RuntimeException.class, () ->
                TEST_DATA_RESOURCE_WITH_DUPLICATE_GUIDS.getDataForTestCall("F-050_Test_Data_Base"));
        assertThat(exception.getMessage(), is("uk.gov.hmcts.befta.exception.InvalidTestDataException: Object with _guid_=F-050_Test_Data_Base already exists"));
        assertThat(exception.getCause(), is(instanceOf(InvalidTestDataException.class)));
    }

    @Test
    public void shouldHaveTheSameDataInBaseRequestAndExtensionResponseBodies() {
        HttpTestData base = TEST_DATA_RESOURCE.getDataForTestCall("F-050_Test_Data_Base");
        HttpTestData extension = TEST_DATA_RESOURCE.getDataForTestCall("S-301");

        @SuppressWarnings("unchecked")
        MapVerificationResult result = new MapVerifier("", 5).verifyMap(
                (Map<String, Object>) base.getRequest().getBody().get("data"),
                (Map<String, Object>) extension.getExpectedResponse().getBody().get("data"));

        Assert.assertArrayEquals(new Object[]{"an active profile in CCD"}, base.getSpecs().toArray());
        Assert.assertArrayEquals(new Object[]{"an active profile in CCD", "has the 200 return code"},
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
