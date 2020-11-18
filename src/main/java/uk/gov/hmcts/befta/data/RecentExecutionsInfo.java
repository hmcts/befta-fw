package uk.gov.hmcts.befta.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

public class RecentExecutionsInfo {

    @Getter @Setter
    @JsonProperty("lastExecutionTime")
    private String lastExecutionTime;

    @Getter @Setter
    @JsonProperty("lastExecutionProjectRepo")
    private String lastExecutionProjectRepo;

    @Getter @Setter
    @JsonProperty("lastExecutionProjectBranch")
    private String lastExecutionProjectBranch;


}
