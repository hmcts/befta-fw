package uk.gov.hmcts.befta.launchdarkly;

import com.launchdarkly.sdk.server.LDClient;
import uk.gov.hmcts.befta.util.EnvironmentVariableUtils;

public class LaunchDarklyConfig {

    public static final String LD_SDK_KEY = "LD_SDK_KEY";

    private LaunchDarklyConfig() { }

    private static LDClient ldClient;

    public static LDClient getLdInstance() {
        if (ldClient == null) {
            //synchronized block to remove overhead
            synchronized (LaunchDarklyConfig.class) {
                if (ldClient == null && System.getenv(LD_SDK_KEY) != null) {
                    ldClient = new LDClient(System.getenv(LD_SDK_KEY));
                }
            }
        }
        return ldClient;
    }

    public static String getEnvironmentName() {
       return EnvironmentVariableUtils.getRequiredVariable("LAUNCH_DARKLY_ENV");
    }

    public static String getLDMicroserviceName() {
        return EnvironmentVariableUtils.getRequiredVariable("MICROSERVICE_NAME");
    }
}
