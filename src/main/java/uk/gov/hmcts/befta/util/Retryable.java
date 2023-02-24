package uk.gov.hmcts.befta.util;

import lombok.Builder;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
public class Retryable {

    public static Retryable N0_RETRYABLE = Retryable.builder().build();

    @Builder.Default
    private int maxAttempts = 1;
    @Builder.Default
    private Set<Integer> statusCodes = new HashSet<>();
    @Builder.Default
    private int delay = 0;
}
