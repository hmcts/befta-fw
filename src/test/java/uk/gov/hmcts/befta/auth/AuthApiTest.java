package uk.gov.hmcts.befta.auth;

import com.sun.net.httpserver.HttpServer;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthApiTest {

    private HttpServer server;
    private String requestBody;
    private String authHeader;
    private AuthApi authApi;

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);

        setContextForServerUrl("/oauth2/authorize");
        setContextForServerUrl("/oauth2/token");
        setContextForServerUrl("/o/token");
        server.start();


        authApi = Feign.builder()
            .encoder(new JacksonEncoder())
            .decoder(new JacksonDecoder())
            .target(AuthApi.class, "http://localhost:" + server.getAddress().getPort());
    }

    private void setContextForServerUrl(String serverUrl) {
        server.createContext(serverUrl, exchange -> {
            authHeader = exchange.getRequestHeaders().getFirst("Authorization");
            requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            byte[] response = "{}".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    @Test
    void authenticateUser_shouldEncodeFormBodyParameters() {
        authApi.authenticateUser("auth", "response+type", "client&id", "http://localhost:2200/test/url");

        // check that the data was actually encoded
        assertEquals(authHeader, "auth");
        assertEquals(requestBody, "response_type=response%2Btype&redirect_uri=http%3A//localhost%3A2200/test/url&client_id=client%26id");

        Map<String, String> form = Arrays.stream(requestBody.split("&"))
                .map(entry -> entry.split("=", 2))
                .collect(Collectors.toMap(entry -> entry[0], entry -> URLDecoder.decode(entry[1], StandardCharsets.UTF_8)));

        // check that the data decodes correctly
        assertEquals(Map.of(
                "response_type", "response+type",
                "client_id", "client&id",
                "redirect_uri", "http://localhost:2200/test/url"), form);
    }

    @Test
    void exchangeCode_shouldEncodeFormBodyParameters() {
        authApi.exchangeCode("code#222", "grant&type", "client+id", "Cl(nt sEcr3T", "http://localhost:2200/test/url");

        // check that the data was actually encoded
        assertEquals(authHeader, null);
        assertEquals(requestBody, "code=code%23222&grant_type=grant%26type&client_id=client%2Bid&client_secret=Cl%28nt+sEcr3T&redirect_uri=http%3A//localhost%3A2200/test/url");

        Map<String, String> form = Arrays.stream(requestBody.split("&"))
                .map(entry -> entry.split("=", 2))
                .collect(Collectors.toMap(entry -> entry[0], entry -> URLDecoder.decode(entry[1], StandardCharsets.UTF_8)));

        // check that the data decodes correctly
        assertEquals(Map.of(
                "code", "code#222",
                "grant_type", "grant&type",
                "client_id", "client+id",
                "client_secret", "Cl(nt sEcr3T",
                "redirect_uri", "http://localhost:2200/test/url"), form);
    }

    @Test
    void generateOIDCToken_shouldEncodeFormBodyParameters() {

        authApi.generateOIDCToken("client id", "secret&value", "password", "scope+value",
                "user+name@test.test", "pässword");

        // check that the data was actually encoded
        assertEquals(authHeader, null);
        assertEquals(requestBody, "client_id=client+id&client_secret=secret%26value&grant_type=password"
            + "&scope=scope%2Bvalue&username=user%2Bname%40test.test&password=p%C3%A4ssword");

        Map<String, String> form = Arrays.stream(requestBody.split("&"))
                .map(entry -> entry.split("=", 2))
                .collect(Collectors.toMap(entry -> entry[0], entry -> URLDecoder.decode(entry[1], StandardCharsets.UTF_8)));

        // check that the data decodes correctly
        assertEquals(Map.of(
                "client_id", "client id",
                "client_secret", "secret&value",
                "grant_type", "password",
                "scope", "scope+value",
                "username", "user+name@test.test",
                "password", "pässword"), form);
    }
}
