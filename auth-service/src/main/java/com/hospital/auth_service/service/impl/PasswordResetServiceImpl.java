package com.hospital.auth_service.service.impl;

import com.hospital.auth_service.config.RabbitConfig;
import com.hospital.auth_service.dto.ForgotPasswordRequestDTO;
import com.hospital.auth_service.dto.PasswordResetEvent;
import com.hospital.auth_service.dto.ResetPasswordRequestDTO;
import com.hospital.auth_service.entity.PasswordResetToken;
import com.hospital.auth_service.entity.User;
import com.hospital.auth_service.exception.ResourceNotFoundException;
import com.hospital.auth_service.repository.PasswordResetTokenRepository;
import com.hospital.auth_service.repository.UserRepository;
import com.hospital.auth_service.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final RabbitTemplate rabbitTemplate;

    @Value("${password-reset.token-expiry-minutes:15}")
    private int tokenExpiryMinutes;

    @Value("${password-reset.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequestDTO dto) {
        log.info("Password reset requested for email: {}", dto.getEmail());

        // Silently succeed even if email not found — prevents user enumeration
        userRepository.findByEmail(dto.getEmail()).ifPresent(user -> {
            tokenRepository.deleteAllByUser(user);

            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .token(token)
                    .user(user)
                    .expiresAt(LocalDateTime.now().plusMinutes(tokenExpiryMinutes))
                    .build();

            tokenRepository.save(resetToken);
            log.info("Password reset token created for user: {}", user.getEmail());

            String resetLink = frontendUrl + "/reset-password?token=" + token;

            PasswordResetEvent event = PasswordResetEvent.builder()
                    .email(user.getEmail())
                    .resetToken(token)
                    .resetLink(resetLink)
                    .build();

            rabbitTemplate.convertAndSend(
                    RabbitConfig.PASSWORD_RESET_EXCHANGE,
                    RabbitConfig.PASSWORD_RESET_ROUTING_KEY,
                    event);

            log.info("RabbitMQ: Password reset event published for {}", user.getEmail());
        });
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequestDTO dto) {
        log.info("Password reset attempt with token: {}", dto.getToken());

        PasswordResetToken resetToken = tokenRepository.findByToken(dto.getToken())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired reset token"));

        if (resetToken.isUsed()) {
            throw new IllegalStateException("Reset token has already been used");
        }

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Reset token has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        log.info("Password reset successful for user: {}", user.getEmail());
    }
}
