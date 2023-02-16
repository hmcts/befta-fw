package uk.gov.hmcts.befta.util;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RetryConfiguration {

    @Builder.Default
    private int maxAttempts = 1;
    @Builder.Default
    private String[] statusCodes = new String[0];
    @Builder.Default
    private int delay = 0;
}
