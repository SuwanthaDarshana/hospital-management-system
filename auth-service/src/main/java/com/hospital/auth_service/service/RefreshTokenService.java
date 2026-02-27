package com.hospital.auth_service.service;

import com.hospital.auth_service.entity.RefreshToken;
import com.hospital.auth_service.entity.User;

public interface RefreshTokenService {

    /**
     * Creates and persists a new refresh token for the given user.
     * Any existing tokens for that user are deleted first (single-session strategy).
     */
    RefreshToken createRefreshToken(User user);

    /**
     * Verifies a refresh token: checks it exists, is not revoked, and is not expired.
     *
     * @throws com.hospital.auth_service.exception.ResourceNotFoundException if token is not found
     * @throws IllegalStateException if token is expired or revoked
     */
    RefreshToken verifyRefreshToken(String token);

    /**
     * Revokes (logically deletes) all refresh tokens for the given user — used during logout.
     */
    void revokeAllUserTokens(User user);
}

