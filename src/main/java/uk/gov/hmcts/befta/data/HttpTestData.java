package uk.gov.hmcts.befta.data;

import java.util.LinkedHashMap;
import java.util.List;

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

    public String get_guid_() {
        return _guid_;
    }

    public void set_guid_(String _guid_) {
        this._guid_ = _guid_;
    }

    public String get_extends_() {
        return _extends_;
    }

    public void set_extends_(String _extends_) {
        this._extends_ = _extends_;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getSpecs() {
        return specs;
    }

    public void setSpecs(List<String> specs) {
        this.specs = specs;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public RequestData getRequest() {
        return request;
    }

    public void setRequest(RequestData request) {
        this.request = request;
    }

    public ResponseData getExpectedResponse() {
        return expectedResponse;
    }

    public void setExpectedResponse(ResponseData expectedResponse) {
        this.expectedResponse = expectedResponse;
    }

    public ResponseData getActualResponse() {
        return actualResponse;
    }

    public void setActualResponse(ResponseData actualResponse) {
        this.actualResponse = actualResponse;
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

    private UserData userSet = null;

    public void setUser(UserData user) {
        setInvokingUser(user);
        userSet = user;
    }

    public LinkedHashMap<String, UserData> getUsers() {
        return users;
    }

    public void setUsers(LinkedHashMap<String, UserData> users) {
        if (users == null)
            throw new IllegalArgumentException("User map cannot be null.");
        this.users = users;
        if (userSet != null) {
            setInvokingUser(userSet);
        }
    }
}
