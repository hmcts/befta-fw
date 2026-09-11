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

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/o/token", exchange -> {
            requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            byte[] response = "{}".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    @Test
    void shouldEncodeFormBodyParameters() {
        AuthApi authApi = Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(AuthApi.class, "http://localhost:" + server.getAddress().getPort());

        authApi.generateOIDCToken("client id", "secret&value", "password", "scope+value",
                "user+name@test.test", "pässword");

        // check that the data was actually encoded
        assertEquals("client_id=client+id&client_secret=secret%26value&grant_type=password"
            + "&scope=scope%2Bvalue&username=user%2Bname%40test.test&password=p%C3%A4ssword", requestBody);

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