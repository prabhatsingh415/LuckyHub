package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.RefreshToken;
import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.exception.RefreshTokenExpiredException;
import com.LuckyHub.Backend.exception.UserNotFoundException;
import com.LuckyHub.Backend.repository.RefreshTokenRepository;
import com.LuckyHub.Backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public RefreshToken createRefreshToken(String email){
        Optional<User> user = userRepository.findByEmail(email);

        if(user.isEmpty()){
            throw new UserNotFoundException("User not found. Please log in again.");
        }

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user.get());
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plus(14, ChronoUnit.DAYS));

        return repository.save(refreshToken);
    }
    public Optional<RefreshToken> findByToken(String token) {
        return repository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token){
        if (token == null) {
            throw new RefreshTokenExpiredException("Invalid refresh token.");
        }
        if(token.getExpiryDate().isBefore(Instant.now())){
             repository.delete(token);
            throw new RefreshTokenExpiredException("Refresh token expired. Please log in again.");
        }
        return token;
    }

    public int deleteByUserId(Long userId) {
         Optional<User> user = userRepository.findById(userId);
         if(user.isEmpty()){
             throw new UserNotFoundException("Unable to delete the refresh Token!, No user Found");
         }
        return repository.deleteByUser(user.get());
    }
}
