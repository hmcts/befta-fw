package uk.gov.hmcts.befta;

import org.apache.commons.lang3.math.NumberUtils;

public class AuthenticationRetryConfiguration {
    private int retryAttempts;
    private int retryMaxTimeInSeconds;
    private int retryMultiplierInMilliseconds;

    public AuthenticationRetryConfiguration(int retryAttempts,
                                            int retryMaxTimeInSeconds,
                                            int retryMultiplierInMilliseconds) {
        this.retryAttempts =retryAttempts;
        this.retryMaxTimeInSeconds = retryMaxTimeInSeconds;
        this.retryMultiplierInMilliseconds = retryMultiplierInMilliseconds;
    }

    public int getRetryAttempts() {
        return retryAttempts;
    }

    public int getRetryMaxTimeInSeconds() {
        return retryMaxTimeInSeconds;
    }

    public int getRetryMultiplierTimeinMilliseconds() {
        return retryMultiplierInMilliseconds;
    }

    public boolean isRetryDisabled() {
        return retryAttempts == 0
                && retryMaxTimeInSeconds == 0
                && retryMultiplierInMilliseconds == 0;
    }
}
