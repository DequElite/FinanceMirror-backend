package dequelite.financemirror_backend.app.auth.verify_user.GenerateVerification;

import dequelite.financemirror_backend.app.auth.verify_user.GenerateVerification.dto.GenerateVerificationDto;
import dequelite.financemirror_backend.app.common.mail.SendHtmlMainService;
import dequelite.financemirror_backend.domain.user.UserEntity;
import dequelite.financemirror_backend.domain.user.UserRepository;
import dequelite.financemirror_backend.domain.verification.VerificationEntity;
import dequelite.financemirror_backend.domain.verification.VerificationRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenerateVerificationService {
    private final PasswordEncoder passwordEncoder;
    private final GenerateVerificationTokensService generateVerificationTokensService;

    private final UserRepository userRepository;
    private final VerificationRepo verificationRepo;

    private final SendHtmlMainService sendHtmlMainService;

    @Value("${email.verify.expiresAt}")
    private long verificationExpiration;

    @Value("${email.verify.link}")
    private String verificationLink;

    public void generateVerification(GenerateVerificationDto dto) {
        log.info("Starting verification generation for userId={}, email={}",
                dto.getUserId(), dto.getEmail());

        UserEntity user = this.userRepository
                .findById(dto.getUserId())
                .orElseThrow(() -> {
                    log.error("User not found: userId={}", dto.getUserId());
                    return new ResponseStatusException(HttpStatus.NOT_FOUND);
                });

        log.info("User found: userId={}", user.getId());

        this.verificationRepo.findByUser(user).ifPresent(verification -> {
            log.info("Existing verification found, deleting it for userId={}", user.getId());
            verificationRepo.delete(verification);
        });

        log.info("Generating verification token and code...");

        String token = this.generateVerificationTokensService.generateToken();
        String code = this.generateVerificationTokensService.generateCode();

        String hashedCode = this.passwordEncoder.encode(code);

        LocalDateTime expiresAt = LocalDateTime.now()
                .plus(Duration.ofMillis(verificationExpiration));

        log.info("Saving verification entity: userId={}, expiresAt={}",
                user.getId(), expiresAt);

        verificationRepo.save(VerificationEntity.builder()
                .token(token)
                .code(hashedCode)
                .expiresAt(expiresAt)
                .attempts(0)
                .user(user)
                .build());

        log.info("Verification entity saved successfully");

        String verificationUrl = verificationLink + token;

        log.info("Sending verification email to {}", dto.getEmail());
        log.info("Verification link configured: {}", verificationLink);

        try {
            this.sendHtmlMainService.sendHtml(
                    dto.getEmail(),
                    "Verify account",
                    EmailVerificationHtml.html(
                            verificationUrl,
                            code
                    )
            );

            log.info("Verification email sent successfully to {}", dto.getEmail());

        } catch (Exception e) {
            log.error("FAILED TO SEND VERIFICATION EMAIL to {}", dto.getEmail(), e);
            throw e;
        }
    }
}
