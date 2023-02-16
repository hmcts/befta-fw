package uk.gov.hmcts.befta.util;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RetryConfiguration {

    private int maxAttempts;
    private String[] statusCodes;
    private int delay;
}
