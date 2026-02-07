package com.LuckyHub.Backend.repository;

import com.LuckyHub.Backend.entity.User;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u.id FROM User u WHERE u.email = :email")
    Optional<Long> findIdByEmail(@Param("email") String email);

    Optional<User> findUserById(Long id);

    boolean existsByEmail(@Email(message = "Invalid email address") @NotBlank(message = "Email cannot be empty!") @Size(max = 100, message = "Email is too long!") String email);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.winnersSelectedThisMonth = 0")
    void bulkResetWinnersCount();
}
