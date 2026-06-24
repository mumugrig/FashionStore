package com.fashionstore.repositories;

import com.fashionstore.models.RefreshToken;
import com.fashionstore.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RefreshTokenRepositoryTest {
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByTokenHashReturnsStoredToken() {
        User user = userRepository.save(user("refresh@example.com"));
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash("hash-value");
        refreshToken.setExpiresAt(Instant.now().plusSeconds(3600));
        refreshTokenRepository.save(refreshToken);

        RefreshToken found = refreshTokenRepository.findByTokenHash("hash-value").orElseThrow();

        assertEquals(user.getId(), found.getUser().getId());
        assertTrue(found.isActive());
    }

    @Test
    void findByTokenHashReturnsEmptyForMissingToken() {
        assertTrue(refreshTokenRepository.findByTokenHash("missing").isEmpty());
    }

    @Test
    void deleteByUserIdRemovesStoredTokens() {
        User user = userRepository.save(user("delete-refresh@example.com"));
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash("delete-hash-value");
        refreshToken.setExpiresAt(Instant.now().plusSeconds(3600));
        refreshTokenRepository.save(refreshToken);

        refreshTokenRepository.deleteByUserId(user.getId());

        assertTrue(refreshTokenRepository.findByTokenHash("delete-hash-value").isEmpty());
    }

    private User user(String email) {
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail(email);
        user.setPhoneNumber("1234567890");
        user.setPasswordHash("hashedPassword");
        return user;
    }
}
