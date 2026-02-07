package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.PasswordToken;
import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.repository.PasswordTokenRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class PasswordServiceImpl implements PasswordService{
    private final PasswordTokenRepository repository;

    @Override
    public Optional<PasswordToken> findToken(String token) {
        return repository.findByToken(token);
    }

    @Override
    @Transactional
    public void deletePasswordToken(String token) {
        repository.deleteByToken(token);
    }

    @Override
    @Transactional
    public void savePasswordToken(PasswordToken passwordToken) {
        repository.save(passwordToken);
    }

    @Override
    @Transactional
    public void deletePasswordToken(PasswordToken passwordToken) {
        repository.delete(passwordToken);
    }

    @Override
    public Optional<PasswordToken> findTokenByUser(User user) {
        return repository.findByUser(user);
    }
}
