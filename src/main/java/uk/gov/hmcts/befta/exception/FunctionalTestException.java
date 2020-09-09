package uk.gov.hmcts.befta.exception;

public class FunctionalTestException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public FunctionalTestException(final String message) {
        super(message);
    }

    public FunctionalTestException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
