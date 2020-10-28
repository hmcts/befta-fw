package uk.gov.hmcts.befta.exception;

public class FeatureToggleCheckFailureException extends FunctionalTestException {

    private static final long serialVersionUID = 1L;

    public FeatureToggleCheckFailureException(String message) {
        super(message);
    }

}
