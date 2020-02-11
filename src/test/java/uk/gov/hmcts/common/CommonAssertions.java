package uk.gov.hmcts.common;

import uk.gov.hmcts.befta.data.HttpTestData;
import uk.gov.hmcts.befta.data.RequestData;
import uk.gov.hmcts.befta.data.ResponseData;
import uk.gov.hmcts.befta.data.UserData;

import static org.junit.Assert.assertEquals;

public class CommonAssertions {

    public static void applyCommonAssertionsOnBasicData(HttpTestData result) {
        RequestData requestData = result.getRequest();
        ResponseData responseData = result.getExpectedResponse();
        UserData invokingUser = result.getUsers().get("invokingUser");

        assertEquals("Simple-Data-Without-Inheritance", result.get_guid_());
        assertEquals("TITLE", result.getTitle());
        assertEquals(1, result.getUsers().size());
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

        assertEquals(2, requestData.getPathVariables().keySet().size());
        assertEquals(2, requestData.getQueryParams().keySet().size());
        assertEquals(2, requestData.getBody().keySet().size());
        assertEquals(2, requestData.getHeaders().keySet().size());

        assertEquals(2, responseData.getHeaders().keySet().size());
        assertEquals(2, responseData.getHeaders().keySet().size());
    }

    public static void applyCommonAssertionsOnExtendedData(HttpTestData result) {
        RequestData requestData = result.getRequest();
        ResponseData responseData = result.getExpectedResponse();
        UserData invokingUser = result.getUsers().get("invokingUser");
        UserData otherUser = result.getUsers().get("otherUser");

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

    public static void applyCommonAssertionsOnOverriddenData(HttpTestData result) {
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
}

