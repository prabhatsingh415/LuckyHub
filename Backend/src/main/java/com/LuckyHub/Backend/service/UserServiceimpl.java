package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.Subscription;
import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.entity.VerificationToken;
import com.LuckyHub.Backend.model.SubscriptionStatus;
import com.LuckyHub.Backend.model.SubscriptionTypes;
import com.LuckyHub.Backend.model.UserModel;
import com.LuckyHub.Backend.repository.UserRepository;
import com.LuckyHub.Backend.repository.VerificationTokenRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserServiceimpl implements UserService{

    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;

    public UserServiceimpl(VerificationTokenRepository verificationTokenRepository, UserRepository userRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.userRepository = userRepository;
    }

    public User save(UserModel userModel){
        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(SubscriptionTypes.FREE);
        subscription.setStatus(SubscriptionStatus.NONE);
        subscription.setStartDate(null);
        subscription.setExpiringDate(null);
        subscription.setPaymentId(null);

        User user  = new User();
        user.setFirstName(userModel.getFirstName());
        user.setLastName(userModel.getLastName());
        user.setEmail(userModel.getEmail());
        user.setPassword(userModel.getPassword());
        user.setVerified(false);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setSubscription(subscription);

        subscription.setUser(user);

        userRepository.save(user);

        return user;
    }

    @Override
    public void saveVerificationTokenForUser(User user, String token) {
        VerificationToken verificationToken = new VerificationToken(user, token);
        verificationTokenRepository.save(verificationToken);
    }
}
