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
    public synchronized void loadTestDataIfNecessary() {
        if (!wasTestDataRecentlyLoaded() && !isTestDataLoadedForCurrentRound) {
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

    public void updateDataLoadDetailsInRecentExecutionsInfo() {
        String recentExecutionsInfoFilePath = TestAutomationAdapter.EXECUTION_INFO_JSON_PATH;
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

    public String getCurrentGitBranch() {
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

    public String getCurrentGitRepo() {
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

    protected void doLoadTestData() {
    }

    @Override
    public synchronized boolean isTestDataLoadedForCurrentRound() {
        return this.isTestDataLoadedForCurrentRound;
    }

    public synchronized boolean wasTestDataRecentlyLoaded() {
        boolean wasRecentlyLoaded = true;
        try {
            String testDataReloadFrequency = BeftaMain.getConfig().getTestDataReloadFrequency();
            RecentExecutionsInfo recentExecutionsInfo = JsonUtils
                    .readObjectFromJsonFile(TestAutomationAdapter.EXECUTION_INFO_JSON_PATH,
                    RecentExecutionsInfo.class);
            String recentExecutionTime = recentExecutionsInfo.getLastExecutionTime();
            String recentExecutionGitBranch = recentExecutionsInfo.getLastExecutionProjectBranch();
            String recentExecutionGitRepo = recentExecutionsInfo.getLastExecutionProjectRepo();

            if (timeFromLastLoadDurationGreaterThanFrequency(recentExecutionTime,
                    testDataReloadFrequency)
                    || !isRecentExecutionFromSameRepoAndBranch(recentExecutionGitRepo, recentExecutionGitBranch)) {
                wasRecentlyLoaded = false;
            }
        } catch (Exception e) {
            return false;
        }
        return wasRecentlyLoaded;
    }

    public boolean timeFromLastLoadDurationGreaterThanFrequency(String recentExecutionTIme,
            String testDataReloadFrequency) {
        boolean isTimeElapsed = false;
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        LocalDateTime givenDateTIme = LocalDateTime.parse(recentExecutionTIme, format);
        Long timeDifference = Duration.between(givenDateTIme, currentTime).toMinutes();
        if (timeDifference >= Integer.parseInt(testDataReloadFrequency)) {
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
            System.out.println("repo branch not matching -" + recentRepoSubString + "--" + getCurrentGitRepo() + "--"
                    + recentBranch + "--" + getCurrentGitBranch());
        }
        return isSameRepoAndBranch;

    }

}
