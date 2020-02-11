package uk.gov.hmcts.befta.data;

import lombok.Data;

@Data
public class UserData {

    private String id;

    private String username;

    private String password;

    private String accessToken;

    public UserData() {
    }

    public UserData(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
