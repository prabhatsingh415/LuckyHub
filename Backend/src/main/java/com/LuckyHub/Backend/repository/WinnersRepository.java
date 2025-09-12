package com.LuckyHub.Backend.repository;

import com.LuckyHub.Backend.entity.Winners;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WinnersRepository extends JpaRepository<Winners, Long> {
}
