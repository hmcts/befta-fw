package uk.gov.hmcts.befta.auth;

import lombok.Getter;
import uk.gov.hmcts.befta.util.EnvironmentVariableUtils;

@Getter
public class UserTokenProviderConfig {

    public static final UserTokenProviderConfig DEFAULT_INSTANCE = new UserTokenProviderConfig();
    private static final String OIDC = "OIDC";

    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final String accessTokenType;

    private UserTokenProviderConfig() {
        clientId = EnvironmentVariableUtils.getRequiredVariable("OAUTH2_CLIENT_ID");
        clientSecret = EnvironmentVariableUtils.getRequiredVariable("OAUTH2_CLIENT_SECRET");
        redirectUri = EnvironmentVariableUtils.getRequiredVariable("OAUTH2_REDIRECT_URI");
        accessTokenType = EnvironmentVariableUtils.getOptionalVariable("OAUTH2_ACCESS_TOKEN_TYPE");
    }

    private UserTokenProviderConfig(String oauthClientId) {
        clientId = oauthClientId;
        clientSecret = EnvironmentVariableUtils
                .getRequiredVariable("BEFTA_OAUTH2_CLIENT_SECRET_OF_" + oauthClientId.toUpperCase());
        redirectUri = EnvironmentVariableUtils
                .getRequiredVariable("BEFTA_OAUTH2_REDIRECT_URI_OF_" + oauthClientId.toUpperCase());
        accessTokenType = EnvironmentVariableUtils
                .getRequiredVariable("BEFTA_OAUTH2_ACCESS_TOKEN_TYPE_OF_" + oauthClientId.toUpperCase());
    }

    public static UserTokenProviderConfig of(String oauth2ConfigId) {
        if (DEFAULT_INSTANCE.getClientId().equals(oauth2ConfigId))
            return DEFAULT_INSTANCE;
        return new UserTokenProviderConfig(oauth2ConfigId);
    }

    public boolean isForOidc() {
        return accessTokenType.equalsIgnoreCase(OIDC);
    }
}
