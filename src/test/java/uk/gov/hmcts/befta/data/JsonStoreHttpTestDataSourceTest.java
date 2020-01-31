package uk.gov.hmcts.befta.data;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(PowerMockRunner.class)
public class JsonStoreHttpTestDataSourceTest {

    private static final String[] TEST_DATA_RESOURCE_PACKAGES = { "framework-test-data" };

    private JsonStoreHttpTestDataSource dataSource;

    @Before
    public void setUp() {
        dataSource = new JsonStoreHttpTestDataSource(TEST_DATA_RESOURCE_PACKAGES);
    }
    
    @Test
    public void shouldGetBasicDataForTestCallSuccessfully() {
        HttpTestData result = dataSource.getDataForTestCall("Simple-Data-Without-Inheritance");

        RequestData requestData = result.getRequest();
        ResponseData responseData = result.getExpectedResponse();
        UserData invokingUser = result.getUsers().get("invokingUser");

        simpleDataAsserts(result);
        assertEquals("Simple-Data-Without-Inheritance", result.get_guid_());
        assertEquals("TITLE", result.getTitle());
        assertEquals(1, result.getUsers().size());

        assertEquals(2, requestData.getPathVariables().keySet().size());
        assertEquals(2, requestData.getQueryParams().keySet().size());
        assertEquals(2, requestData.getBody().keySet().size());
        assertEquals(2, requestData.getHeaders().keySet().size());

        assertEquals(2, responseData.getHeaders().keySet().size());
        assertEquals(2, responseData.getHeaders().keySet().size());
    }

    @Test
    public void shouldGetInheritedDataForTestCallAndExtendSuccessfully() {
        HttpTestData result = dataSource.getDataForTestCall("Simple-Data-With-Inheritance");
        RequestData requestData = result.getRequest();
        ResponseData responseData = result.getExpectedResponse();
        UserData otherUser = result.getUsers().get("otherUser");

        simpleDataAsserts(result);
        assertEquals("Simple-Data-With-Inheritance", result.get_guid_());
        assertEquals("OTHER USERNAME", otherUser.getUsername());
        assertEquals("OTHER PASSWORD", otherUser.getPassword());
        assertEquals("456", otherUser.getId());
        assertEquals(2, result.getUsers().size());

        assertEquals(3, requestData.getPathVariables().keySet().size());
        assertEquals(3, requestData.getQueryParams().keySet().size());
        assertEquals(3, requestData.getBody().keySet().size());
        assertEquals(3, requestData.getHeaders().keySet().size());
        assertEquals("REQ PATH VAR 3", requestData.getPathVariables().get("reqvar3"));
        assertEquals("REQ PARAM 3", requestData.getQueryParams().get("reqparam3"));
        assertEquals("REQ BODY 3", requestData.getBody().get("reqbody3"));
        assertEquals("REQ HEADER 3", requestData.getHeaders().get("reqheader3"));

        assertEquals(400, responseData.getResponseCode());
        assertEquals(3, responseData.getHeaders().keySet().size());
        assertEquals(3, responseData.getHeaders().keySet().size());
        assertEquals("RES BODY 3", responseData.getBody().get("resbody3"));
        assertEquals("RES HEADER 3", responseData.getHeaders().get("resheader3"));
    }

    @Test
    public void shouldGetInheritedDataForTestCallAndOverrideSuccessfully() {
        HttpTestData result = dataSource.getDataForTestCall("Simple-Data-With-Overrides");
        RequestData requestData = result.getRequest();
        ResponseData responseData = result.getExpectedResponse();
        UserData invokingUser = result.getUsers().get("invokingUser");

        assertEquals("Simple-Data-With-Overrides", result.get_guid_());
        assertEquals("NEW USERNAME", invokingUser.getUsername());
        assertEquals("NEW PASSWORD", invokingUser.getPassword());
        assertEquals("789", invokingUser.getId());
        assertEquals(invokingUser, result.getInvokingUser());
        assertEquals(1, result.getUsers().size());

        assertEquals(2, requestData.getPathVariables().keySet().size());
        assertEquals(2, requestData.getQueryParams().keySet().size());
        assertEquals(2, requestData.getBody().keySet().size());
        assertEquals(2, requestData.getHeaders().keySet().size());
        assertEquals("NEW REQ PATH VAR 2", requestData.getPathVariables().get("reqvar2"));
        assertEquals("NEW REQ PARAM 2", requestData.getQueryParams().get("reqparam2"));
        assertEquals("NEW REQ BODY 2", requestData.getBody().get("reqbody2"));
        assertEquals("NEW REQ HEADER 2", requestData.getHeaders().get("reqheader2"));

        assertEquals(200, responseData.getResponseCode());
        assertEquals(2, responseData.getHeaders().keySet().size());
        assertEquals(2, responseData.getHeaders().keySet().size());
        assertEquals("NEW RES BODY 2", responseData.getBody().get("resbody2"));
        assertEquals("NEW RES HEADER 2", responseData.getHeaders().get("resheader2"));
    }

    @Test
    public void shouldReturnNullWhenGettingDataForTestDataIdNotFound() {
        HttpTestData result = dataSource.getDataForTestCall("Non-Existing-Id");
        assertNull(result);
    }

    private void simpleDataAsserts(HttpTestData result) {
        RequestData requestData = result.getRequest();
        ResponseData responseData = result.getExpectedResponse();
        UserData invokingUser = result.getUsers().get("invokingUser");

        assertEquals("INVOKING USERNAME", invokingUser.getUsername());
        assertEquals("INVOKING PASSWORD", invokingUser.getPassword());
        assertEquals("123", invokingUser.getId());
        assertEquals(invokingUser, result.getInvokingUser());
        assertEquals("CCD Data Store", result.getProductName());
        assertEquals("OPERATION", result.getOperationName());
        assertEquals("POST", result.getMethod());
        assertEquals("URI", result.getUri());
        assertEquals(2, result.getSpecs().size());
        assertEquals("SPEC 1", result.getSpecs().get(0));
        assertEquals("SPEC 2", result.getSpecs().get(1));

        assertEquals("REQ PATH VAR 1", requestData.getPathVariables().get("reqvar1"));
        assertEquals("REQ PATH VAR 2", requestData.getPathVariables().get("reqvar2"));
        assertEquals("REQ PARAM 1", requestData.getQueryParams().get("reqparam1"));
        assertEquals("REQ PARAM 2", requestData.getQueryParams().get("reqparam2"));
        assertEquals("REQ BODY 1", requestData.getBody().get("reqbody1"));
        assertEquals("REQ BODY 2", requestData.getBody().get("reqbody2"));
        assertEquals("REQ HEADER 1", requestData.getHeaders().get("reqheader1"));
        assertEquals("REQ HEADER 2", requestData.getHeaders().get("reqheader2"));

        assertEquals(400, responseData.getResponseCode());
        assertEquals("RESPONSE MESSAGE", responseData.getResponseMessage());
        assertEquals("RES BODY 1", responseData.getBody().get("resbody1"));
        assertEquals("RES BODY 2", responseData.getBody().get("resbody2"));
        assertEquals("RES HEADER 1", responseData.getHeaders().get("resheader1"));
        assertEquals("RES HEADER 2", responseData.getHeaders().get("resheader2"));
    }
}

