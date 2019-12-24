package uk.gov.hmcts.befta.data;

public interface HttpTestDataSource {

    HttpTestData getDataForTestCall(String testDataId);

}
