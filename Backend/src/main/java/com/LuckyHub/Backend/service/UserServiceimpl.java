package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.entity.*;
import com.LuckyHub.Backend.event.ResendVerificationTokenEvent;
import com.LuckyHub.Backend.exception.ImageUploadFailedException;
import com.LuckyHub.Backend.exception.InvalidCurrentPasswordException;
import com.LuckyHub.Backend.exception.PasswordMismatchException;
import com.LuckyHub.Backend.exception.UserNotFoundException;
import com.LuckyHub.Backend.model.*;
import com.LuckyHub.Backend.repository.PasswordTokenRepository;
import com.LuckyHub.Backend.repository.UserRepository;
import com.LuckyHub.Backend.repository.VerificationTokenRepository;
import com.LuckyHub.Backend.utils.VerificationTokenUtil;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceimpl implements UserService{

    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final ApplicationEventPublisher publisher;
    private final PasswordTokenRepository passwordTokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final PaymentService paymentService;
    private final CacheManager cacheManager;
    private final Cloudinary cloudinary;

    public User save(UserModel userModel){
         Optional<User> optUser = userRepository.findByEmail(userModel.getEmail());

         if(optUser.isPresent()) return null;

        Subscription subscription = Subscription.builder()
                                    .subscriptionType(SubscriptionTypes.FREE)
                                    .status(SubscriptionStatus.NONE)
                                    .maxComments(SubscriptionTypes.FREE.getMaxComments())
                                    .maxWinners(SubscriptionTypes.FREE.getMaxWinners())
                                    .remainingGiveaways(SubscriptionTypes.FREE.getMaxGiveaways())
                                    .startDate(new Date())
                                    .expiringDate(null)
                                    .paymentId(null)
                                    .build();

        User user  = new User();
        user.setFirstName(userModel.getFirstName());
        user.setLastName(userModel.getLastName());
        user.setEmail(userModel.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(userModel.getPassword()));
        user.setVerified(false);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setSubscription(subscription);

        user.setAvatarUrl("https://cdn-icons-png.flaticon.com/512/4712/4712109.png");
        user.setWinnersSelectedThisMonth(0);

        subscription.setUser(user);

        userRepository.save(user);

        return user;
    }

    @Override
    public Map<String, Object> verifyLogin(UserModel userModel) {
        // Authenticate user
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userModel.getEmail(),
                        userModel.getPassword()
                )
        );

        // Load the User entity from DB
        User user = userRepository.findByEmail(userModel.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Generate JWT using the User entity
        String accessToken = jwtService.generateToken(user);

        refreshTokenService.deleteByUserId(user.getId());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());
        user.setUpdatedAt(new Date());
        long expiresIn = 10 * 60;

        return Map.of(
                "status", "success",
                "accessToken", accessToken,
                "refreshToken", refreshToken.getToken(),
                "expiresIn", expiresIn
        );
    }

    @Override
    public void saveVerificationTokenForUser(User user, String token) {
        VerificationToken existingToken = verificationTokenRepository.findByUser(user);

        if (existingToken != null) {
            // Update existing token and expiration time
            existingToken.setToken(token);
            existingToken.setExpirationTime(VerificationTokenUtil.calculateExpirationTime());
            verificationTokenRepository.save(existingToken);
        } else {
            // Create a new verification token for first-time signup
            VerificationToken newToken = new VerificationToken(user, token, new Date());
            newToken.setExpirationTime(VerificationTokenUtil.calculateExpirationTime());
            verificationTokenRepository.save(newToken);
        }
    }

    @Override
    public String verifyVerificationToken(String token) {
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

    @Override
    public ResponseEntity<?> resendVerifyToken(String oldToken, final HttpServletRequest request, String url, User user) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(oldToken);

        if(verificationToken == null){
            return ResponseEntity
                    .badRequest()
                    .body(
                        Map.of(
                            "Error",
                            "Invalid Old Token , Try Sign up again !"
                        )
                    );
        }

        // Check resend limit
        if(verificationToken.getResendCount() >= 3){
        return ResponseEntity
                .badRequest()
                .body(
                    Map.of(
                    "Error","You’ve exceeded maximum resends. Please sign up again."
                    )
                );
        }

        // Calculate time difference
        long diffInMillis = new Date().getTime() - verificationToken.getLastTokenSendTime().getTime();
        long diffInMinutes = diffInMillis / (1000 * 60);
        if (diffInMinutes < 15 && verificationToken.getResendCount() > 1) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "Error",
                            "Requested too early! Wait at least 15 minutes before requesting a new verification email."
                    ));
        }

        // Update resend count and last send time
        verificationToken.setResendCount(verificationToken.getResendCount() + 1);
        verificationToken.setLastTokenSendTime(new Date());
        verificationTokenRepository.save(verificationToken);

        String token = UUID.randomUUID().toString();

        // resending Email using the Event and Event Listener
        publisher.publishEvent(new ResendVerificationTokenEvent(
                user,
                url,
                token
        ));

        return ResponseEntity.ok(Map.of(
                "message", "Verification link resent to your email",
                "token", token
        ));
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void createResetPasswordToken(User user, String token) {
        // Delete old token if exists
        PasswordToken existingToken = passwordTokenRepository.findByUser(user);
        if (existingToken != null) {
            passwordTokenRepository.delete(existingToken);
        }
        //Save token
        PasswordToken passwordToken = new PasswordToken(user, token);
        passwordTokenRepository.save(passwordToken);
    }


    @Override
    public String GeneratePasswordResetURL(String url, String token) {
        return url
                + "/reset-password?token="
                + token;
    }

    @Override
    public String validatePasswordToken(String token) {
        PasswordToken  passwordToken = passwordTokenRepository.findByToken(token);

        if(passwordToken == null){
            return "Invalid Token !";
        }

        Calendar calendar = Calendar.getInstance();

        if (passwordToken.getExpirationTime().before(calendar.getTime())) {
            System.out.println("Verification token is Expired: " + passwordToken);
            passwordTokenRepository.delete(passwordToken);
            return "Token Expired !";
        }

        return "Valid";
    }

    @Override
    public Optional<User> getUserByPasswordResetToken(String token) {
        return Optional.ofNullable(passwordTokenRepository.findByToken(token).getUser());
    }
    @Override
    public void changePassword(User user, String newPassword) {
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    @Cacheable(value = "dashboardCache", key = "#email")
    public Map<String, Object> getCurrentUserFromToken(String token, String email) {
        // 1. Fetch user from DB (optional: ensures latest data)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // 2. Prepare response map
        Map<String, Object> response = new HashMap<>();

        Subscription sub = user.getSubscription();

        response.put("email", user.getEmail());
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("avatarUrl", user.getAvatarUrl());

        response.put("subscriptionType", sub.getSubscriptionType().name());
        response.put("subscriptionStatus", sub.getStatus().name());

        response.put("createdAt", user.getCreatedAt());
        response.put("winnersSelectedThisMonth", user.getWinnersSelectedThisMonth());

        response.put("maxGiveaways", sub.getSubscriptionType().getMaxGiveaways());
        response.put("remainingGiveaways", sub.getRemainingGiveaways());
        response.put("maxComments", sub.getMaxComments());
        response.put("maxWinners", sub.getMaxWinners());

        Date expiryDate = sub.getExpiringDate();
        if(expiryDate != null){
            response.put("subscriptionExpiryDate", sub.getExpiringDate().getTime());
            boolean isExpired = sub.getExpiringDate().before(new Date());
            response.put("isSubscriptionExpired", isExpired);
        }
        return response;
    }

    @Override
    public Long findUserIdByEmail(String email) {
        return userRepository.findIdByEmail(email);

    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findUserById(id);
    }

    @Override
    public void upgradeSubscription(String paymentId) {
        // fetching payment data
        Payment payment = paymentService.getPaymentDataToUpgradeService(paymentId);

        Long userId = payment.getUserId();
        SubscriptionTypes planType = payment.getPlanType();

        Date startDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.MONTH, 1); // add 1 month
        Date expiringDate  = cal.getTime();

        SubscriptionStatus status = SubscriptionStatus.ACTIVE;

        Integer maxComments = planType.getMaxComments();
        Integer maxWinners = planType.getMaxWinners();
        Integer remainingGiveaways = planType.getMaxGiveaways();

        Optional<User> optUser = userRepository.findUserById(userId);

        if (optUser.isEmpty())
            throw new UserNotFoundException("optUser not found, unable to upgrade Subscription !");

        User user = optUser.get();
        Subscription subscription = user.getSubscription();

        if (subscription != null) {
            // update existing subscription
            subscription.setSubscriptionType(planType);
            subscription.setStartDate(startDate);
            subscription.setExpiringDate(expiringDate);
            subscription.setStatus(status);
            subscription.setPaymentId(paymentId);
            subscription.setMaxComments(maxComments);
            subscription.setMaxWinners(maxWinners);
            subscription.setRemainingGiveaways(remainingGiveaways);
        } else {
            // create new subscription if user has none
            subscription = Subscription.builder()
                    .subscriptionType(planType)
                    .startDate(startDate)
                    .expiringDate(expiringDate)
                    .status(status)
                    .paymentId(paymentId)
                    .maxComments(maxComments)
                    .maxWinners(maxWinners)
                    .remainingGiveaways(remainingGiveaways)
                    .user(user) // link to user
                    .build();
            user.setSubscription(subscription);
        }

        userRepository.save(user); // updating the user with updated/new subscription
    }

    @Override
    public UserModel convertToUserModel(User user) {
        UserModel userModel = new UserModel();
        userModel.setEmail(user.getEmail());
        userModel.setFirstName(user.getFirstName());
        userModel.setLastName(user.getLastName());
        userModel.setPassword(user.getPassword());
        return userModel;
    }

    @Override
    @Transactional
    public boolean changeUserName(String email, ChangeNameRequest request) {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }

        if (Objects.equals(user.get().getFirstName(), request.getFirstName()) &&
                Objects.equals(user.get().getLastName(), request.getLastName())) {
            return false;
        }

         user.get().setFirstName(request.getFirstName());
         user.get().setLastName(request.getLastName());

        if (cacheManager.getCache("dashboardCache") != null) {
            Objects.requireNonNull(cacheManager.getCache("dashboardCache")).evict(email);
        }

         userRepository.save(user.get());

        return true;
    }

    @Transactional
    @Override
    public void changeAvatar(String email, MultipartFile file) {

        Map<String, Object> upload;
        try {
            upload = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "avatars",
                            "overwrite", true,
                            "resource_type", "image"
                    )
            );
        } catch (IOException e) {
            throw new ImageUploadFailedException("Image uploading failed");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setAvatarUrl((String) upload.get("url"));
        userRepository.save(user);

        if (cacheManager.getCache("dashboardCache") != null) {
            Objects.requireNonNull(cacheManager.getCache("dashboardCache")).evict(email);
        }
    }

    @Override
    @Transactional
    public void updatePassword(String email, ChangePasswordModel model) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));


        if (!model.getNewPassword().equals(model.getConfirmNewPassword())) {
            throw new PasswordMismatchException("New password and confirmation password do not match.");
        }


        if (!bCryptPasswordEncoder.matches(
                model.getCurrentPassword(),
                user.getPassword()
        )) {
            throw new InvalidCurrentPasswordException("Current password is incorrect.");
        }

        user.setPassword(bCryptPasswordEncoder.encode(model.getNewPassword()));
        userRepository.save(user);
    }



    @Override
    public void deletePasswordToken(String token) {
        passwordTokenRepository.deleteByToken(token);
    }
}