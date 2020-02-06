package uk.gov.hmcts.befta.data;

import lombok.Data;

import java.util.Map;
import java.util.TreeMap;

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
}
