package uk.gov.hmcts.befta.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

public class RecentExecutionsInfo {

    @Getter @Setter
    @JsonProperty("dataSetupEnvironment")
    private String dataSetupEnvironment;

    @Getter
    @Setter
    @JsonProperty("lastExecutionTime")
    private String lastExecutionTime;

    @Getter @Setter
    @JsonProperty("lastExecutionProjectRepo")
    private String lastExecutionProjectRepo;

    @Getter @Setter
    @JsonProperty("lastExecutionProjectBranch")
    private String lastExecutionProjectBranch;

    public RecentExecutionsInfo() {
    }

    public RecentExecutionsInfo(String dataSetupEnvironment, String lastExecutionTime, String lastExecutionProjectRepo,
            String lastExecutionProjectBranch) {
        super();
        this.dataSetupEnvironment = dataSetupEnvironment;
        this.lastExecutionTime = lastExecutionTime;
        this.lastExecutionProjectRepo = lastExecutionProjectRepo;
        this.lastExecutionProjectBranch = lastExecutionProjectBranch;
    }

}
