package com.fashionstore.services;

import com.fashionstore.dto.request.UserRequest;
import com.fashionstore.dto.response.UserResponse;
import com.fashionstore.exceptions.ConflictException;
import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.models.User;
import com.fashionstore.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

@ExtendWith(MockitoExtension.class)
class UserServiceTest extends ServiceTestSupport {
    @Mock private UserRepository userRepositoryMock;
    @Mock private CurrentUserService currentUserServiceMock;
    @Mock private PasswordEncoder passwordEncoderMock;
    private UserService objectUnderTest;

    @BeforeEach
    void setUp() {
        objectUnderTest = new UserService(userRepositoryMock, currentUserServiceMock, passwordEncoderMock);
    }

    @Test
    void createUser_whenEmailIsNew_returnsCreatedUser() {
        User savedUser = user(1L, "service-user@example.com");
        savedUser.setFirstName("Service");
        savedUser.setPasswordHash("encoded-password");
        when(userRepositoryMock.findByEmail("service-user@example.com")).thenReturn(Optional.empty());
        when(passwordEncoderMock.encode("password")).thenReturn("encoded-password");
        when(userRepositoryMock.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = objectUnderTest.createUser(userRequest("service-user@example.com", "Service"));

        assertEquals("Service", response.getFirstName(), "Created user first name should match saved entity");
        assertEquals("service-user@example.com", response.getEmail(), "Created user email should match saved entity");
        assertTrue(response.getId() > 0, "Created user should expose a positive id");
    }

    @Test
    void updateUser_whenUserExists_returnsUpdatedUser() {
        User existing = user(1L, "service-user@example.com");
        User updatedUser = user(1L, "service-user-updated@example.com");
        updatedUser.setFirstName("Updated");
        when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepositoryMock.findByEmail("service-user-updated@example.com")).thenReturn(Optional.empty());
        when(passwordEncoderMock.encode("password")).thenReturn("encoded-password");
        when(userRepositoryMock.save(existing)).thenReturn(updatedUser);

        UserRequest updateRequest = userRequest("service-user-updated@example.com", "Updated");
        UserResponse response = objectUnderTest.updateUser(1L, updateRequest);

        assertEquals("Updated", response.getFirstName(), "Updated user first name should match saved entity");
        assertEquals("service-user-updated@example.com", response.getEmail(), "Updated user email should match saved entity");
    }

    @Test
    void updateUser_whenEmailBelongsToAnotherUser_throwsConflictException() {
        User existing = user(1L, "service-user@example.com");
        User otherUser = user(2L, "taken@example.com");

        when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepositoryMock.findByEmail("taken@example.com")).thenReturn(Optional.of(otherUser));

        assertThrows(ConflictException.class, () -> objectUnderTest.updateUser(1L, userRequest("taken@example.com", "Updated")));
    }

    @Test
    void updateUser_whenEmailBelongsToSameUser_returnsUpdatedUser() {
        User existing = user(1L, "service-user@example.com");
        User updatedUser = user(1L, "service-user@example.com");
        updatedUser.setFirstName("Updated");

        when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepositoryMock.findByEmail("service-user@example.com")).thenReturn(Optional.of(existing));
        when(passwordEncoderMock.encode("password")).thenReturn("encoded-password");
        when(userRepositoryMock.save(existing)).thenReturn(updatedUser);

        UserResponse response = objectUnderTest.updateUser(1L, userRequest("service-user@example.com", "Updated"));

        assertEquals("Updated", response.getFirstName(), "Same-user email update should be allowed");
    }

    @Test
    void getUserById_whenUserIsMissing_throwsNotFoundException() {
        when(userRepositoryMock.findById(99999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> objectUnderTest.getUserById(99999L));
    }

    @Test
    void getAuthenticatedUser_whenPrincipalEmailExists_returnsCurrentUser() {
        User currentUser = user(1L, "me-service@example.com");
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("me-service@example.com", null);

        when(currentUserServiceMock.findCurrentUser(authentication)).thenReturn(currentUser);

        UserResponse response = objectUnderTest.getAuthenticatedUser(authentication);

        assertEquals(currentUser.getId(), response.getId(), "Authenticated user id should match principal email lookup");
    }

    @Test
    void updateAuthenticatedUser_whenPrincipalEmailExists_returnsUpdatedUser() {
        User currentUser = user(1L, "me-service@example.com");
        User updatedUser = user(1L, "me-service-updated@example.com");
        updatedUser.setFirstName("Mine");
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("me-service@example.com", null);

        when(currentUserServiceMock.findCurrentUser(authentication)).thenReturn(currentUser);
        when(userRepositoryMock.findByEmail("me-service-updated@example.com")).thenReturn(Optional.empty());
        when(passwordEncoderMock.encode("password")).thenReturn("encoded-password");
        when(userRepositoryMock.save(currentUser)).thenReturn(updatedUser);

        UserResponse response = objectUnderTest.updateAuthenticatedUser(authentication, userRequest("me-service-updated@example.com", "Mine"));

        assertEquals("Mine", response.getFirstName(), "Authenticated user update should return saved first name");
    }

    @Test
    void updateAuthenticatedUser_whenEmailBelongsToAnotherUser_throwsConflictException() {
        User currentUser = user(1L, "me-service@example.com");
        User otherUser = user(2L, "taken@example.com");
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("me-service@example.com", null);

        when(currentUserServiceMock.findCurrentUser(authentication)).thenReturn(currentUser);
        when(userRepositoryMock.findByEmail("taken@example.com")).thenReturn(Optional.of(otherUser));

        assertThrows(ConflictException.class,
                () -> objectUnderTest.updateAuthenticatedUser(authentication, userRequest("taken@example.com", "Mine")));
    }

    @Test
    void deleteUser_whenUserExists_deletesUserEntityAndLetsCascadeRemoveOwnedData() {
        User user = user(1L, "delete-me@example.com");
        when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(user));

        objectUnderTest.deleteUser(1L);

        verify(userRepositoryMock).delete(user);
    }
}
