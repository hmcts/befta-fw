package uk.gov.hmcts.befta;

import org.apache.commons.lang3.math.NumberUtils;

public class AuthenticationRetryConfiguration {
    private int retryAttempts;
    private int retryMaxTimeInSeconds;
    private int retryMultiplierInMilliseconds;

    public AuthenticationRetryConfiguration(String retryAttempts,
                                            String retryMaxTimeInSeconds,
                                            String retryMultiplierInMilliseconds) {
        this.retryAttempts = NumberUtils.toInt(retryAttempts);
        this.retryMaxTimeInSeconds = NumberUtils.toInt(retryMaxTimeInSeconds);
        this.retryMultiplierInMilliseconds = NumberUtils.toInt(retryMultiplierInMilliseconds);
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
