package uk.gov.hmcts.befta.data;

import java.util.Map;

import lombok.Data;

@Data
public class RequestData {

    private Map<String, Object> headers;

    private Map<String, Object> pathVariables;

    private Map<String, Object> queryParams;

    private Map<String, Object> body;

    public boolean isMultipart() {
        return headers != null && headers.get("media-type") != null
                && headers.get("media-type").toString().toLowerCase().contains("multipart");
    }
}
