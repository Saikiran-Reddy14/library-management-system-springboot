package com.practice.library_management.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.practice.library_management.entity.User;

public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

}
