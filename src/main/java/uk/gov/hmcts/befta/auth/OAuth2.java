package uk.gov.hmcts.befta.auth;

import uk.gov.hmcts.befta.util.EnvironmentVariableUtils;

public class OAuth2 {

    public static final OAuth2 INSTANCE = new OAuth2();

    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;

    private OAuth2() {
        clientId = EnvironmentVariableUtils.getRequiredVariable("OAUTH2_CLIENT_ID");
        clientSecret = EnvironmentVariableUtils.getRequiredVariable("OAUTH2_CLIENT_SECRET");
        redirectUri = EnvironmentVariableUtils.getRequiredVariable("OAUTH2_REDIRECT_URI");
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }
}
