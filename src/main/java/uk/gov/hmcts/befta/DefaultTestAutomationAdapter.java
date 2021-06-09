package uk.gov.hmcts.befta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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

    private Logger logger = LoggerFactory.getLogger(DefaultTestAutomationAdapter.class);

    private static final String AUTHORIZATION_CODE = "authorization_code";
    private static final String CODE = "code";
    private static final String BASIC = "Basic ";
    private static final String PASSWORD = "password";

    private final AuthApi idamApi;
    private final ServiceAuthorisationApi serviceAuthorisationApi;

    private Cache<String, UserData> users;
    private Cache<String, ServiceAuthTokenGenerator> tokenGenerators;

    private BeftaTestDataLoader dataLoader;

    public DefaultTestAutomationAdapter() {
        serviceAuthorisationApi = BeftaServiceAuthorisationApiClientFactory.createServiceAuthorisationApiClient();
        idamApi = BeftaIdamApiClientFactory.createAuthorizationClient();

        dataLoader = buildTestDataLoader();

        setupUsersCache();
        setupTokenGeneratorsCache();
    }

    private void setupUsersCache() {
        CacheBuilder<Object, Object> userCacheBuilder = CacheBuilder.newBuilder();

        Long userCacheTtl = BeftaMain.getConfig().getUserTokenCacheTtlInSeconds();
        if (userCacheTtl != 0) {
            userCacheBuilder.expireAfterAccess(userCacheTtl, TimeUnit.SECONDS);
        }
        users = userCacheBuilder.build();
    }

    private void setupTokenGeneratorsCache() {
        CacheBuilder<Object, Object> tokenGeneratorsCacheBuilder = CacheBuilder.newBuilder();
        Long tokenGeneratorCacheTtl = BeftaMain.getConfig().getS2STokenCacheTtlInSeconds();
        if (tokenGeneratorCacheTtl != 0) {
            tokenGeneratorsCacheBuilder.expireAfterAccess(tokenGeneratorCacheTtl, TimeUnit.SECONDS);
        }
        tokenGenerators = tokenGeneratorsCacheBuilder.build();

        ServiceAuthTokenGenerator defaultGenerator = getNewS2sClientWithCredentials(
                BeftaMain.getConfig().getS2SClientId(), BeftaMain.getConfig().getS2SClientSecret());

        tokenGenerators.put(BeftaMain.getConfig().getS2SClientId(), defaultGenerator);
    }

    @Override
    public String getNewS2SToken() {
        return getNewS2SToken(BeftaMain.getConfig().getS2SClientId());
    }

    @Override
    public synchronized String getNewS2SToken(String clientId) {
        String s2sToken = null;
        try {
            if(tokenGenerators.asMap().containsKey(clientId)) {
                logger.info("Using S2S token from cache for the clientId: {}", clientId);
            } else{
                logger.info("Generating S2S token for the user: {}", clientId);
            }
            s2sToken = tokenGenerators.get(clientId, () -> getNewS2sClient(clientId)).generate();
        } catch (ExecutionException e) {
            BeftaUtils.defaultLog("Exception when acquiring a new S2S token for client Id:" + clientId);
        }

        return s2sToken;
    }

    @Override
    public synchronized void authenticate(UserData user, String userTokenClientId) throws ExecutionException {
        if(users.asMap().containsKey(user.getUsername())){
            logger.info("Using token from cache for the user: {}", user.getUsername());
        } else {
            logger.info("Generating token for the user: {}", user.getUsername());
        }
        UserData userData = users.get(user.getUsername(), () -> createAuthenticatedUserData(user.getUsername(), user.getPassword(), userTokenClientId));
        user.setId(userData.getId());
        user.setAccessToken(userData.getAccessToken());
    }

    private UserData createAuthenticatedUserData(String userName, String password, String userTokenClientId) {
        final String accessToken = getUserAccessToken(userName, password,
                UserTokenProviderConfig.of(userTokenClientId));
        final AuthApi.User idamUser = idamApi.getUser(accessToken);

        UserData userData = new UserData(userName, password);
        userData.setId(idamUser.getId());
        userData.setAccessToken(accessToken);

        return userData;
    }

    protected BeftaTestDataLoader buildTestDataLoader() {
        return new DefaultBeftaTestDataLoader();
    }

    protected ServiceAuthTokenGenerator getNewS2sClient(String s2sClientId) {
        String clientSecret = EnvironmentVariableUtils
                .getRequiredVariable("BEFTA_S2S_CLIENT_SECRET_OF_" + s2sClientId.toUpperCase());
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
        logger.info("User >> {}", printableOf(authorisation));
        String base64Authorisation = Base64.getEncoder().encodeToString(authorisation.getBytes());

        AuthApi.AuthenticateUserResponse authenticateUserResponse = idamApi.authenticateUser(
                BASIC + base64Authorisation, CODE, tokenProviderConfig.getClientId(),
                tokenProviderConfig.getRedirectUri());

        printLogs(tokenProviderConfig);
        AuthApi.TokenExchangeResponse tokenExchangeResponse = idamApi.exchangeCode(authenticateUserResponse.getCode(),
                AUTHORIZATION_CODE, tokenProviderConfig.getClientId(), tokenProviderConfig.getClientSecret(),
                tokenProviderConfig.getRedirectUri());

        return tokenExchangeResponse.getAccessToken();
    }

    private void printLogs(UserTokenProviderConfig tokenProviderConfig) {
        logger.info("Token type >> {}", tokenProviderConfig.getAccessTokenType());
        logger.info("Client id >> {}", tokenProviderConfig.getClientId());
        logger.info("Client secret >> {}", printableOf(tokenProviderConfig.getClientSecret()));
        logger.info("Redicrect uri >> {}", tokenProviderConfig.getRedirectUri());
        logger.info("Scope vars >> {}", tokenProviderConfig.getScopeVariables());
    }

    private String printableOf(String s) {
        if (s == null)
            return null;
        String out = "";
        for (int i = 0; i < s.length(); i++)
            out = out + "_|_" + s.charAt(i);
        return out;
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

    protected String getDateTimeFormatRequested(String key) {
        return BeftaUtils.getDateTimeFormatRequested(key);
    }

    @Override
    public BeftaTestDataLoader getDataLoader() {
        return dataLoader;
    }
}
