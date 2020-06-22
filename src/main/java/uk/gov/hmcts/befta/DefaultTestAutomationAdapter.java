package uk.gov.hmcts.befta;

import org.springframework.cloud.openfeign.support.SpringMvcContract;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import uk.gov.hmcts.befta.auth.AuthApi;
import uk.gov.hmcts.befta.auth.OAuth2Config;
import uk.gov.hmcts.befta.data.UserData;
import uk.gov.hmcts.befta.exception.FunctionalTestException;
import uk.gov.hmcts.befta.player.BackEndFunctionalTestScenarioContext;
import uk.gov.hmcts.befta.util.EnvironmentVariableUtils;
import uk.gov.hmcts.befta.util.ReflectionUtils;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;

public class DefaultTestAutomationAdapter implements TestAutomationAdapter {

    private static final String AUTHORIZATION_CODE = "authorization_code";
    private static final String CODE = "code";
    private static final String BASIC = "Basic ";

    private final AuthApi idamApi;

    private final Map<String, ServiceAuthTokenGenerator> tokenGenerators = new ConcurrentHashMap<>();

    private final Map<String, UserData> users = new HashMap<>();

    private static boolean isTestDataLoaded = false;

    public DefaultTestAutomationAdapter() {
        idamApi = Feign.builder().encoder(new JacksonEncoder()).decoder(new JacksonDecoder()).target(AuthApi.class,
                BeftaMain.getConfig().getIdamURL());
    }

    @Override
    public String getNewS2SToken() {
        return getNewS2SToken(BeftaMain.getConfig().getS2SClientId());
    }

    @Override
    public String getNewS2SToken(String clientId) {
        return tokenGenerators.computeIfAbsent(clientId, key -> {
            return getNewS2sClient(clientId);
        }).generate();
    }

    @Override
    public void authenticate(UserData user, String oauth2ConfigId) {
        UserData cached = users.computeIfAbsent(user.getUsername(), e -> {
            final String accessToken = getIdamOauth2Token(user.getUsername(), user.getPassword(),
                    OAuth2Config.of(oauth2ConfigId));
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

    protected ServiceAuthTokenGenerator getNewS2sClient(String s2sClientId) {
        String clientSecret = EnvironmentVariableUtils
                .getRequiredVariable("BEFTA_S2S_CLIENT_SECRET_OF_" + s2sClientId.toUpperCase());
        return getNewS2sClientWithCredentials(s2sClientId, clientSecret);
    }

    protected ServiceAuthTokenGenerator getNewS2sClientWithCredentials(String clientId, String clientSecret) {
        final ServiceAuthorisationApi serviceAuthorisationApi = Feign.builder().encoder(new JacksonEncoder())
                .contract(new SpringMvcContract())
                .target(ServiceAuthorisationApi.class, BeftaMain.getConfig().getS2SURL());

        return new ServiceAuthTokenGenerator(clientSecret, clientId, serviceAuthorisationApi);
    }

    private String getIdamOauth2Token(String username, String password, OAuth2Config oauth2Config) {
        String authorisation = username + ":" + password;
        String base64Authorisation = Base64.getEncoder().encodeToString(authorisation.getBytes());

        AuthApi.AuthenticateUserResponse authenticateUserResponse = idamApi.authenticateUser(
                BASIC + base64Authorisation, CODE, oauth2Config.getClientId(), oauth2Config.getRedirectUri());

        AuthApi.TokenExchangeResponse tokenExchangeResponse = idamApi.exchangeCode(authenticateUserResponse.getCode(),
                AUTHORIZATION_CODE, oauth2Config.getClientId(), oauth2Config.getClientSecret(),
                oauth2Config.getRedirectUri());

        return tokenExchangeResponse.getAccessToken();
    }

    @Override
    public Object calculateCustomValue(BackEndFunctionalTestScenarioContext scenarioContext, Object key) {
        if (key == null)
            return null;
        if (key instanceof String) {
            String keyString = ((String) key).toLowerCase().replaceAll(" ", "").replaceAll("-", "").replaceAll("_", "");
            switch (keyString) {
            case "request":
                return scenarioContext.getTestData().getRequest();

            case "requestbody":
                return scenarioContext.getTestData().getRequest().getBody();

            case "requestheaders":
                return scenarioContext.getTestData().getRequest().getHeaders();

            case "requestpathvars":
                return scenarioContext.getTestData().getRequest().getPathVariables();

            case "requestqueryparams":
                return scenarioContext.getTestData().getRequest().getQueryParams();

            case "expectedresponse":
                return scenarioContext.getTestData().getExpectedResponse();

            case "expectedresponseheaders":
                return scenarioContext.getTestData().getExpectedResponse().getHeaders();

            case "expectedresponsebody":
                return scenarioContext.getTestData().getExpectedResponse().getBody();

            case "actualresponse":
                return scenarioContext.getTestData().getActualResponse();

            case "actualresponseheaders":
                return scenarioContext.getTestData().getActualResponse().getHeaders();

            case "actualresponsebody":
                return scenarioContext.getTestData().getActualResponse().getBody();
            case "tokenvaluefromaccompanyingtokencall":
                try {
                    String accompanyingTokenCreationDataId = scenarioContext.getTestData().get_guid_()
                            + "_Token_Creation";
                    return ReflectionUtils.deepGetFieldInObject(scenarioContext, "scenarioContext.siblingContexts."
                            + accompanyingTokenCreationDataId + ".testData.actualResponse.body.token");

                } catch (Exception e) {
                    throw new FunctionalTestException("Failed to get custom value", e);
                }
            }
            String dateTimeFormat = getDateTimeFormatRequested((String) key);
            if (dateTimeFormat != null)
                return LocalDate.now().format(DateTimeFormatter.ofPattern(dateTimeFormat));
        }
        return null;
    }

    protected String getDateTimeFormatRequested(String key) {
        if (key.equals("today"))
            return "yyyy-MM-dd";
        else if (key.equals("now"))
            return "yyyy-MM-dd'T'HH:mm:ss.SSS";
        else if (key.startsWith("now("))
            return key.substring(4, key.length() - 1);
        return null;
    }
}
