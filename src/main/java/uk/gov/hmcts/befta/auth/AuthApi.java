package uk.gov.hmcts.befta.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import feign.Body;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import lombok.Getter;

public interface AuthApi {

    @RequestLine("POST /oauth2/authorize")
    @Headers({"Authorization: {authorization}", "Content-Type: application/x-www-form-urlencoded"})
    @Body("response_type={response_type}&redirect_uri={redirect_uri}&client_id={client_id}")
    AuthenticateUserResponse authenticateUser(@Param("authorization") String authorization,
                                              @Param("response_type") String responseType,
                                              @Param("client_id") String clientId,
                                              @Param("redirect_uri") String redirectUri);

    @RequestLine("POST /oauth2/token")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @Body("code={code}&grant_type={grant_type}&client_id={client_id}&client_secret={client_secret}&redirect_uri={redirect_uri}")
    TokenExchangeResponse exchangeCode(@Param("code") String code,
                                       @Param("grant_type") String grantType,
                                       @Param("client_id") String clientId,
                                       @Param("client_secret") String clientSecret,
                                       @Param("redirect_uri") String redirectUri);

    @RequestLine("GET /details")
    @Headers("Authorization: Bearer {access_token}")
    User getUser(@Param("access_token") String accessToken);

    @Getter
    class AuthenticateUserResponse {
        @JsonProperty("code")
        private String code;
    }

    @Getter
    class TokenExchangeResponse {
        @JsonProperty("access_token")
        private String accessToken;
    }

    @Getter
    class User {
        @JsonProperty("id")
        private String id;

        @JsonProperty("roles")
        private List<String> roles;
    }
}

