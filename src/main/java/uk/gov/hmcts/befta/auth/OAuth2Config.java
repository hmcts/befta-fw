package uk.gov.hmcts.befta.auth;

import lombok.Getter;
import uk.gov.hmcts.befta.util.EnvironmentVariableUtils;

@Getter
public class OAuth2Config {

    public static final OAuth2Config DEFAULT_INSTANCE = new OAuth2Config();

    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final String accessTokenType;

    private OAuth2Config() {
        clientId = EnvironmentVariableUtils.getRequiredVariable("OAUTH2_CLIENT_ID");
        clientSecret = EnvironmentVariableUtils.getRequiredVariable("OAUTH2_CLIENT_SECRET");
        redirectUri = EnvironmentVariableUtils.getRequiredVariable("OAUTH2_REDIRECT_URI");
        accessTokenType = EnvironmentVariableUtils.getOptionalVariable("OAUTH2_ACCESS_TOKEN_TYPE");
    }

    private OAuth2Config(String oauthClientId) {
        clientId = oauthClientId;
        clientSecret = EnvironmentVariableUtils
                .getRequiredVariable("BEFTA_OAUTH2_CLIENT_SECRET_OF_" + oauthClientId.toUpperCase());
        redirectUri = EnvironmentVariableUtils
                .getRequiredVariable("BEFTA_OAUTH2_REDIRECT_URI_OF_" + oauthClientId.toUpperCase());
        accessTokenType = EnvironmentVariableUtils
                .getRequiredVariable("BEFTA_OAUTH2_ACCESS_TOKEN_TYPE_OF_" + oauthClientId.toUpperCase());
    }

    public static OAuth2Config of(String oauth2ConfigId) {
        if (DEFAULT_INSTANCE.getClientId().equals(oauth2ConfigId))
            return DEFAULT_INSTANCE;
        return new OAuth2Config(oauth2ConfigId);
    }
}
