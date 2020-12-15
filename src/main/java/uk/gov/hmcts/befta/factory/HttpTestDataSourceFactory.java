package uk.gov.hmcts.befta.factory;

import java.util.Arrays;

import uk.gov.hmcts.befta.data.HttpTestDataSource;
import uk.gov.hmcts.befta.data.JsonStoreHttpTestDataSource;
import uk.gov.hmcts.befta.util.BeftaUtils;

public class HttpTestDataSourceFactory {

    private HttpTestDataSourceFactory() {
    }

    public static HttpTestDataSource createHttpTestDataSource(String[] resourcePackages) {
        BeftaUtils.defaultLog("Loading test data resources at: " + Arrays.asList(resourcePackages));
        return new JsonStoreHttpTestDataSource(resourcePackages);
    }

}
