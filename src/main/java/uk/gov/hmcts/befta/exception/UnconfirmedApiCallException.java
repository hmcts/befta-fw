package uk.gov.hmcts.befta.exception;

public class UnconfirmedApiCallException extends FunctionalTestException {

    private static final long serialVersionUID = 1L;

    public UnconfirmedApiCallException(final String productName, final String operationName) {
        this(productName, operationName, null);
    }

    public UnconfirmedApiCallException(final String productName, final String operationName, final Throwable cause) {
        super("Test data does not confirm it is calling the following operation of a product: " + operationName + " -> "
                + productName, cause);
    }
}
