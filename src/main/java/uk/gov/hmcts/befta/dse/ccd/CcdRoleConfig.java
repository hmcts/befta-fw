package uk.gov.hmcts.befta.dse.ccd;

import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CcdRoleConfig {

    private String role;

    private String securityClassification;

}
