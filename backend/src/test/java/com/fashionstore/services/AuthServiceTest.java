package com.fashionstore.services;

import com.fashionstore.dto.request.LoginRequest;
import com.fashionstore.dto.request.RefreshTokenRequest;
import com.fashionstore.dto.request.RegisterRequest;
import com.fashionstore.dto.response.AuthResponse;
import com.fashionstore.exceptions.ConflictException;
import com.fashionstore.exceptions.ValidationException;
import com.fashionstore.models.RefreshToken;
import com.fashionstore.models.User;
import com.fashionstore.repositories.RefreshTokenRepository;
import com.fashionstore.repositories.UserRepository;
import com.fashionstore.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest extends ServiceTestSupport {
    @Mock private UserRepository userRepositoryMock;
    @Mock private RefreshTokenRepository refreshTokenRepositoryMock;
    @Mock private PasswordEncoder passwordEncoderMock;
    @Mock private AuthenticationManager authenticationManagerMock;
    @Mock private JwtService jwtServiceMock;

    @Test
    void register_whenEmailIsNew_returnsTokensAndCreatedUser() {
        AuthService authService = authService();
        User savedUser = user(1L, "new@example.com");
        savedUser.setPasswordHash("encoded-password");
        when(userRepositoryMock.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoderMock.encode("password")).thenReturn("encoded-password");
        when(userRepositoryMock.save(any(User.class))).thenReturn(savedUser);
        when(jwtServiceMock.createAccessToken(savedUser)).thenReturn(tokenResult());

        AuthResponse response = authService.register(registerRequest("new@example.com"));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepositoryMock).save(userCaptor.capture());
        assertEquals("encoded-password", userCaptor.getValue().getPasswordHash(), "Registered user should store encoded password");
        assertNotEquals("password", userCaptor.getValue().getPasswordHash(), "Registered user should not store raw password");
        assertEquals("access-token", response.getAccessToken(), "Registration should return access token");
        assertNotNull(response.getRefreshToken(), "Registration should return refresh token");
        assertEquals("Bearer", response.getTokenType(), "Registration should return bearer token type");
        assertEquals("new@example.com", response.getUser().getEmail(), "Registration response should include created user email");
    }

    @Test
    void register_whenEmailExists_throwsConflictException() {
        AuthService authService = authService();
        when(userRepositoryMock.findByEmail("duplicate@example.com")).thenReturn(Optional.of(user(1L, "duplicate@example.com")));

        assertThrows(ConflictException.class, () -> authService.register(registerRequest("duplicate@example.com")));
    }

    @Test
    void login_whenCredentialsAreValid_returnsTokens() {
        AuthService authService = authService();
        User user = user(1L, "login@example.com");
        when(userRepositoryMock.findByEmail("login@example.com")).thenReturn(Optional.of(user));
        when(jwtServiceMock.createAccessToken(user)).thenReturn(tokenResult());

        AuthResponse response = authService.login(loginRequest("login@example.com", "password"));

        verify(authenticationManagerMock).authenticate(any());
        assertEquals("access-token", response.getAccessToken(), "Login should return access token");
        assertNotNull(response.getRefreshToken(), "Login should return refresh token");
    }

    @Test
    void login_whenCredentialsAreInvalid_throwsAuthenticationException() {
        AuthService authService = authService();
        when(authenticationManagerMock.authenticate(any())).thenThrow(new BadCredentialsException("bad"));

        assertThrows(AuthenticationException.class, () -> authService.login(loginRequest("bad-login@example.com", "wrong-password")));
    }

    @Test
    void refresh_whenRefreshTokenIsValid_rotatesRefreshToken() {
        AuthService authService = authService();
        User user = user(1L, "refresh@example.com");
        RefreshToken currentToken = refreshToken(user, "refresh-token", null);
        when(refreshTokenRepositoryMock.findByTokenHash(hashToken("refresh-token"))).thenReturn(Optional.of(currentToken));
        when(jwtServiceMock.createAccessToken(user)).thenReturn(tokenResult());

        AuthResponse refreshed = authService.refresh(refreshTokenRequest("refresh-token"));

        assertNotNull(refreshed.getAccessToken(), "Refresh should return a new access token");
        assertNotEquals("refresh-token", refreshed.getRefreshToken(), "Refresh should rotate the refresh token");
        assertNotNull(currentToken.getRevokedAt(), "Refresh should revoke the old refresh token");
        verify(refreshTokenRepositoryMock).save(currentToken);
    }

    @Test
    void refresh_whenRefreshTokenIsRevoked_throwsValidationException() {
        AuthService authService = authService();
        RefreshToken revokedToken = refreshToken(user(1L, "refresh@example.com"), "refresh-token", Instant.now());
        when(refreshTokenRepositoryMock.findByTokenHash(hashToken("refresh-token"))).thenReturn(Optional.of(revokedToken));

        assertThrows(ValidationException.class, () -> authService.refresh(refreshTokenRequest("refresh-token")));
    }

    @Test
    void logout_whenRefreshTokenExists_revokesRefreshToken() {
        AuthService authService = authService();
        RefreshToken token = refreshToken(user(1L, "logout@example.com"), "refresh-token", null);
        when(refreshTokenRepositoryMock.findByTokenHash(hashToken("refresh-token"))).thenReturn(Optional.of(token));

        authService.logout(refreshTokenRequest("refresh-token"));

        assertTrue(token.getRevokedAt() != null, "Logout should revoke the refresh token");
        verify(refreshTokenRepositoryMock).save(token);
    }

    private AuthService authService() {
        return new AuthService(userRepositoryMock, refreshTokenRepositoryMock, passwordEncoderMock,
                authenticationManagerMock, jwtServiceMock);
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

    private LoginRequest loginRequest(String email, String password) {
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);
        return request;
    }

    private RefreshTokenRequest refreshTokenRequest(String token) {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(token);
        return request;
    }

    private RefreshToken refreshToken(User user, String token, Instant revokedAt) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(1L);
        refreshToken.setUser(user);
        refreshToken.setTokenHash(hashToken(token));
        refreshToken.setExpiresAt(Instant.now().plusSeconds(3600));
        refreshToken.setRevokedAt(revokedAt);
        return refreshToken;
    }

    private JwtService.TokenResult tokenResult() {
        return new JwtService.TokenResult("access-token", Instant.now().plusSeconds(900));
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
