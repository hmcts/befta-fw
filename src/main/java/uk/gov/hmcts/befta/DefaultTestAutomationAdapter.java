package uk.gov.hmcts.befta;

import org.springframework.cloud.openfeign.support.SpringMvcContract;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import uk.gov.hmcts.befta.auth.AuthApi;
import uk.gov.hmcts.befta.auth.OAuth2;
import uk.gov.hmcts.befta.data.UserData;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;

public class DefaultTestAutomationAdapter implements TestAutomationAdapter {

    private static final String AUTHORIZATION_CODE = "authorization_code";
    private static final String CODE = "code";
    private static final String BASIC = "Basic ";

    private final AuthApi idamApi;

    private final ServiceAuthTokenGenerator tokenGenerator;

    private final Map<String, UserData> users = new HashMap<>();

    private static boolean isTestDataLoaded = false;

    public DefaultTestAutomationAdapter() {
        final ServiceAuthorisationApi serviceAuthorisationApi = Feign.builder().encoder(new JacksonEncoder())
                .contract(new SpringMvcContract())
                .target(ServiceAuthorisationApi.class, BeftaMain.getConfig().getS2SURL());

        this.tokenGenerator = new ServiceAuthTokenGenerator(BeftaMain.getConfig().getGatewayServiceSecret(),
                BeftaMain.getConfig().getGatewayServiceName(), serviceAuthorisationApi);

        idamApi = Feign.builder().encoder(new JacksonEncoder()).decoder(new JacksonDecoder()).target(AuthApi.class,
                BeftaMain.getConfig().getIdamURL());
    }

    @Override
    public String getNewS2SToken() {
        return tokenGenerator.generate();
    }

    @Override
    public void authenticate(UserData user) {
        UserData cached = users.computeIfAbsent(user.getUsername(), e -> {
            final String accessToken = getIdamOauth2Token(user.getUsername(), user.getPassword());
            final AuthApi.User idamUser = idamApi.getUser(accessToken);
            user.setId(idamUser.getId());
            user.setAccessToken(accessToken);
            return user;
        });

        if (user != cached) {
            user.setId(cached.getId());
            user.setAccessToken(cached.getAccessToken());
        }
    }

    @Override
    public void loadTestDataIfNecessary() {
        if (!isTestDataLoaded) {
            try {
                doLoadTestData();
            } catch (Exception e) {
                throw e;
            } finally {
                isTestDataLoaded = true;
            }
        }
    }

    protected void doLoadTestData() {
    }

    private String getIdamOauth2Token(String username, String password) {
        String authorisation = username + ":" + password;
        String base64Authorisation = Base64.getEncoder().encodeToString(authorisation.getBytes());

        OAuth2 oauth2 = BeftaMain.getConfig().getOauth2Config();
        AuthApi.AuthenticateUserResponse authenticateUserResponse = idamApi
                .authenticateUser(BASIC + base64Authorisation, CODE, oauth2.getClientId(), oauth2.getRedirectUri());

        AuthApi.TokenExchangeResponse tokenExchangeResponse = idamApi.exchangeCode(authenticateUserResponse.getCode(),
                AUTHORIZATION_CODE, oauth2.getClientId(), oauth2.getClientSecret(), oauth2.getRedirectUri());

        return tokenExchangeResponse.getAccessToken();
    }
}