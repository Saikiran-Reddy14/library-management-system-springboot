package com.practice.library_management.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.practice.library_management.dto.RegisterReq;
import com.practice.library_management.dto.RegisterRes;
import com.practice.library_management.entity.RoleEnum;
import com.practice.library_management.entity.User;
import com.practice.library_management.exception.ResourceExists;
import com.practice.library_management.repo.UserRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public RegisterRes register(RegisterReq request) {
        if (userRepo.existsByEmail(request.email())) {
            throw new ResourceExists("Email already exists");
        }
        User user = User.builder().username(request.username()).email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(RoleEnum.USER).build();
        userRepo.save(user);
        return RegisterRes.builder().username(request.username()).email(request.email()).build();
    }

}
