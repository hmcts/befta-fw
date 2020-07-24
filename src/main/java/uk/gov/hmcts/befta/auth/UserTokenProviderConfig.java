package uk.gov.hmcts.befta.auth;

import static uk.gov.hmcts.befta.util.EnvironmentVariableUtils.getOptionalVariable;
import static uk.gov.hmcts.befta.util.EnvironmentVariableUtils.getRequiredVariable;

import lombok.Getter;

@Getter
public class UserTokenProviderConfig {

    public static final UserTokenProviderConfig DEFAULT_INSTANCE = new UserTokenProviderConfig();
    private static final String OIDC = "OIDC";
    private static final String OAUTH2 = "OAUTH2";

    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private String accessTokenType;
    private String scopeVariables;

    private UserTokenProviderConfig() {
        clientId = getRequiredVariable("OAUTH2_CLIENT_ID");
        clientSecret = getRequiredVariable("OAUTH2_CLIENT_SECRET");
        redirectUri = getRequiredVariable("OAUTH2_REDIRECT_URI");
        scopeVariables = getOptionalVariable("OAUTH2_SCOPE_VARIABLES");
        accessTokenType = getOptionalVariable("OAUTH2_ACCESS_TOKEN_TYPE");
        if (accessTokenType == null) {
            accessTokenType = OAUTH2;
        }
    }

    private UserTokenProviderConfig(String tokenProviderClientId) {
        clientId = tokenProviderClientId;
        clientSecret = getRequiredVariable("BEFTA_OAUTH2_CLIENT_SECRET_OF_" + tokenProviderClientId.toUpperCase());
        redirectUri = getRequiredVariable("BEFTA_OAUTH2_REDIRECT_URI_OF_" + tokenProviderClientId.toUpperCase());
        scopeVariables = getOptionalVariable("BEFTA_OAUTH2_SCOPE_VARIABLES_OF_" + tokenProviderClientId.toUpperCase());
        accessTokenType = getOptionalVariable(
                "BEFTA_OAUTH2_ACCESS_TOKEN_TYPE_OF_" + tokenProviderClientId.toUpperCase());
        if (accessTokenType == null) {
            accessTokenType = OAUTH2;
        }
    }

    public static UserTokenProviderConfig of(String tokenProviderClientId) {
        if (DEFAULT_INSTANCE.getClientId().equals(tokenProviderClientId))
            return DEFAULT_INSTANCE;
        return new UserTokenProviderConfig(tokenProviderClientId);
    }

    public boolean isForOidc() {
        return accessTokenType.equalsIgnoreCase(OIDC);
    }

    public boolean isForOauth2() {
        return accessTokenType.equalsIgnoreCase(OAUTH2);
    }
}
