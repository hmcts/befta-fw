package uk.gov.hmcts.befta.exception;

public class DefinitionTransformerException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DefinitionTransformerException(final String message) {
        super(message);
    }

    public DefinitionTransformerException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
