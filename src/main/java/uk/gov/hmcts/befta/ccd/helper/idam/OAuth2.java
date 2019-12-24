package uk.gov.hmcts.befta.ccd.helper.idam;

import uk.gov.hmcts.befta.util.EnvUtils;

public enum OAuth2 {

    INSTANCE;

    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;

    OAuth2() {
        clientId = EnvUtils.require("OAUTH2_CLIENT_ID");
        clientSecret = EnvUtils.require("OAUTH2_CLIENT_SECRET");
        redirectUri = EnvUtils.require("OAUTH2_REDIRECT_URI");
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
