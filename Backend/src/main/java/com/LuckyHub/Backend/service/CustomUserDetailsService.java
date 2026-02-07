package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.CustomUserDetails;
import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                 .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if(Objects.isNull(user)){
            throw new UsernameNotFoundException("No UserName Found !");
        }
        return new CustomUserDetails(user);
    }
}
