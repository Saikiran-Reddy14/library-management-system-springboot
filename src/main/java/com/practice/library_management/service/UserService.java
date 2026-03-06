package com.practice.library_management.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.practice.library_management.dto.LoginReq;
import com.practice.library_management.dto.TokenRes;
import com.practice.library_management.dto.RegisterReq;
import com.practice.library_management.dto.RegisterRes;
import com.practice.library_management.entity.BlackListedToken;
import com.practice.library_management.entity.RefreshToken;
import com.practice.library_management.entity.RoleEnum;
import com.practice.library_management.entity.User;
import com.practice.library_management.exception.CustomException;
import com.practice.library_management.exception.ResourceExists;
import com.practice.library_management.exception.ResourceNotFound;
import com.practice.library_management.repo.BlackListedTokenRepo;
import com.practice.library_management.repo.RefreshTokenRepo;
import com.practice.library_management.repo.UserRepo;
import com.practice.library_management.security.JwtUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final BlackListedTokenRepo blackListedTokenRepo;
    private final RefreshTokenRepo refreshTokenRepo;

    @Value("${jwt.refreshTokenExpiration}")
    private long refreshTokenExpiry;

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

    @Transactional
    public TokenRes login(LoginReq request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepo.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFound("Invalid email or password"));

        refreshTokenRepo.deleteAllByUser(user);

        String accessToken = jwtUtils.generateAccessToken(authentication.getName());
        String refreshToken = jwtUtils.generateRefreshToken(authentication.getName());

        RefreshToken refresh = RefreshToken.builder().accessToken(accessToken).token(refreshToken)
                .expiresAt(Instant.now().plusMillis(refreshTokenExpiry)).user(user).build();
        refreshTokenRepo.save(refresh);

        return TokenRes.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken).build();
    }

    @Transactional
    public void logout(String token) {
        if (!blackListedTokenRepo.existsByToken(token)) {
            blackListedTokenRepo.save(BlackListedToken.builder().token(token).build());
        }
        refreshTokenRepo.findByAccessToken(token).ifPresent(refreshTokenRepo::delete);
    }

    @Transactional
    public TokenRes refresh(String token) {
        RefreshToken refresh = refreshTokenRepo.findByToken(token)
                .orElseThrow(() -> new ResourceNotFound("Refresh token not found"));

        if (refresh.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepo.delete(refresh);
            throw new CustomException("Refresh token expired");
        }

        if (blackListedTokenRepo.existsByToken(refresh.getAccessToken())) {
            refreshTokenRepo.delete(refresh);
            throw new CustomException("Access token is blacklisted");
        }

        String accessToken = jwtUtils.generateAccessToken(refresh.getUser().getEmail());
        String refreshToken = jwtUtils.generateRefreshToken(refresh.getUser().getEmail());

        blackListedTokenRepo.save(BlackListedToken.builder().token(refresh.getAccessToken()).build());
        refreshTokenRepo.delete(refresh);

        RefreshToken newRefresh = RefreshToken.builder().accessToken(accessToken).token(refreshToken)
                .expiresAt(Instant.now().plusMillis(refreshTokenExpiry)).user(refresh.getUser()).build();
        refreshTokenRepo.save(newRefresh);

        return TokenRes.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }

}
