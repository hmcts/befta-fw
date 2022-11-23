package uk.gov.hmcts.befta.exception;

public class ImportException extends RuntimeException {
    private final int httpStatusCode;

    public ImportException(String message, int httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }

    public int getHttpStatusCode() {
        return this.httpStatusCode;
    }
}
