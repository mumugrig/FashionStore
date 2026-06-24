package com.fashionstore.services;

import com.fashionstore.dto.request.LoginRequest;
import com.fashionstore.dto.request.RefreshTokenRequest;
import com.fashionstore.dto.request.RegisterRequest;
import com.fashionstore.dto.response.AuthResponse;
import com.fashionstore.dto.response.UserResponse;
import com.fashionstore.exceptions.ConflictException;
import com.fashionstore.exceptions.ValidationException;
import com.fashionstore.models.RefreshToken;
import com.fashionstore.models.User;
import com.fashionstore.repositories.RefreshTokenRepository;
import com.fashionstore.repositories.UserRepository;
import com.fashionstore.security.JwtService;
import com.fashionstore.vo.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final Duration refreshTokenTtl;

    public AuthService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       @Value("${app.jwt.refresh-token-ttl-days:7}") long refreshTokenTtlDays) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenTtl = Duration.ofDays(refreshTokenTtlDays);
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictException("Email is already registered");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER);

        return createAuthResponse(userRepository.save(user));
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ValidationException("Invalid email or password"));
        return createAuthResponse(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken currentToken = refreshTokenRepository.findByTokenHash(hashToken(request.getRefreshToken()))
                .orElseThrow(() -> new ValidationException("Invalid refresh token"));

        if (!currentToken.isActive()) {
            throw new ValidationException("Invalid refresh token");
        }

        currentToken.setRevokedAt(Instant.now());
        refreshTokenRepository.save(currentToken);

        return createAuthResponse(currentToken.getUser());
    }

    @Transactional
    public void logout(RefreshTokenRequest request) {
        refreshTokenRepository.findByTokenHash(hashToken(request.getRefreshToken()))
                .ifPresent(token -> {
                    token.setRevokedAt(Instant.now());
                    refreshTokenRepository.save(token);
                });
    }

    private AuthResponse createAuthResponse(User user) {
        JwtService.TokenResult accessToken = jwtService.createAccessToken(user);
        String refreshToken = UUID.randomUUID() + "." + UUID.randomUUID();

        RefreshToken persistedRefreshToken = new RefreshToken();
        persistedRefreshToken.setUser(user);
        persistedRefreshToken.setTokenHash(hashToken(refreshToken));
        persistedRefreshToken.setExpiresAt(Instant.now().plus(refreshTokenTtl));
        refreshTokenRepository.save(persistedRefreshToken);

        return new AuthResponse(
                accessToken.token(),
                refreshToken,
                "Bearer",
                accessToken.expiresAt(),
                UserResponse.from(user)
        );
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is not available", ex);
        }
    }
}
