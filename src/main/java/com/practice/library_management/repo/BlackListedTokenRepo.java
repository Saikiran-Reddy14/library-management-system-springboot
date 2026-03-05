package com.practice.library_management.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.practice.library_management.entity.BlackListedToken;

public interface BlackListedTokenRepo extends JpaRepository<BlackListedToken, Long> {

    boolean existsByToken(String token);

    void deleteByToken(String token);

}
