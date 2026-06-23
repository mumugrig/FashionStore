package com.fashionstore.controllers;

import com.fashionstore.dto.request.UserRequest;
import com.fashionstore.dto.response.UserResponse;
import com.fashionstore.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

//    @GetMapping("/me")
//    public ResponseEntity<UserResponse> getAuthenticatedUser() {
//        UserResponse userResponse = userService.getAuthenticatedUser();
//        return ResponseEntity.ok(userResponse);
//    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse userResponse = userService.getUserById(id);
        return ResponseEntity.ok(userResponse);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserRequest userRequest) {
        UserResponse updatedUser = userService.updateUser(id, userRequest);
        return ResponseEntity.ok(updatedUser);
    }

//    @PatchMapping("/me")
//    public ResponseEntity<UserResponse> updateAuthenticatedUser(@RequestBody UserRequest userRequest) {
//        UserResponse updatedUser = userService.updateAuthenticatedUser(userRequest);
//        return ResponseEntity.ok(updatedUser);
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
