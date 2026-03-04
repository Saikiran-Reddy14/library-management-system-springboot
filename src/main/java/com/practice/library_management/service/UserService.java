package com.practice.library_management.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.practice.library_management.config.JwtUtils;
import com.practice.library_management.dto.LoginReq;
import com.practice.library_management.dto.LoginRes;
import com.practice.library_management.dto.RegisterReq;
import com.practice.library_management.dto.RegisterRes;
import com.practice.library_management.entity.RoleEnum;
import com.practice.library_management.entity.User;
import com.practice.library_management.exception.ResourceExists;
import com.practice.library_management.exception.ResourceNotFound;
import com.practice.library_management.repo.UserRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Transactional
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

    public LoginRes login(LoginReq request) {
        User user = userRepo.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFound("Invalid email or password"));
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        return LoginRes.builder().accessToken(jwtUtils.generateAccessToken(authentication.getName()))
                .refreshToken(jwtUtils.generateRefreshToken(authentication.getName())).build();
    }

}
