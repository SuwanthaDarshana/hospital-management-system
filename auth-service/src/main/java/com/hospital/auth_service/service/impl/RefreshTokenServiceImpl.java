package com.hospital.auth_service.service.impl;

import com.hospital.auth_service.entity.RefreshToken;
import com.hospital.auth_service.entity.User;
import com.hospital.auth_service.exception.ResourceNotFoundException;
import com.hospital.auth_service.repository.RefreshTokenRepository;
import com.hospital.auth_service.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMs;

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        log.info("Creating refresh token for user: {}", user.getEmail());

        // Delete old tokens for this user (single active session strategy)
        refreshTokenRepository.deleteAllByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshExpirationMs))
                .revoked(false)
                .build();

        RefreshToken saved = refreshTokenRepository.save(refreshToken);
        log.debug("Refresh token created: {}", saved.getToken());
        return saved;
    }

    @Override
    @Transactional
    public RefreshToken verifyRefreshToken(String token) {
        log.info("Verifying refresh token");

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("Refresh token not found: {}", token);
                    return new ResourceNotFoundException("Refresh token not found");
                });

        if (refreshToken.isRevoked()) {
            log.warn("Refresh token has been revoked for user: {}", refreshToken.getUser().getEmail());
            throw new IllegalStateException("Refresh token has been revoked. Please login again.");
        }

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            log.warn("Refresh token expired for user: {}", refreshToken.getUser().getEmail());
            refreshTokenRepository.delete(refreshToken);
            throw new IllegalStateException("Refresh token has expired. Please login again.");
        }

        log.debug("Refresh token is valid for user: {}", refreshToken.getUser().getEmail());
        return refreshToken;
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(User user) {
        log.info("Revoking all refresh tokens for user: {}", user.getEmail());
        refreshTokenRepository.deleteAllByUser(user);
    }
}
