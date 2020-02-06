package uk.gov.hmcts.befta.data;

import lombok.Data;

import java.util.Map;

@Data
public class RequestData {

    private Map<String, Object> headers;

    private Map<String, Object> pathVariables;

    private Map<String, Object> queryParams;

    private Map<String, Object> body;
}
