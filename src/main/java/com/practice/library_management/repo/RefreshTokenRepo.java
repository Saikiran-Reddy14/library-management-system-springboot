package com.practice.library_management.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.practice.library_management.entity.RefreshToken;
import com.practice.library_management.entity.User;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {

    boolean existsByToken(String token);

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByAccessToken(String accessToken);

    void deleteAllByUser(User user);

}
