package uk.gov.hmcts.befta.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecentExecutionsInfo {

    public static final String LAST_EXECUTION_TIME = "lastExecutionTime";
    public static final String LAST_EXECUTION_PROJECT_REPO = "lastExecutionProjectRepo";
    public static final String LAST_EXECUTION_PROJECT_BRANCH = "lastExecutionProjectBranch";


    @JsonProperty(LAST_EXECUTION_TIME)
    private String lastExecutionTime;

    @JsonProperty(LAST_EXECUTION_PROJECT_REPO)
    private String lastExecutionProjectRepo;

    @JsonProperty(LAST_EXECUTION_PROJECT_BRANCH)
    private String lastExecutionProjectBranch;

    public void setLastExecutionTime(String lastExecutionTime) {
        this.lastExecutionTime = lastExecutionTime;
    }

    public void setLastExecutionProjectRepo(String lastExecutionProjectRepo) {
        this.lastExecutionProjectRepo = lastExecutionProjectRepo;
    }

    public void setLastExecutionProjectBranch(String lastExecutionProjectBranch) {
        this.lastExecutionProjectBranch = lastExecutionProjectBranch;
    }

    public String getLastExecutionTime() {
        return lastExecutionTime;
    }

    public String getLastExecutionProjectRepo() {
        return lastExecutionProjectRepo;
    }

    public String getLastExecutionProjectBranch() {
        return lastExecutionProjectBranch;
    }
}
