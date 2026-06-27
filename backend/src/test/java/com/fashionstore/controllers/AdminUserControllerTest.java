package com.fashionstore.controllers;

import com.fashionstore.controllers.admin.AdminUserController;
import com.fashionstore.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUserControllerTest extends ControllerTestSupport {
    @Mock private UserService userServiceMock;
    private AdminUserController objectUnderTest;

    @BeforeEach
    void setUp() {
        objectUnderTest = new AdminUserController(userServiceMock);
    }

    @Test
    void getAllUsers_returnsUsersFromAdminServiceMethod() {
        when(userServiceMock.getPagedAdminUsers(1, 20, null, null, null)).thenReturn(pageResponse(adminUserResponse(1L, "admin-user@example.com")));

        var response = objectUnderTest.getAllUsers(1, 20, null, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals("USER", response.getBody().getContent().get(0).getRole());
    }

    @Test
    void getUserById_returnsRequestedUser() {
        when(userServiceMock.getUserById(1L)).thenReturn(userResponse(1L, "admin-user@example.com"));

        var response = objectUnderTest.getUserById(1L);

        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void updateUser_callsUnrestrictedAdminUpdateMethod() {
        when(userServiceMock.updateUser(eq(1L), any()))
                .thenReturn(userResponse(1L, "updated-admin-user@example.com"));

        var response = objectUnderTest.updateUser(1L, userRequest("updated-admin-user@example.com", "Updated"));

        assertEquals("updated-admin-user@example.com", response.getBody().getEmail());
    }

    @Test
    void deleteUser_callsUnrestrictedAdminDeleteMethod() {
        var response = objectUnderTest.deleteUser(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userServiceMock).deleteUser(1L);
    }
}
