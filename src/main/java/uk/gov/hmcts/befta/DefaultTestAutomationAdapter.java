package uk.gov.hmcts.befta;

import java.io.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


import uk.gov.hmcts.befta.auth.AuthApi;
import uk.gov.hmcts.befta.auth.UserTokenProviderConfig;
import uk.gov.hmcts.befta.data.RecentExecutionsInfo;
import uk.gov.hmcts.befta.data.UserData;
import uk.gov.hmcts.befta.exception.FunctionalTestException;
import uk.gov.hmcts.befta.factory.BeftaIdamApiClientFactory;
import uk.gov.hmcts.befta.factory.BeftaServiceAuthorisationApiClientFactory;
import uk.gov.hmcts.befta.player.BackEndFunctionalTestScenarioContext;
import uk.gov.hmcts.befta.util.EnvironmentVariableUtils;
import uk.gov.hmcts.befta.util.JsonUtils;
import uk.gov.hmcts.befta.util.ReflectionUtils;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;

public class DefaultTestAutomationAdapter implements TestAutomationAdapter {

    private static final String AUTHORIZATION_CODE = "authorization_code";
    private static final String CODE = "code";
    private static final String BASIC = "Basic ";
    private static final String PASSWORD = "password";
    private static final String EXECUTION_INFO_JSON_PATH="./befta_recent_executions_info.json";

    private final AuthApi idamApi;
    private final ServiceAuthorisationApi serviceAuthorisationApi;

    private final Map<String, ServiceAuthTokenGenerator> tokenGenerators = new ConcurrentHashMap<>();

    private final Map<String, UserData> users = new HashMap<>();

    private boolean isTestDataLoadedForThisRound = false;

    public DefaultTestAutomationAdapter() {
        serviceAuthorisationApi = BeftaServiceAuthorisationApiClientFactory.createServiceAuthorisationApiClient();
        idamApi = BeftaIdamApiClientFactory.createAuthorizationClient();
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
        return tokenGenerators.computeIfAbsent(clientId, key -> {
            return getNewS2sClient(clientId);
        }).generate();
    }

    @Override
    public synchronized void authenticate(UserData user, String userTokenClientId) {
        UserData cached = users.computeIfAbsent(user.getUsername(), e -> {
            final String accessToken = getUserAccessToken(user.getUsername(), user
                            .getPassword(),
                    UserTokenProviderConfig.of(userTokenClientId));
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
    public synchronized void loadTestDataIfNecessary() {
        if (!wasTestDataRecentlyLoaded() && !isTestDataLoadedForThisRound) {
            try {
                doLoadTestData();
                updateDataLoadDetailsInRecentExecutionsInfo();
            } catch (Exception e) {
                throw e;
            } finally {
                isTestDataLoadedForThisRound = true;
            }
        }
    }


    public void updateDataLoadDetailsInRecentExecutionsInfo() {
        String recentExecutionsInfoFilePath = EXECUTION_INFO_JSON_PATH;
        String dateTimeFormat = getDateTimeFormatRequested("now");
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateTimeFormat));
        RecentExecutionsInfo recentExecutionsInfo = new RecentExecutionsInfo();
        recentExecutionsInfo.setLastExecutionTime(currentTime);
        recentExecutionsInfo.setLastExecutionProjectRepo(getCurrentGitRepo());
        recentExecutionsInfo.setLastExecutionProjectBranch(getCurrentGitBranch());
        try {
            JsonUtils.writeJsonToFile(recentExecutionsInfoFilePath, recentExecutionsInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCurrentGitBranch() {
        String branchName = "";
        try {
            Process process = Runtime.getRuntime().exec("git rev-parse --abbrev-ref HEAD");
            process.waitFor();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            branchName = reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return branchName;
    }

    public String getCurrentGitRepo() {
        String repoName = "";
        try {
            Process process = Runtime.getRuntime().exec("git remote -v");
            process.waitFor();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            repoName = reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return repoName;
    }

    protected void doLoadTestData() {
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

    public synchronized boolean isTestDataLoadedForThisRound() {
        return this.isTestDataLoadedForThisRound;
    }

    public synchronized boolean wasTestDataRecentlyLoaded() {
       boolean wasRecentlyLoaded = true;
      try {
          String testDataReloadFrequency = BeftaMain.getConfig().getTestDataReloadFrequency();
          RecentExecutionsInfo recentExecutionsInfo  = JsonUtils.readObjectFromJsonFile(EXECUTION_INFO_JSON_PATH, RecentExecutionsInfo.class);
          String recentExecutionTIme = recentExecutionsInfo.getLastExecutionTime();
          String recentExecutionGitBranch= recentExecutionsInfo.getLastExecutionProjectBranch();
          String recentExecutionGitRepo = recentExecutionsInfo.getLastExecutionProjectRepo();

          if(timeFromLastLoadDurationGreaterThanFrequency(recentExecutionTIme,testDataReloadFrequency ) ||
                  !isRecentExecutionFromSameRepoAndBranch(recentExecutionGitRepo, recentExecutionGitBranch)) {
              wasRecentlyLoaded = false;
          }
      }catch (Exception e) {
          e.printStackTrace();
      }
       return wasRecentlyLoaded;
    }

    public boolean timeFromLastLoadDurationGreaterThanFrequency(String recentExecutionTIme, String testDataReloadFrequency) {
        boolean isTimeElapsed = false;
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        LocalDateTime givenDateTIme = LocalDateTime.parse(recentExecutionTIme, format);
        Long timeDifference = Duration.between(givenDateTIme, currentTime).toMinutes();
        if(timeDifference >= Integer.parseInt(testDataReloadFrequency)) {
            isTimeElapsed = true;
        }
        return isTimeElapsed;
    }

    public boolean isRecentExecutionFromSameRepoAndBranch(String recentRepo, String recentBranch) {
        boolean isSameRepoAndBranch = false;
        String recentRepoSubString = recentRepo.substring(recentRepo.indexOf(":"), recentRepo.length());
        if (getCurrentGitRepo().contains(recentRepoSubString) && getCurrentGitBranch().equalsIgnoreCase(recentBranch)) {
            isSameRepoAndBranch = true;
        } else {
            System.out.println("repo branch not matching -" +
                    recentRepoSubString + "--" + getCurrentGitRepo() +
                    "--" + recentBranch + "--" + getCurrentGitBranch());
        }
        return isSameRepoAndBranch;

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
