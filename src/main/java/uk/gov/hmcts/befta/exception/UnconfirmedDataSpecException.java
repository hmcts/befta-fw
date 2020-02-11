package uk.gov.hmcts.befta.exception;

public class UnconfirmedDataSpecException extends FunctionalTestException {

    private static final long serialVersionUID = 1L;

    public UnconfirmedDataSpecException(final String unconfirmedSpec) {
        this(unconfirmedSpec, null);
    }

    public UnconfirmedDataSpecException(final String unconfirmedSpec, final Throwable cause) {
        super("Test data does not confirm it meets the specification: '" + unconfirmedSpec + "'", cause);
    }
}
