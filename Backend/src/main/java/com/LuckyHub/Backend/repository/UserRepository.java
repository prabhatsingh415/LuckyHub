package com.LuckyHub.Backend.repository;

import com.LuckyHub.Backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Long findIdByEmail(String email);

   Optional<User> findUserById(Long id);
}
