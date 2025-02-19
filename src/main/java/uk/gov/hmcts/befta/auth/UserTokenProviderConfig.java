package uk.gov.hmcts.befta.auth;

import java.util.Objects;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.befta.util.EnvironmentVariableUtils;

@Slf4j
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
        clientId = EnvironmentVariableUtils.getRequiredVariable("OAUTH2_CLIENT_ID", "CCD_API_GATEWAY_OAUTH2_CLIENT_ID");
        clientSecret = EnvironmentVariableUtils.getRequiredVariable("OAUTH2_CLIENT_SECRET",
                "CCD_API_GATEWAY_OAUTH2_CLIENT_SECRET");
        redirectUri = EnvironmentVariableUtils.getRequiredVariable("OAUTH2_REDIRECT_URI",
                "CCD_API_GATEWAY_OAUTH2_REDIRECT_URL");
        scopeVariables = EnvironmentVariableUtils.getOptionalVariable("OAUTH2_SCOPE_VARIABLES");
        accessTokenType = EnvironmentVariableUtils.getOptionalVariable("OAUTH2_ACCESS_TOKEN_TYPE");
        if (accessTokenType == null) {
            accessTokenType = OAUTH2;
        }
    }

    private UserTokenProviderConfig(String tokenProviderClientId) {
        clientId = tokenProviderClientId;
        clientSecret = EnvironmentVariableUtils.getRequiredVariable("BEFTA_OAUTH2_CLIENT_SECRET_OF_" + tokenProviderClientId.toUpperCase());
        redirectUri = EnvironmentVariableUtils.getRequiredVariable("BEFTA_OAUTH2_REDIRECT_URI_OF_" + tokenProviderClientId.toUpperCase());
        scopeVariables = EnvironmentVariableUtils.getOptionalVariable("BEFTA_OAUTH2_SCOPE_VARIABLES_OF_" + tokenProviderClientId.toUpperCase());
        accessTokenType = EnvironmentVariableUtils.getOptionalVariable(
                "BEFTA_OAUTH2_ACCESS_TOKEN_TYPE_OF_" + tokenProviderClientId.toUpperCase());
        if (accessTokenType == null) {
            accessTokenType = OAUTH2;
        }
    }

    public static UserTokenProviderConfig of(String tokenProviderClientId) {
    	Objects.requireNonNull(tokenProviderClientId);
        if (tokenProviderClientId.equals(DEFAULT_INSTANCE.getClientId()))
            return DEFAULT_INSTANCE;
//        return new UserTokenProviderConfig(tokenProviderClientId);

        UserTokenProviderConfig userTokenProvider = new UserTokenProviderConfig(tokenProviderClientId);

        log.info("Client id {}", userTokenProvider.clientId);
        log.info("Token Type {}", userTokenProvider.accessTokenType);
        log.info("Redirect URI {}", userTokenProvider.redirectUri);
        log.info("Scope variables {}", userTokenProvider.scopeVariables);

        return userTokenProvider;
    }

    public boolean isForOidc() {
        return accessTokenType.equalsIgnoreCase(OIDC);
    }

    public boolean isForOauth2() {
        return accessTokenType.equalsIgnoreCase(OAUTH2);
    }
}
