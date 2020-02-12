package uk.gov.hmcts.befta.exception;

public class InvalidTestDataException extends FunctionalTestException {

    private static final long serialVersionUID = 1L;

    public InvalidTestDataException(final String message) {
        super(message);
    }

    public InvalidTestDataException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
