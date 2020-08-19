package uk.gov.hmcts.befta.factory;

import uk.gov.hmcts.befta.data.HttpTestDataSource;
import uk.gov.hmcts.befta.data.JsonStoreHttpTestDataSource;

public class HttpTestDataSourceFactory {
	private HttpTestDataSourceFactory(){}

    public static HttpTestDataSource createHttpTestDataSource(String[] resourcePackages) {
        return new JsonStoreHttpTestDataSource(resourcePackages);
	}

}
