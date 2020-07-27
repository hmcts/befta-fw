package uk.gov.hmcts.befta.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Data;
import uk.gov.hmcts.befta.BeftaMain;
import uk.gov.hmcts.befta.auth.UserTokenProviderConfig;
import uk.gov.hmcts.befta.exception.FunctionalTestException;

@Data
public class HttpTestData {

    private static final String KEY_INVOKING_USER = "invokingUser";

    @SuppressWarnings({ "checkstyle:MemberName", "java:S116" })
    private String _guid_;

    @SuppressWarnings({ "checkstyle:MemberName", "java:S116" })
    private String _extends_;

    private String title;

    private List<String> specs;

    private String productName;

    private String operationName;

    private String method;

    private String uri;

    /**
     * Prerequisites can be specified as a List of objects.
     * <ul>
     * <li>if an element is a String, it will be treated as a guid</li>
     * <li>if an element is a Map, it will be treated as a unique context ID to a
     * guid entry</li>
     * </ul>
     * So the following will define 6 prerequisites: 4 of them referring the same
     * test data guid:
     * 
     * <pre>
     * {@code 
     * "prerequisites": {
     *   "call_data_id_1",
     *   { "call_data_id_2": "generic_data_1" },
     *   { "call_data_id_3": "generic_data_1" },
     *   { "call_data_id_4": "generic_data_1" },
     *   "call_data_id_5",
     *   { "call_data_id_6": "generic_data_1" },
     * }
     * }
     * </pre>
     */
    private List<Object> prerequisites = new ArrayList<>();

    private RequestData request;

    private ResponseData expectedResponse;

    private ResponseData actualResponse;

    private LinkedHashMap<String, UserData> users = new LinkedHashMap<>();

    private String s2sClientId;

    private String userTokenClientId;

    public HttpTestData() {
    }

    public HttpTestData(HttpTestData other) {
        this.set_guid_(other.get_guid_());
        this.set_extends_(other.get_extends_());
        this.setTitle(other.getTitle());
        this.setSpecs(other.getSpecs() == null ? null : new ArrayList<>(other.getSpecs()));
        this.setProductName(other.getProductName());
        this.setOperationName(other.getOperationName());
        this.setMethod(other.getMethod());
        this.setUri(other.getUri());

        this.setPrerequisites(other.getPrerequisites());

        this.setRequest(other.getRequest() == null ? null : new RequestData(other.getRequest()));
        this.setExpectedResponse(
                other.getExpectedResponse() == null ? null : new ResponseData(other.getExpectedResponse()));
        this.setActualResponse(other.getActualResponse() == null ? null : new ResponseData(other.getActualResponse()));

        this.setUsers(new LinkedHashMap<>());

        for (Entry<String, UserData> entry : other.getUsers().entrySet()) {
            this.users.put(entry.getKey(), new UserData(entry.getValue()));
        }

        this.setUserTokenClientId(other.getUserTokenClientId());
        this.setS2sClientId(other.getS2sClientId());
    }

    public boolean meetsSpec(String specification) {
        return specs.contains(specification);
    }

    public boolean meetsOperationOfProduct(final String productName, final String operationName) {
        return operationName.equals(this.operationName) && productName.equals(this.productName);
    }

    public UserData getInvokingUser() {
        UserData userInMap = getUsers().get(KEY_INVOKING_USER);
        if (userInMap != null)
            return userInMap;
        return getUsers().values().iterator().next();
    }

    public void setInvokingUser(UserData invokingUser) {
        getUsers().put(KEY_INVOKING_USER, invokingUser);
    }

    public void setUser(UserData user) {
        setInvokingUser(user);
    }

    public void setUsers(Map<String, UserData> users) {
        if (users == null) {
            throw new IllegalArgumentException("User map cannot be null.");
        }
        this.users = (LinkedHashMap<String, UserData>) users;
    }

    public String getS2sClientId() {
        if (this.s2sClientId == null) {
            return BeftaMain.getConfig().getS2SClientId();
        }
        return s2sClientId;
    }

    public String getUserTokenClientId() {
        if (this.userTokenClientId == null) {
            return UserTokenProviderConfig.DEFAULT_INSTANCE.getClientId();
        }
        return userTokenClientId;
    }

    public Entry<String, UserData> getUserEntryAt(int userIndex) {
        int i = 0;
        for (Entry<String, UserData> e : users.entrySet()) {
            if (i == userIndex) {
                return e;
            }
            i++;
        }
        throw new FunctionalTestException("No user entry with index " + userIndex);
    }
}
