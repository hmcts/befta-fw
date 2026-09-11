package uk.gov.hmcts.befta.auth;

import feign.Param;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class FormUrlEncodedExpander implements Param.Expander {

    @Override
    public String expand(Object value) {

        if (value == null) {
            return null;
        }

        return URLEncoder.encode(String.valueOf(value), StandardCharsets.UTF_8);
    }
}
