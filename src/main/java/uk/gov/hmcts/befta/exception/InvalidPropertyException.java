package uk.gov.hmcts.befta.exception;

import java.io.IOException;

public class InvalidPropertyException extends IOException {

    /**
     * Constructs a {@code InvalidPropertyException} with no detail message.
     */
    public InvalidPropertyException() {
    }

    /**
     * Constructs a {@code InvalidPropertyException} with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidPropertyException(String msg) {
        super(msg);
    }
}
