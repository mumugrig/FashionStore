package com.fashionstore.services;

import com.fashionstore.dto.request.UserRequest;
import com.fashionstore.dto.response.UserResponse;
import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.models.User;
import com.fashionstore.repositories.AddressRepository;
import com.fashionstore.repositories.CartItemRepository;
import com.fashionstore.repositories.FavoriteRepository;
import com.fashionstore.repositories.RefreshTokenRepository;
import com.fashionstore.repositories.ReviewRepository;
import com.fashionstore.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import org.mockito.InOrder;
import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest extends ServiceTestSupport {
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private AddressRepository addressRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private FavoriteRepository favoriteRepository;
    @Mock private ReviewRepository reviewRepository;
    @InjectMocks private UserService userService;

    @Test
    void createsUsersWithEncodedPassword() {
        User savedUser = user(1L, "service-user@example.com");
        savedUser.setFirstName("Service");
        savedUser.setPasswordHash("encoded-password");
        when(userRepository.findByEmail("service-user@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse created = userService.createUser(userRequest("service-user@example.com", "Service"));

        assertEquals("Service", created.getFirstName());
        assertEquals("service-user@example.com", created.getEmail());
        assertTrue(created.getId() > 0);
    }

    @Test
    void updatesExistingUser() {
        User existing = user(1L, "service-user@example.com");
        User updatedUser = user(1L, "service-user-updated@example.com");
        updatedUser.setFirstName("Updated");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("password")).thenReturn("encoded-password");
        when(userRepository.save(existing)).thenReturn(updatedUser);

        UserRequest updateRequest = userRequest("service-user-updated@example.com", "Updated");
        UserResponse updated = userService.updateUser(1L, updateRequest);

        assertEquals("Updated", updated.getFirstName());
        assertEquals("service-user-updated@example.com", updated.getEmail());
    }

    @Test
    void throwsWhenUserIsMissing() {
        when(userRepository.findById(99999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(99999L));
    }

    @Test
    void usesAuthenticatedPrincipalForMeOperations() {
        User currentUser = user(1L, "me-service@example.com");
        User updatedUser = user(1L, "me-service-updated@example.com");
        updatedUser.setFirstName("Mine");
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("me-service@example.com", null);
        when(userRepository.findByEmail("me-service@example.com")).thenReturn(Optional.of(currentUser));
        when(passwordEncoder.encode("password")).thenReturn("encoded-password");
        when(userRepository.save(currentUser)).thenReturn(updatedUser);

        assertEquals(currentUser.getId(), userService.getAuthenticatedUser(authentication).getId());
        assertEquals("Mine", userService.updateAuthenticatedUser(authentication, userRequest("me-service-updated@example.com", "Mine")).getFirstName());
    }

    @Test
    void deletesUserOwnedDataBeforeDeletingUser() {
        userService.deleteUser(1L);

        InOrder inOrder = inOrder(refreshTokenRepository, addressRepository, cartItemRepository, favoriteRepository, reviewRepository, userRepository);
        inOrder.verify(refreshTokenRepository).deleteByUserId(1L);
        inOrder.verify(addressRepository).deleteByUserId(1L);
        inOrder.verify(cartItemRepository).deleteByUserId(1L);
        inOrder.verify(favoriteRepository).deleteByUserId(1L);
        inOrder.verify(reviewRepository).deleteByUserId(1L);
        inOrder.verify(userRepository).deleteById(1L);
    }
}
