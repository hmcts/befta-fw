package uk.gov.hmcts.befta.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import feign.Body;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

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

    @RequestLine("POST /o/token")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @Body("client_id={client_id}&client_secret={client_secret}&grant_type={grant_type}&scope={scope}&username={username}&password={password}")
    TokenExchangeResponse generateOIDCToken(@Param("client_id") String clientId,
                                            @Param("client_secret") String clientSecret,
                                            @Param("grant_type") String grantType,
                                            @Param("scope") String scope,
                                            @Param("username") String userName,
                                            @Param("password") String password);

    @RequestLine("GET /details")
    @Headers("Authorization: Bearer {access_token}")
    User getUser(@Param("access_token") String accessToken);

    @RequestLine("GET /o/userinfo")
    @Headers("Authorization: Bearer {access_token}")
    UserInfo getUserInfo(@Param("access_token") String accessToken);

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

    @Getter
    class UserInfo {
        @JsonProperty("uid")
        private String uid;

        @JsonProperty("roles")
        private List<String> roles;
    }
}

