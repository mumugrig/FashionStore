package com.fashionstore.services;

import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.exceptions.ConflictException;
import com.fashionstore.models.User;
import com.fashionstore.dto.request.UserRequest;
import com.fashionstore.dto.response.UserResponse;
import com.fashionstore.repositories.AddressRepository;
import com.fashionstore.repositories.CartItemRepository;
import com.fashionstore.repositories.FavoriteRepository;
import com.fashionstore.repositories.RefreshTokenRepository;
import com.fashionstore.repositories.ReviewRepository;
import com.fashionstore.repositories.UserRepository;
import com.fashionstore.vo.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AddressRepository addressRepository;
    private final CartItemRepository cartItemRepository;
    private final FavoriteRepository favoriteRepository;
    private final ReviewRepository reviewRepository;


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
    public UserResponse updateAuthenticatedUser(Authentication authentication, UserRequest userRequest) {
        User user = currentUserService.findCurrentUser(authentication);
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        user.setPasswordHash(passwordEncoder.encode(userRequest.getPassword()));
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
    public Optional<UserResponse> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserResponse::from);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User", id);
        }
        refreshTokenRepository.deleteByUserId(id);
        addressRepository.deleteByUserId(id);
        cartItemRepository.deleteByUserId(id);
        favoriteRepository.deleteByUserId(id);
        reviewRepository.deleteByUserId(id);
        userRepository.deleteById(id);
    }


}
