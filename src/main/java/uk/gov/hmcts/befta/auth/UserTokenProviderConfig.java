package uk.gov.hmcts.befta.auth;

import lombok.Getter;
import uk.gov.hmcts.befta.util.EnvironmentVariableUtils;

@Getter
public class UserTokenProviderConfig {

    public static final UserTokenProviderConfig DEFAULT_INSTANCE = new UserTokenProviderConfig();
    private static final String OIDC = "OIDC";
    private static final String OAUTH2 = "OAUTH2";

    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private String accessTokenType;

    private UserTokenProviderConfig() {
        clientId = EnvironmentVariableUtils.getRequiredVariable("OAUTH2_CLIENT_ID");
        clientSecret = EnvironmentVariableUtils.getRequiredVariable("OAUTH2_CLIENT_SECRET");
        redirectUri = EnvironmentVariableUtils.getRequiredVariable("OAUTH2_REDIRECT_URI");
        accessTokenType = EnvironmentVariableUtils.getOptionalVariable("OAUTH2_ACCESS_TOKEN_TYPE");
        if (accessTokenType == null) {
            accessTokenType = OAUTH2;
        }
    }

    private UserTokenProviderConfig(String tokenProviderClientId) {
        clientId = tokenProviderClientId;
        clientSecret = EnvironmentVariableUtils
                .getRequiredVariable("BEFTA_OAUTH2_CLIENT_SECRET_OF_" + tokenProviderClientId.toUpperCase());
        redirectUri = EnvironmentVariableUtils
                .getRequiredVariable("BEFTA_OAUTH2_REDIRECT_URI_OF_" + tokenProviderClientId.toUpperCase());
        accessTokenType = EnvironmentVariableUtils
                .getRequiredVariable("BEFTA_OAUTH2_ACCESS_TOKEN_TYPE_OF_" + tokenProviderClientId.toUpperCase());
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
