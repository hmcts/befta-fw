package uk.gov.hmcts.befta;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import uk.gov.hmcts.befta.auth.AuthApi;
import uk.gov.hmcts.befta.auth.UserTokenProviderConfig;
import uk.gov.hmcts.befta.data.UserData;
import uk.gov.hmcts.befta.exception.FunctionalTestException;
import uk.gov.hmcts.befta.factory.BeftaIdamApiClientFactory;
import uk.gov.hmcts.befta.factory.BeftaServiceAuthorisationApiClientFactory;
import uk.gov.hmcts.befta.player.BackEndFunctionalTestScenarioContext;
import uk.gov.hmcts.befta.util.BeftaUtils;
import uk.gov.hmcts.befta.util.EnvironmentVariableUtils;
import uk.gov.hmcts.befta.util.ReflectionUtils;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;

public class DefaultTestAutomationAdapter implements TestAutomationAdapter {

    private static final String AUTHORIZATION_CODE = "authorization_code";
    private static final String CODE = "code";
    private static final String BASIC = "Basic ";
    private static final String PASSWORD = "password";

    private final AuthApi idamApi;
    private final ServiceAuthorisationApi serviceAuthorisationApi;

    private final Map<String, String> s2sTokens = new ConcurrentHashMap<>();

    private final Map<String, UserData> users = new HashMap<>();

    private boolean isTestDataLoaded = false;

    public DefaultTestAutomationAdapter() {
        serviceAuthorisationApi = BeftaServiceAuthorisationApiClientFactory.createServiceAuthorisationApiClient();
        idamApi = BeftaIdamApiClientFactory.createAuthorizationClient();
    }

    @Override
    public String getNewS2SToken() {
        return getNewS2SToken(BeftaMain.getConfig().getDefaultS2sClientId());
    }

    @Override
    public synchronized String getNewS2SToken(String clientId) {
        return s2sTokens.computeIfAbsent(clientId, key -> {
            BeftaUtils.log("Getting a new S2S token for " + clientId);
            return getNewS2sClient(clientId).generate();
        });
    }

    // S2S_AUTH_RETRY_INTERVALS=1000
    // USER_AUTH_RETRY_INTERVALS=1000,2000,4000

    @Override
    public synchronized void authenticateIfNecessary(UserData user, String userTokenClientId) {
        UserData cached = users.computeIfAbsent(user.getUsername(), e -> {
            BeftaUtils.log("Authenticating " + user.getUsername());
            final String accessToken = getUserAccessToken(user.getUsername(), user
                            .getPassword(),
                    UserTokenProviderConfig.of(userTokenClientId));
            BeftaUtils.log("Authenticated " + user.getUsername());
            final AuthApi.User idamUser = idamApi.getUser(accessToken);
            BeftaUtils.log("Fetched user info for " + user.getUsername());
            user.setId(idamUser.getId());
            user.setAccessToken(accessToken);
            return user;
        });
        user.setId(cached.getId());
        user.setAccessToken(cached.getAccessToken());
    }


    @Override
    public synchronized void loadTestDataIfNecessary() {
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
        String enVarName = "BEFTA_S2S_CLIENT_SECRET";

        if (!s2sClientId.equals(BeftaMain.getConfig().getDefaultS2sClientId())) {
            enVarName += "_" + s2sClientId.toUpperCase();
        }

        String clientSecret = EnvironmentVariableUtils.getRequiredVariable(enVarName);
        return getNewS2sClientWithCredentials(s2sClientId, clientSecret);
    }

    protected ServiceAuthTokenGenerator getNewS2sClientWithCredentials(String clientId, String clientSecret) {
        return new ServiceAuthTokenGenerator(clientSecret, clientId, serviceAuthorisationApi);
    }

    private String getUserAccessToken(String username, String password, UserTokenProviderConfig tokenProviderConfig) {
        if (tokenProviderConfig.isForOidc()) {
            return getIdamOidcToken(username, password, tokenProviderConfig);
        } else {
            return getIdamOauth2Token(username, password, tokenProviderConfig);
        }
    }

    private String getIdamOauth2Token(String username, String password, UserTokenProviderConfig tokenProviderConfig) {
        String authorisation = username + ":" + password;
        String base64Authorisation = Base64.getEncoder().encodeToString(authorisation.getBytes());

        AuthApi.AuthenticateUserResponse authenticateUserResponse = idamApi.authenticateUser(
                BASIC + base64Authorisation, CODE, tokenProviderConfig.getClientId(),
                tokenProviderConfig.getRedirectUri());

        AuthApi.TokenExchangeResponse tokenExchangeResponse = idamApi.exchangeCode(authenticateUserResponse.getCode(),
                AUTHORIZATION_CODE, tokenProviderConfig.getClientId(), tokenProviderConfig.getClientSecret(),
                tokenProviderConfig.getRedirectUri());

        return tokenExchangeResponse.getAccessToken();
    }

    private String getIdamOidcToken(String username, String password, UserTokenProviderConfig tokenProviderConfig) {

        AuthApi.TokenExchangeResponse generateOIDCToken = idamApi.generateOIDCToken(tokenProviderConfig.getClientId(),
                tokenProviderConfig.getClientSecret(), PASSWORD, tokenProviderConfig.getScopeVariables(), username, password);

        return generateOIDCToken.getAccessToken();
    }

    @Override
    public synchronized Object calculateCustomValue(BackEndFunctionalTestScenarioContext scenarioContext, Object key) {
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

    public synchronized boolean isTestDataLoaded() {
        return this.isTestDataLoaded;
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
