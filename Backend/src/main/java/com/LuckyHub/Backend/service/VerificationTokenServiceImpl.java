package com.LuckyHub.Backend.service;


import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.entity.VerificationToken;
import com.LuckyHub.Backend.exception.InvalidTokenException;
import com.LuckyHub.Backend.exception.VerificationTokenExpiredException;
import com.LuckyHub.Backend.repository.VerificationTokenRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class VerificationTokenServiceImpl implements VerificationTokenService {
    private final VerificationTokenRepository repository;

    @Override
    public User validateAndGetUser(String token) {
        VerificationToken vToken = repository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));

        if (vToken.getExpirationTime().before(new Date())) {
            repository.delete(vToken);
            throw new VerificationTokenExpiredException("Token expired");
        }

        return vToken.getUser();
    }

    @Override
    public Optional<VerificationToken> findVerificationTokenByUser(User user) {
        return  repository.findByUser(user);
    }

    @Override
    public Optional<VerificationToken> findTokenByOldToken(String oldToken) {
        return repository.findByToken(oldToken);
    }

    @Override
    @Transactional
    public void saveToken(VerificationToken verificationToken) {
        repository.save(verificationToken);
    }

    @Override
    public void deleteByUser(User user) {
        repository.deleteByUser(user);
    }
}
