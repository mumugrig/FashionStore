package com.fashionstore.services;

import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.exceptions.ConflictException;
import com.fashionstore.exceptions.ValidationException;
import com.fashionstore.models.User;
import com.fashionstore.dto.request.ProfileUpdateRequest;
import com.fashionstore.dto.request.UserRequest;
import com.fashionstore.dto.response.AdminUserResponse;
import com.fashionstore.dto.response.PageResponse;
import com.fashionstore.dto.response.UserResponse;
import com.fashionstore.repositories.UserRepository;
import com.fashionstore.vo.UserRole;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public UserResponse createUser(UserRequest userRequest) {
        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new ConflictException("Email is already registered");
        }

        User user = new User();
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        user.setPasswordHash(passwordEncoder.encode(userRequest.getPassword()));
        user.setRole(UserRole.USER);

        User savedUser = userRepository.save(user);
        return UserResponse.from(savedUser);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            ensureEmailAvailableForUser(userRequest.getEmail(), user.getId());
            user.setFirstName(userRequest.getFirstName());
            user.setLastName(userRequest.getLastName());
            user.setEmail(userRequest.getEmail());
            user.setPhoneNumber(userRequest.getPhoneNumber());
            user.setPasswordHash(passwordEncoder.encode(userRequest.getPassword()));

            User updatedUser = userRepository.save(user);
            return UserResponse.from(updatedUser);
        }
        throw new NotFoundException("User", id);
    }

    @Transactional(readOnly = true)
    public UserResponse getAuthenticatedUser(Authentication authentication) {
        return UserResponse.from(currentUserService.findCurrentUser(authentication));
    }

    @Transactional
    public UserResponse updateAuthenticatedUser(Authentication authentication, ProfileUpdateRequest userRequest) {
        User user = currentUserService.findCurrentUser(authentication);
        ensureEmailAvailableForUser(userRequest.getEmail(), user.getId());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        if (hasText(userRequest.getNewPassword())) {
            if (!hasText(userRequest.getCurrentPassword())) {
                throw new ValidationException("Current password is required to set a new password");
            }
            if (!passwordEncoder.matches(userRequest.getCurrentPassword(), user.getPasswordHash())) {
                throw new ValidationException("Current password is incorrect");
            }
            user.setPasswordHash(passwordEncoder.encode(userRequest.getNewPassword()));
        }
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.map(UserResponse::from).orElseThrow(() -> new NotFoundException("User", id));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getPagedUsers(int page, int size, String search, String filterColumn, String filterValue) {
        if (!AdminFilterSpecification.hasFilters(search, filterColumn, filterValue)) {
            return PageResponse.from(userRepository.findAll(PageRequestFactory.create(page, size)), UserResponse::from);
        }
        return PageResponse.from(userRepository.findAll(
                AdminFilterSpecification.create(adminFields(), search, filterColumn, filterValue),
                PageRequestFactory.create(page, size)
        ), UserResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminUserResponse> getPagedAdminUsers(int page, int size, String search, String filterColumn, String filterValue) {
        if (!AdminFilterSpecification.hasFilters(search, filterColumn, filterValue)) {
            return PageResponse.from(userRepository.findAll(PageRequestFactory.create(page, size)), AdminUserResponse::from);
        }
        return PageResponse.from(userRepository.findAll(
                AdminFilterSpecification.create(adminFields(), search, filterColumn, filterValue),
                PageRequestFactory.create(page, size)
        ), AdminUserResponse::from);
    }

    @Transactional(readOnly = true)
    public Optional<UserResponse> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserResponse::from);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User", id));
        userRepository.delete(user);
    }

    @Transactional
    public void deleteUsers(List<Long> ids) {
        ids.forEach(this::deleteUser);
    }

    @Transactional
    public void deleteAuthenticatedUser(Authentication authentication) {
        User user = currentUserService.findCurrentUser(authentication);
        userRepository.delete(user);
    }

    private void ensureEmailAvailableForUser(String email, Long userId) {
        userRepository.findByEmail(email)
                .filter(existingUser -> !existingUser.getId().equals(userId))
                .ifPresent(existingUser -> {
                    throw new ConflictException("Email is already registered");
                });
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private Map<String, Function<Root<User>, Expression<?>>> adminFields() {
        return Map.ofEntries(
                Map.entry("id", root -> root.get("id")),
                Map.entry("firstName", root -> root.get("firstName")),
                Map.entry("lastName", root -> root.get("lastName")),
                Map.entry("email", root -> root.get("email")),
                Map.entry("phoneNumber", root -> root.get("phoneNumber")),
                Map.entry("role", root -> root.get("role"))
        );
    }
}
