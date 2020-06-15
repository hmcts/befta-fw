package uk.gov.hmcts.befta.data;

import java.util.Map;
import java.util.TreeMap;

import lombok.Data;
import uk.gov.hmcts.befta.util.JsonUtils;

@Data
public class ResponseData {

    private int responseCode;

    private String responseMessage;

    private Map<String, Object> headers;

    private Map<String, Object> body;

    public void setHeaders(Map<String, Object> headers) {
        this.headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.headers.putAll(headers);
    }

    public ResponseData() {

    }

    @SuppressWarnings("unchecked")
    public ResponseData(ResponseData other) {
        setResponseCode(other.getResponseCode());
        setResponseMessage(other.getResponseMessage());
        setHeaders((Map<String, Object>) JsonUtils.deepCopy(other.getHeaders()));
        setBody((Map<String, Object>) JsonUtils.deepCopy(other.getBody()));
    }
}
