package uk.gov.hmcts.befta.exception;

public class JsonStoreCreationException extends FunctionalTestException {

    private static final long serialVersionUID = 1L;

    public JsonStoreCreationException(final String message) {
        super(message);
    }

    public JsonStoreCreationException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
