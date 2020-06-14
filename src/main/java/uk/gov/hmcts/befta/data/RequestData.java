package uk.gov.hmcts.befta.data;

import java.util.Map;

import lombok.Data;
import uk.gov.hmcts.befta.util.JsonUtils;

@Data
public class RequestData {

    private Map<String, Object> headers;

    private Map<String, Object> pathVariables;

    private Map<String, Object> queryParams;

    private Map<String, Object> body;

    public boolean isMultipart() {
        return headers != null && headers.get("Content-Type") != null
                && headers.get("Content-Type").toString().toLowerCase().contains("multipart");
    }

    public RequestData() {
    }

    @SuppressWarnings("unchecked")
    public RequestData(RequestData other) {
        setHeaders((Map<String, Object>) JsonUtils.deepCopy(other.getHeaders()));
        setPathVariables((Map<String, Object>) JsonUtils.deepCopy(other.getPathVariables()));
        setQueryParams((Map<String, Object>) JsonUtils.deepCopy(other.getQueryParams()));
        setBody((Map<String, Object>) JsonUtils.deepCopy(other.getBody()));
    }

}
