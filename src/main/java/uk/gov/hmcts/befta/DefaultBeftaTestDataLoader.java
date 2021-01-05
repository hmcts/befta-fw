package uk.gov.hmcts.befta;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import io.restassured.RestAssured;
import uk.gov.hmcts.befta.data.RecentExecutionsInfo;
import uk.gov.hmcts.befta.util.BeftaUtils;
import uk.gov.hmcts.befta.util.JsonUtils;

import static java.lang.String.format;
import static uk.gov.hmcts.befta.util.BeftaUtils.defaultLog;

public class DefaultBeftaTestDataLoader implements BeftaTestDataLoader {

    private boolean isTestDataLoadedForCurrentRound = false;

    @Override
    public synchronized boolean isTestDataLoadedForCurrentRound() {
        return this.isTestDataLoadedForCurrentRound;
    }

    @Override
    public synchronized void loadTestDataIfNecessary() {
        if (!isTestDataLoadedForCurrentRound && !shouldSkipDataLoad()) {
            try {
                RestAssured.useRelaxedHTTPSValidation();
                doLoadTestData();
                updateDataLoadDetailsInRecentExecutionsInfo();
            } catch (Exception e) {
                throw e;
            } finally {
                isTestDataLoadedForCurrentRound = true;
            }
        }
    }

    protected void doLoadTestData() {
    }

    private boolean shouldSkipDataLoad() {
        try {
            //declaring with a dummy last execution time
            String recentExecutionTime  = "2020-01-01T00:00:00.001";
            File recentExecutionFile = new File(TestAutomationAdapter.EXECUTION_INFO_FILE);
            double testDataLoadSkipPeriod = BeftaMain.getConfig().getTestDataLoadSkipPeriod();
            defaultLog(format("testDataLoadSkipPeriod from the config: %s minutes", testDataLoadSkipPeriod));
            if(recentExecutionFile.exists()) {
                RecentExecutionsInfo recentExecutionsInfo = JsonUtils
                        .readObjectFromJsonFile(TestAutomationAdapter.EXECUTION_INFO_FILE, RecentExecutionsInfo.class);
                recentExecutionTime = recentExecutionsInfo.getLastExecutionTime();
                defaultLog(format("recent exec file exists and timestamp is : %s", recentExecutionTime));
                if (isWithinSkipPeriod(
                        recentExecutionTime,
                        testDataLoadSkipPeriod)
                        && wasMostRecentDataForSameTests(recentExecutionsInfo)) {
                    defaultLog("Should skip loading data.");
                    return true;
                }
            }
        } catch (Exception e) {
            defaultLog("Should NOT skip loading data.", e);
            return false;
        }
        return false;
    }

    private void updateDataLoadDetailsInRecentExecutionsInfo() {
        String recentExecutionsInfoFilePath = TestAutomationAdapter.EXECUTION_INFO_FILE;
        String dateTimeFormat = BeftaUtils.getDateTimeFormatRequested("now");
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

    private String getCurrentGitBranch() {
        String branchName = "";
        try {
            Process process = Runtime.getRuntime().exec("git rev-parse --abbrev-ref HEAD");
            process.waitFor();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            branchName = reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return branchName;
    }

    public String getCurrentGitRepo() {
        String repoName = System.getProperty("user.dir");
        repoName = repoName.substring(repoName.lastIndexOf("/") + 1);

        try {
            Process process = Runtime.getRuntime().exec("git remote -v");
            process.waitFor();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            repoName = reader.readLine();
            if (null != repoName && repoName.contains(".git")) {
                repoName = repoName.substring(repoName.indexOf(":hmcts/") + 7, repoName.lastIndexOf(".git"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return repoName.trim();
    }

    private boolean isWithinSkipPeriod(
            String recentExecutionTime,
            double testDataLoadSkipPeriod) {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        LocalDateTime givenDateTIme = LocalDateTime.parse(recentExecutionTime, format);
        Long timeDifference = Duration.between(givenDateTIme, currentTime).toMinutes();
        defaultLog(format("Reload test data if last test execution time (%s minutes ago) is "
                        + "greater than skip period value (%s minutes)", timeDifference, testDataLoadSkipPeriod));
        return timeDifference < testDataLoadSkipPeriod;
    }

    private boolean wasMostRecentDataForSameTests(RecentExecutionsInfo recentExecutionsInfo) {
        String repoName = recentExecutionsInfo.getLastExecutionProjectRepo();
        String branchName = recentExecutionsInfo.getLastExecutionProjectBranch();
        String recentRepoSubString = repoName.substring(repoName.lastIndexOf("/") + 1);
        if (getCurrentGitRepo().contains(recentRepoSubString) && getCurrentGitBranch().equalsIgnoreCase(branchName)) {
            defaultLog(format("the repository (%s) and the branch (%s) from the recent execution"
                    + " of %s matched", getCurrentGitRepo(), branchName, recentRepoSubString));
            return true;
        } else {
            defaultLog(format("The repository (%s) and the branch (%s) do not match: ",
                    getCurrentGitRepo(), branchName));
        }
        return false;
    }
}
