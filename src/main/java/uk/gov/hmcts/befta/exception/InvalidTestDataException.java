package uk.gov.hmcts.befta.exception;

public class InvalidTestDataException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidTestDataException(final String message) {
        super(message);
    }

    public InvalidTestDataException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
