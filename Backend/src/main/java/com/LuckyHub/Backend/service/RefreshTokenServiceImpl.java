package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.RefreshToken;
import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.exception.RefreshTokenExpiredException;
import com.LuckyHub.Backend.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository repository;

    @Transactional
    public RefreshToken createRefreshToken(User user){
        Optional<RefreshToken> existingToken = repository.findByUser(user);

        RefreshToken refreshToken;
        if (existingToken.isPresent()) {
            refreshToken = existingToken.get();
        } else {
            refreshToken = new RefreshToken();
            refreshToken.setUser(user);
        }

        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plus(14, ChronoUnit.DAYS));

        return repository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return repository.findByToken(token);
    }

    public void verifyExpiration(RefreshToken token){
        if (token == null) {
            throw new RefreshTokenExpiredException("Invalid refresh token.");
        }
        if(token.getExpiryDate().isBefore(Instant.now())){
             repository.delete(token);
            throw new RefreshTokenExpiredException("Refresh token expired. Please log in again.");
        }
    }

    public void deleteByUser(User user) {
        repository.deleteByUserId(user.getId());
    }

    public void deleteByUserEmail(String email) {
        repository.deleteByUserEmail(email);
    }
}
