package com.practice.library_management.service;

import java.time.Instant;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.practice.library_management.repo.BlackListedTokenRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupService {

    private final BlackListedTokenRepo blackListedTokenRepo;

    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void deleteExpiredBlacklistedTokens() {
        blackListedTokenRepo.deleteByExpiresAtBefore(Instant.now());
        log.info("Deleted expired blacklisted tokens");
    }

}
