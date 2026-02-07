package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.PasswordToken;
import com.LuckyHub.Backend.entity.User;

import java.util.Optional;

public interface PasswordService {
    Optional<PasswordToken> findToken(String token);

    void deletePasswordToken(String token);

    void savePasswordToken(PasswordToken passwordToken);

    void deletePasswordToken(PasswordToken passwordToken);

    Optional<PasswordToken> findTokenByUser(User user);
}


