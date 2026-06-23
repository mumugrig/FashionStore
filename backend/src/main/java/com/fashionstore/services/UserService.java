package com.fashionstore.services;

import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.models.User;
import com.fashionstore.dto.request.UserRequest;
import com.fashionstore.dto.response.UserResponse;
import com.fashionstore.repositories.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse createUser(UserRequest userRequest) {
        User user = new User();
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        user.setPasswordHash(userRequest.getPassword());

        User savedUser = userRepository.save(user);
        return UserResponse.from(savedUser);
    }

    public UserResponse updateUser(Long id, UserRequest userRequest) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setFirstName(userRequest.getFirstName());
            user.setLastName(userRequest.getLastName());
            user.setEmail(userRequest.getEmail());
            user.setPhoneNumber(userRequest.getPhoneNumber());
            user.setPasswordHash(userRequest.getPassword());

            User updatedUser = userRepository.save(user);
            return UserResponse.from(updatedUser);
        }
        throw new NotFoundException("User", id);
    }

    public UserResponse getUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.map(UserResponse::from).orElseThrow(() -> new NotFoundException("User", id));
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    public Optional<UserResponse> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserResponse::from);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}

