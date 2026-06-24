package com.fashionstore.security;

import com.fashionstore.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class FashionUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public FashionUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        return userRepository.findByEmail(email)
                .map(FashionUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
