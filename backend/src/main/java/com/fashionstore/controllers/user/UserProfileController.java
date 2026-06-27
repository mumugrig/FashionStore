package com.fashionstore.controllers.user;

import com.fashionstore.dto.request.ProfileUpdateRequest;
import com.fashionstore.dto.response.UserResponse;
import com.fashionstore.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class UserProfileController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getAuthenticatedUser(Authentication authentication) {
        return ResponseEntity.ok(userService.getAuthenticatedUser(authentication));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateAuthenticatedUser(Authentication authentication, @Valid @RequestBody ProfileUpdateRequest profileUpdateRequest) {
        return ResponseEntity.ok(userService.updateAuthenticatedUser(authentication, profileUpdateRequest));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteAuthenticatedUser(Authentication authentication) {
        userService.deleteAuthenticatedUser(authentication);
        return ResponseEntity.noContent().build();
    }
}
