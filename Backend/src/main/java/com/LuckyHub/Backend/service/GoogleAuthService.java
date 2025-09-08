package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class GoogleAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    public GoogleAuthService(UserRepository userRepository,
                             PasswordEncoder passwordEncoder,
                             JWTService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // Process Google user info map and return JWT
    public String processUser(Map<String, Object> userInfo) {
        String email = userInfo.get("email").toString();
        String firstName = userInfo.getOrDefault("given_name", "").toString();
        String lastName = userInfo.getOrDefault("family_name", "").toString();

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    System.out.println("Email "+email);
                    newUser.setFirstName(firstName);
                    System.out.println("firstName "+ firstName);
                    newUser.setLastName(lastName);
                    System.out.println("last "+lastName);
                    newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    newUser.setVerified(true);
                    newUser.setCreatedAt(new Date());
                    newUser.setUpdatedAt(new Date());
                    return userRepository.save(newUser);
                });

        // Generate JWT token
        return jwtService.generateToken(user);
    }
}
