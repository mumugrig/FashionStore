package com.fashionstore.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fashionstore.dto.request.RegisterRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fashionstore.dto.response.AuthResponse;
import com.fashionstore.models.User;
import com.fashionstore.repositories.UserRepository;
import com.fashionstore.services.AuthService;
import com.fashionstore.vo.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerAuthTest {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @LocalServerPort
    private int port;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void usersMeRequiresAuthentication() throws Exception {
        HttpResponse<String> response = httpClient.send(
                HttpRequest.newBuilder(URI.create(url("/users/me"))).GET().build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertStatusIn(response.statusCode(), 401, 403);
    }

    @Test
    void usersMeReturnsCurrentUser() throws Exception {
        AuthResponse authResponse = authService.register(registerRequest("me@example.com"));
        HttpRequest request = HttpRequest.newBuilder(URI.create(url("/users/me")))
                .header("Authorization", "Bearer " + authResponse.getAccessToken())
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"email\":\"me@example.com\""));
    }

    @Test
    void registerEndpointCreatesUser() throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url("/auth/register")))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(registerRequest("endpoint@example.com"))))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode body = objectMapper.readTree(response.body());

        assertEquals(201, response.statusCode());
        assertNotNull(body.get("accessToken").asText());
        assertNotNull(body.get("refreshToken").asText());
        assertEquals("endpoint@example.com", body.get("user").get("email").asText());
    }

    @Test
    void logoutRequiresAuthentication() throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url("/auth/logout")))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"refreshToken\":\"unused\"}"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertStatusIn(response.statusCode(), 401, 403);
    }

    @Test
    void userEndpointRequiresAdminRole() throws Exception {
        AuthResponse userAuth = authService.register(registerRequest("normal-user@example.com"));
        AuthResponse adminAuth = createAdminAndLogin("admin@example.com");

        HttpResponse<String> userResponse = httpClient.send(
                getWithBearer("/users/1", userAuth.getAccessToken()),
                HttpResponse.BodyHandlers.ofString()
        );
        HttpResponse<String> adminResponse = httpClient.send(
                getWithBearer("/users/1", adminAuth.getAccessToken()),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(403, userResponse.statusCode());
        assertEquals(200, adminResponse.statusCode());
    }

    @Test
    void documentedAdminOnlyEndpointsRejectRegularUsers() throws Exception {
        AuthResponse userAuth = authService.register(registerRequest("regular@example.com"));

        for (Map.Entry<String, String> endpoint : adminOnlyPayloads().entrySet()) {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url(endpoint.getKey())))
                    .header("Authorization", "Bearer " + userAuth.getAccessToken())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(endpoint.getValue()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(403, response.statusCode(), endpoint.getKey());
        }
    }

    @Test
    void documentedAdminOnlyEndpointsAllowAdminsToReachControllerValidation() throws Exception {
        AuthResponse adminAuth = createAdminAndLogin("admin-endpoints@example.com");

        for (Map.Entry<String, String> endpoint : adminOnlyPayloads().entrySet()) {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url(endpoint.getKey())))
                    .header("Authorization", "Bearer " + adminAuth.getAccessToken())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(endpoint.getValue()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            assertTrue(response.statusCode() != 401 && response.statusCode() != 403, endpoint.getKey());
        }
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    private HttpRequest getWithBearer(String path, String token) {
        return HttpRequest.newBuilder(URI.create(url(path)))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
    }

    private AuthResponse createAdminAndLogin(String email) {
        User user = new User();
        user.setFirstName("Admin");
        user.setLastName("User");
        user.setEmail(email);
        user.setPhoneNumber("1234567890");
        user.setPasswordHash(passwordEncoder.encode("password"));
        user.setRole(UserRole.ADMIN);
        userRepository.save(user);

        com.fashionstore.dto.request.LoginRequest loginRequest = new com.fashionstore.dto.request.LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword("password");
        return authService.login(loginRequest);
    }

    private Map<String, String> adminOnlyPayloads() {
        return Map.of(
                "/items", "{\"name\":\"Test Item\",\"price\":10.0,\"description\":\"Valid test description\",\"audience\":\"MEN\",\"categoryId\":1}",
                "/sizes", "{\"label\":\"XL\",\"sizeSystem\":\"US\"}",
                "/api/categories", "{\"name\":\"Test Category\"}",
                "/api/colors", "{\"name\":\"Black\",\"value\":\"#000000\",\"imageUrl\":\"https://example.com/black.png\"}"
        );
    }

    private void assertStatusIn(int actual, int... expected) {
        for (int status : expected) {
            if (actual == status) {
                return;
            }
        }
        throw new AssertionError("Expected one of " + java.util.Arrays.toString(expected) + " but was " + actual);
    }

    private RegisterRequest registerRequest(String email) {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("Test");
        request.setLastName("User");
        request.setEmail(email);
        request.setPhoneNumber("1234567890");
        request.setPassword("password");
        return request;
    }
}
