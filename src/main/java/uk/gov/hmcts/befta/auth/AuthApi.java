package uk.gov.hmcts.befta.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import feign.Body;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import lombok.Getter;

import java.util.List;

public interface AuthApi {

    @RequestLine("POST /oauth2/authorize")
    @Headers({"Authorization: {authorization}", "Content-Type: application/x-www-form-urlencoded"})
    @Body("response_type={response_type}&redirect_uri={redirect_uri}&client_id={client_id}")
    AuthenticateUserResponse authenticateUser(@Param("authorization") String authorization,
                                              @Param(value = "response_type", expander = FormUrlEncodedExpander.class) String responseType,
                                              @Param(value = "client_id", expander = FormUrlEncodedExpander.class) String clientId,
                                              @Param(value = "redirect_uri", expander = FormUrlEncodedExpander.class) String redirectUri);

    @RequestLine("POST /oauth2/token")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @Body("code={code}&grant_type={grant_type}&client_id={client_id}&client_secret={client_secret}&redirect_uri={redirect_uri}")
    TokenExchangeResponse exchangeCode(@Param(value = "code", expander = FormUrlEncodedExpander.class) String code,
                                       @Param(value = "grant_type", expander = FormUrlEncodedExpander.class) String grantType,
                                       @Param(value = "client_id", expander = FormUrlEncodedExpander.class) String clientId,
                                       @Param(value = "client_secret", expander = FormUrlEncodedExpander.class) String clientSecret,
                                       @Param(value = "redirect_uri", expander = FormUrlEncodedExpander.class) String redirectUri);

    @RequestLine("POST /o/token")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @Body("client_id={client_id}&client_secret={client_secret}&grant_type={grant_type}&scope={scope}&username={username}&password={password}")
    TokenExchangeResponse generateOIDCToken(@Param(value = "client_id", expander = FormUrlEncodedExpander.class) String clientId,
                                            @Param(value = "client_secret", expander = FormUrlEncodedExpander.class) String clientSecret,
                                            @Param(value = "grant_type", expander = FormUrlEncodedExpander.class) String grantType,
                                            @Param(value = "scope", expander = FormUrlEncodedExpander.class) String scope,
                                            @Param(value = "username", expander = FormUrlEncodedExpander.class) String userName,
                                            @Param(value = "password", expander = FormUrlEncodedExpander.class) String password);

    @RequestLine("GET /details")
    @Headers("Authorization: Bearer {access_token}")
    User getUser(@Param("access_token") String accessToken);

    @RequestLine("GET /o/userinfo")
    @Headers("Authorization: Bearer {access_token}")
    IdamUser getUserInfo(@Param("access_token") String accessToken);

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
    class IdamUser {
        @JsonProperty("uid")
        private String uid;

        @JsonProperty("roles")
        private List<String> roles;
    }


}

