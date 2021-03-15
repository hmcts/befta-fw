package uk.gov.hmcts.befta.factory;

import feign.Feign;
import feign.jackson.JacksonEncoder;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import uk.gov.hmcts.befta.BeftaMain;
import uk.gov.hmcts.reform.ccd.document.am.feign.CaseDocumentClientApi;

public class BeftaCaseDocumentAmApiClientFactory {

    private BeftaCaseDocumentAmApiClientFactory() {
    }

    public static CaseDocumentClientApi createCaseDocumentAmApiClient() {
        return Feign.builder().encoder(new JacksonEncoder()).contract(
                new SpringMvcContract())
                .target(CaseDocumentClientApi.class, BeftaMain.getConfig().getCaseDocsURL());
    }
}
