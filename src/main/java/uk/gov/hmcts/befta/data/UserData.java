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

    public UserData(UserData other) {
        this.setId(other.getId());
        this.setUsername(other.getUsername());
        this.setPassword(other.getPassword());
        this.setAccessToken(other.getAccessToken());
    }

}
