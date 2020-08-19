package uk.gov.hmcts.befta.exception;

public class ParentNotFoundException extends FunctionalTestException {

    private static final long serialVersionUID = 1L;

    public ParentNotFoundException(final String message) {
        super(message);
    }

    public ParentNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
