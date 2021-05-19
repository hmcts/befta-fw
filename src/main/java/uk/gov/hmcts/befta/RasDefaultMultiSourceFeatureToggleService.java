package uk.gov.hmcts.befta;

import uk.gov.hmcts.befta.featuretoggle.FeatureToggleService;
import uk.gov.hmcts.befta.util.RasFeatureToggleService;

public class RasDefaultMultiSourceFeatureToggleService extends DefaultMultiSourceFeatureToggleService {
//Move this class to RAS code.
    @Override
    protected FeatureToggleService getToggleServiceFor(String toggleDomain) {
        if (toggleDomain.equalsIgnoreCase("RAS") || toggleDomain.equalsIgnoreCase("customName")) {
            return new RasFeatureToggleService();
        } else {
            return super.getToggleServiceFor(toggleDomain);
        }
    }
}
