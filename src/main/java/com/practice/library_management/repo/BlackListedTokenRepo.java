package com.practice.library_management.repo;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.practice.library_management.entity.BlackListedToken;

@Repository
public interface BlackListedTokenRepo extends JpaRepository<BlackListedToken, Long> {

    boolean existsByToken(String token);

    void deleteByToken(String token);

    void deleteByExpiresAtBefore(Instant now);

}
