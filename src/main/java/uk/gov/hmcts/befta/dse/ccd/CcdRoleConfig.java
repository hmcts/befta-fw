package uk.gov.hmcts.befta.dse.ccd;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CcdRoleConfig {

    private String role;

    private String securityClassification;

    public CcdRoleConfig(String role, String securityClassification) {
        super();
        this.role = role;
        this.securityClassification = securityClassification;
    }

}
