package com.fashionstore.security;

import com.fashionstore.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class JwtService {
    private final JwtEncoder jwtEncoder;
    private final Duration accessTokenTtl;

    public JwtService(JwtEncoder jwtEncoder,
                      @Value("${app.jwt.access-token-ttl-minutes:15}") long accessTokenTtlMinutes) {
        this.jwtEncoder = jwtEncoder;
        this.accessTokenTtl = Duration.ofMinutes(accessTokenTtlMinutes);
    }

    public TokenResult createAccessToken(User user) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(accessTokenTtl);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("fashionstore")
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("roles", List.of("ROLE_" + user.getRole().name()))
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new TokenResult(token, expiresAt);
    }

    public record TokenResult(String token, Instant expiresAt) {
    }
}
