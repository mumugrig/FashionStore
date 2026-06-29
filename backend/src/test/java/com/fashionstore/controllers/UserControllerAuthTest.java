package com.fashionstore.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fashionstore.dto.request.RegisterRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fashionstore.dto.response.AuthResponse;
import com.fashionstore.models.CartItem;
import com.fashionstore.models.Category;
import com.fashionstore.models.Color;
import com.fashionstore.models.Favorite;
import com.fashionstore.models.Item;
import com.fashionstore.models.ItemVariant;
import com.fashionstore.models.Size;
import com.fashionstore.models.User;
import com.fashionstore.repositories.CartItemRepository;
import com.fashionstore.repositories.CategoryRepository;
import com.fashionstore.repositories.ColorRepository;
import com.fashionstore.repositories.FavoriteRepository;
import com.fashionstore.repositories.ItemRepository;
import com.fashionstore.repositories.ItemVariantRepository;
import com.fashionstore.repositories.SizeRepository;
import com.fashionstore.repositories.UserRepository;
import com.fashionstore.services.AuthService;
import com.fashionstore.vo.Audience;
import com.fashionstore.vo.SizeSystem;
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
import java.math.BigDecimal;
import java.util.List;
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
    private CategoryRepository categoryRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemVariantRepository itemVariantRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void usersMeRequiresAuthentication() throws Exception {
        HttpResponse<String> response = httpClient.send(
                HttpRequest.newBuilder(URI.create(url("/api/users/me"))).GET().build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertStatusIn(response.statusCode(), 401, 403);
    }

    @Test
    void usersMeReturnsCurrentUser() throws Exception {
        AuthResponse authResponse = authService.register(registerRequest("me@example.com"));
        HttpRequest request = HttpRequest.newBuilder(URI.create(url("/api/users/me")))
                .header("Authorization", "Bearer " + authResponse.getAccessToken())
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"email\":\"me@example.com\""));
    }

    @Test
    void registerEndpointCreatesUser() throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url("/api/auth/register")))
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
    void refreshEndpointRotatesRefreshTokenAndReturnsNewAccessToken() throws Exception {
        AuthResponse authResponse = authService.register(registerRequest("refresh-endpoint@example.com"));
        String refreshBody = objectMapper.writeValueAsString(Map.of("refreshToken", authResponse.getRefreshToken()));
        HttpRequest request = HttpRequest.newBuilder(URI.create(url("/api/auth/refresh")))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(refreshBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode body = objectMapper.readTree(response.body());

        assertEquals(200, response.statusCode());
        assertNotNull(body.get("accessToken").asText());
        assertNotNull(body.get("refreshToken").asText());
        assertTrue(!authResponse.getRefreshToken().equals(body.get("refreshToken").asText()));
    }

    @Test
    void logoutRequiresAuthentication() throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url("/api/auth/logout")))
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
                getWithBearer("/api/admin/users/1", userAuth.getAccessToken()),
                HttpResponse.BodyHandlers.ofString()
        );
        HttpResponse<String> adminResponse = httpClient.send(
                getWithBearer("/api/admin/users/1", adminAuth.getAccessToken()),
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

    @Test
    void adminGetEndpointsRejectRegularUsers() throws Exception {
        AuthResponse userAuth = authService.register(registerRequest("regular-admin-get@example.com"));

        for (String endpoint : adminGetEndpoints()) {
            HttpResponse<String> response = httpClient.send(
                    getWithBearer(endpoint, userAuth.getAccessToken()),
                    HttpResponse.BodyHandlers.ofString()
            );

            assertEquals(403, response.statusCode(), endpoint);
        }
    }

    @Test
    void adminGetEndpointsAllowAdminsToReachController() throws Exception {
        AuthResponse adminAuth = createAdminAndLogin("admin-get@example.com");

        for (String endpoint : adminGetEndpoints()) {
            HttpResponse<String> response = httpClient.send(
                    getWithBearer(endpoint, adminAuth.getAccessToken()),
                    HttpResponse.BodyHandlers.ofString()
            );

            assertTrue(response.statusCode() != 401 && response.statusCode() != 403, endpoint);
        }
    }

    @Test
    void catalogGetEndpointsRequireAuthentication() throws Exception {
        for (String endpoint : catalogGetEndpoints()) {
            HttpResponse<String> response = httpClient.send(
                    HttpRequest.newBuilder(URI.create(url(endpoint))).GET().build(),
                    HttpResponse.BodyHandlers.ofString()
            );

            assertStatusIn(response.statusCode(), 401, 403);
        }
    }

    @Test
    void catalogGetEndpointsAllowAuthenticatedUsers() throws Exception {
        AuthResponse userAuth = authService.register(registerRequest("catalog-reader@example.com"));

        for (String endpoint : catalogGetEndpoints()) {
            HttpResponse<String> response = httpClient.send(
                    getWithBearer(endpoint, userAuth.getAccessToken()),
                    HttpResponse.BodyHandlers.ofString()
            );

            assertTrue(response.statusCode() != 401 && response.statusCode() != 403, endpoint);
        }
    }

    @Test
    void itemsEndpointAppliesDocumentedFiltersExceptReviewStars() throws Exception {
        AuthResponse userAuth = authService.register(registerRequest("query-items@example.com"));
        createFilterableVariant();
        HttpRequest request = HttpRequest.newBuilder(URI.create(url(
                "/api/items?category=Shirts&search=Oxford&itemSize=M&color=Navy&audience=men&pricemin=40&pricemax=60")))
                .header("Authorization", "Bearer " + userAuth.getAccessToken())
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"name\":\"Oxford Shirt\""));
        assertTrue(response.body().contains("\"totalElements\":1"));
    }

    @Test
    void userCollectionEndpointsApplySearchFilters() throws Exception {
        AuthResponse userAuth = authService.register(registerRequest("query-owned@example.com"));
        User user = userRepository.findByEmail("query-owned@example.com").orElseThrow();
        ItemVariant variant = createFilterableVariant();

        CartItem cartItem = new CartItem();
        cartItem.setUser(user);
        cartItem.setItemVariant(variant);
        cartItem.setQuantity(1);
        cartItemRepository.save(cartItem);

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setItemVariant(variant);
        favoriteRepository.save(favorite);

        for (String endpoint : List.of("/api/cart?search=shirt", "/api/favorites?search=shirt")) {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url(endpoint)))
                    .header("Authorization", "Bearer " + userAuth.getAccessToken())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode(), endpoint);
            assertTrue(response.body().contains("\"totalElements\":"), endpoint);
        }
    }

    @Test
    void adminUsersEndpointAppliesSearchAndRejectsInvalidFilterColumn() throws Exception {
        authService.register(registerRequest("admin-search-target@example.com"));
        AuthResponse adminAuth = createAdminAndLogin("admin-search@example.com");

        HttpResponse<String> searchResponse = httpClient.send(
                HttpRequest.newBuilder(URI.create(url("/api/admin/users?search=search-target")))
                        .header("Authorization", "Bearer " + adminAuth.getAccessToken())
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        HttpResponse<String> invalidFilterResponse = httpClient.send(
                HttpRequest.newBuilder(URI.create(url("/api/admin/users?filterColumn=unknown&filterValue=target")))
                        .header("Authorization", "Bearer " + adminAuth.getAccessToken())
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(200, searchResponse.statusCode());
        assertTrue(searchResponse.body().contains("admin-search-target@example.com"));
        assertEquals(400, invalidFilterResponse.statusCode());
    }

    @Test
    void adminBulkDeleteEndpointDeletesSelectedRecords() throws Exception {
        AuthResponse adminAuth = createAdminAndLogin("admin-bulk-delete@example.com");
        Category category = new Category();
        category.setName("Bulk Delete Category");
        Category savedCategory = categoryRepository.save(category);

        HttpRequest request = HttpRequest.newBuilder(URI.create(url("/api/admin/categories/bulk-delete")))
                .header("Authorization", "Bearer " + adminAuth.getAccessToken())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(Map.of("ids", List.of(savedCategory.getId())))))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(204, response.statusCode());
        assertTrue(categoryRepository.findById(savedCategory.getId()).isEmpty());
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
                "/api/admin/items", "{\"name\":\"Test Item\",\"price\":10.0,\"description\":\"Valid test description\",\"audience\":\"MEN\",\"categoryId\":1}",
                "/api/admin/sizes", "{\"label\":\"XL\",\"sizeSystem\":\"US\"}",
                "/api/admin/categories", "{\"name\":\"Test Category\"}",
                "/api/admin/colors", "{\"name\":\"Black\",\"value\":\"#000000\",\"imageUrl\":\"https://example.com/black.png\"}"
        );
    }

    private List<String> adminGetEndpoints() {
        return List.of(
                "/api/admin/users",
                "/api/admin/items",
                "/api/admin/categories",
                "/api/admin/colors",
                "/api/admin/sizes",
                "/api/admin/cart",
                "/api/admin/favorites",
                "/api/admin/addresses",
                "/api/admin/reviews"
        );
    }

    private List<String> catalogGetEndpoints() {
        return List.of(
                "/api/items",
                "/api/categories",
                "/api/colors",
                "/api/sizes"
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

    private ItemVariant createFilterableVariant() {
        Category category = new Category();
        category.setName("Shirts");
        categoryRepository.save(category);

        Color color = new Color();
        color.setName("Navy");
        color.setValue("#000080");
        colorRepository.save(color);

        Size size = new Size();
        size.setLabel("M");
        size.setSizeSystem(SizeSystem.ALPHA);
        sizeRepository.save(size);

        Item item = new Item();
        item.setName("Oxford Shirt");
        item.setPrice(BigDecimal.valueOf(49.90));
        item.setDescription("A filterable shirt for query parameter tests");
        item.setAudience(Audience.MEN);
        item.setCategory(category);
        itemRepository.save(item);

        ItemVariant variant = new ItemVariant();
        variant.setItem(item);
        variant.setColor(color);
        variant.setSize(size);
        variant.setActive(true);
        variant.setStockLeft(10);
        return itemVariantRepository.save(variant);
    }
}
