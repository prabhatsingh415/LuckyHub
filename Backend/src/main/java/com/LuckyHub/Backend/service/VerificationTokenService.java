package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.entity.VerificationToken;

import java.util.Optional;


public interface VerificationTokenService {
    User validateAndGetUser(String token);

    Optional<VerificationToken> findVerificationTokenByUser(User user);

    Optional<VerificationToken> findTokenByOldToken(String oldToken);

    void saveToken(VerificationToken verificationToken);

    void deleteByUser(User user);
}


