package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.Subscription;
import com.LuckyHub.Backend.entity.User;
import com.LuckyHub.Backend.entity.VerificationToken;
import com.LuckyHub.Backend.model.SubscriptionStatus;
import com.LuckyHub.Backend.model.SubscriptionTypes;
import com.LuckyHub.Backend.model.UserModel;
import com.LuckyHub.Backend.repository.UserRepository;
import com.LuckyHub.Backend.repository.VerificationTokenRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service

public class UserServiceimpl implements UserService{


    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public UserServiceimpl(VerificationTokenRepository verificationTokenRepository, UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, AuthenticationManager authenticationManager, JWTService jwtService) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
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
        user.setPassword(bCryptPasswordEncoder.encode(userModel.getPassword()));
        user.setVerified(false);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setSubscription(subscription);

        subscription.setUser(user);
        userRepository.save(user);

        return user;
    }

    @Override
    public String verifyLogin(UserModel userModel) {
        // Authenticate user
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userModel.getEmail(),
                        userModel.getPassword()
                )
        );

        // Load the User entity from DB
        User user = userRepository.findByEmail(userModel.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate JWT using the User entity
        return jwtService.generateToken(user);
    }

    @Override
    public void saveVerificationTokenForUser(User user, String token) {
        VerificationToken verificationToken = new VerificationToken(user, token);
        verificationTokenRepository.save(verificationToken);
    }  //Saving Verification token

    @Override
    public String verify(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);

        if(verificationToken == null){
            System.out.println("Token is INVALID because its null !");
            return "Invalid Token !";
        }

        Calendar calendar = Calendar.getInstance();

        if (verificationToken.getExpirationTime().before(calendar.getTime())) {
            System.out.println("Verification token is Expired: " + verificationToken);
            verificationTokenRepository.delete(verificationToken);
            return "Token Expired !";
        }

        User user = verificationToken.getUser();
        user.setVerified(true);
        userRepository.save(user);

        return "Valid";
    }  //Signup Verification


}
