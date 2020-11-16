package uk.gov.hmcts.befta;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import uk.gov.hmcts.befta.data.RecentExecutionsInfo;
import uk.gov.hmcts.befta.util.BeftaUtils;
import uk.gov.hmcts.befta.util.JsonUtils;

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
            double testDataLoadSkipPeriod = BeftaMain.getConfig().getTestDataLoadSkipPeriod();
            RecentExecutionsInfo recentExecutionsInfo = JsonUtils
                    .readObjectFromJsonFile(TestAutomationAdapter.EXECUTION_INFO_FILE, RecentExecutionsInfo.class);
            String recentExecutionTime = recentExecutionsInfo.getLastExecutionTime();

            if (isWithinSkipPeriod(
                    recentExecutionTime,
                    testDataLoadSkipPeriod)
                    && wasMostRecentDataForSameTests(recentExecutionsInfo)) {
                return true;
            }
        } catch (Exception e) {
            BeftaUtils.defaultLog("Should NOT skip loading data.", e);
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
        }

        return branchName;
    }

    private String getCurrentGitRepo() {
        String repoName = "";
        try {
            Process process = Runtime.getRuntime().exec("git remote -v");
            process.waitFor();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            repoName = reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return repoName;
    }

    private boolean isWithinSkipPeriod(
            String recentExecutionTime,
            double testDataLoadSkipPeriod) {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        LocalDateTime givenDateTIme = LocalDateTime.parse(recentExecutionTime, format);
        Long timeDifference = Duration.between(givenDateTIme, currentTime).toMinutes();
        return timeDifference < testDataLoadSkipPeriod;
    }

    private boolean wasMostRecentDataForSameTests(RecentExecutionsInfo recentExecutionsInfo) {
        String repoName = recentExecutionsInfo.getLastExecutionProjectRepo();
        String branchName = recentExecutionsInfo.getLastExecutionProjectBranch();
        String recentRepoSubString = repoName.substring(repoName.indexOf(":"), repoName.length());
        if (getCurrentGitRepo().contains(recentRepoSubString) && getCurrentGitBranch().equalsIgnoreCase(branchName)) {
            return true;
        } else {
            BeftaUtils.defaultLog("repo branch not matching -" + recentRepoSubString + "--"
                    + getCurrentGitRepo()
                    + "--"
                    + branchName + "--" + getCurrentGitBranch());
        }
        return false;
    }

}
