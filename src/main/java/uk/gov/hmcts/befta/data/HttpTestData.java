package uk.gov.hmcts.befta.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import lombok.Data;
import uk.gov.hmcts.befta.BeftaMain;
import uk.gov.hmcts.befta.auth.OAuth2Config;

@Data
public class HttpTestData {

    private static final String KEY_INVOKING_USER = "invokingUser";

    private String _guid_;

    private String _extends_;

    private String title;

    private List<String> specs;

    private String productName;

    private String operationName;

    private String method;

    private String uri;

    private RequestData request;

    private ResponseData expectedResponse;

    private ResponseData actualResponse;

    private LinkedHashMap<String, UserData> users = new LinkedHashMap<>();

    private String s2sClientId;

    private String oauth2ClientId;

    private UserData userSet = null;

    public HttpTestData() {
    }

    public HttpTestData(HttpTestData other) {
        this.set_guid_(other.get_guid_());
        this.set_extends_(other.get_extends_());
        this.setTitle(other.getTitle());
        this.setSpecs(new ArrayList<>(other.getSpecs()));
        this.setProductName(other.getProductName());
        this.setOperationName(other.getOperationName());
        this.setMethod(other.getMethod());
        this.setUri(other.getUri());

        this.setRequest(other.getRequest() == null ? null : new RequestData(other.getRequest()));
        this.setExpectedResponse(
                other.getExpectedResponse() == null ? null : new ResponseData(other.getExpectedResponse()));
        this.setActualResponse(other.getActualResponse() == null ? null : new ResponseData(other.getActualResponse()));

        this.setUsers(new LinkedHashMap<>());

        for (Entry<String, UserData> entry : other.getUsers().entrySet()) {
            this.users.put(entry.getKey(), new UserData(entry.getValue()));
        }

        this.setS2sClientId(other.getS2sClientId());

        this.userSet = other.userSet;
    }

    public boolean meetsSpec(String specification) {
        return specs.contains(specification);
    }

    public boolean meetsOperationOfProduct(final String productName, final String operationName) {
        return operationName.equals(this.operationName) && productName.equals(this.productName);
    }

    public UserData getInvokingUser() {
        return getUsers().get(KEY_INVOKING_USER);
    }

    public void setInvokingUser(UserData invokingUser) {
        getUsers().put(KEY_INVOKING_USER, invokingUser);
    }

    public void setUser(UserData user) {
        setInvokingUser(user);
        userSet = user;
    }

    public void setUsers(LinkedHashMap<String, UserData> users) {
        if (users == null)
            throw new IllegalArgumentException("User map cannot be null.");
        this.users = users;
        if (userSet != null) {
            setInvokingUser(userSet);
        }
    }

    public String getS2sClientId() {
        if (this.s2sClientId == null) {
            return BeftaMain.getConfig().getS2SClientId();
        }
        return s2sClientId;
    }

    public String getOauth2ClientId() {
        if (this.oauth2ClientId == null) {
            return OAuth2Config.DEFAULT_INSTANCE.getClientId();
        }
        return oauth2ClientId;
    }
}
