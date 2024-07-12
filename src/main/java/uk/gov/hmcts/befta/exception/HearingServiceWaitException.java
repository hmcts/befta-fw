package uk.gov.hmcts.befta.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class HearingServiceWaitException extends RuntimeException{

    public HearingServiceWaitException(final String message) {
        super(message);
    }
}
