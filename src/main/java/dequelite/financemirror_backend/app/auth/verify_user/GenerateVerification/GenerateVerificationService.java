package dequelite.financemirror_backend.app.auth.verify_user.GenerateVerification;

import dequelite.financemirror_backend.app.auth.verify_user.GenerateVerification.dto.GenerateVerificationDto;
import dequelite.financemirror_backend.app.common.mail.SendHtmlMainService;
import dequelite.financemirror_backend.app.common.response.ApiException;
import dequelite.financemirror_backend.app.common.response.ErrorCode;
import dequelite.financemirror_backend.domain.user.UserEntity;
import dequelite.financemirror_backend.domain.user.UserRepository;
import dequelite.financemirror_backend.domain.verification.VerificationEntity;
import dequelite.financemirror_backend.domain.verification.VerificationRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

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
        UserEntity user = this.userRepository
                .findById(dto.getUserId())
                .orElseThrow(() ->
                        new ApiException(ErrorCode.VERIFICATION_USER_NOT_FOUND)
                );

        this.verificationRepo.findByUser(user).ifPresent(verificationRepo::delete);

        String token = this.generateVerificationTokensService.generateToken();
        String code = this.generateVerificationTokensService.generateCode();

        String hashedCode = this.passwordEncoder.encode(code);

        LocalDateTime expiresAt = LocalDateTime.now()
                .plus(Duration.ofMillis(verificationExpiration));

        verificationRepo.save(VerificationEntity.builder()
                .token(token)
                .code(hashedCode)
                .expiresAt(expiresAt)
                .attempts(0)
                .user(user)
                .build());

        String verificationUrl = verificationLink + token;

        try {
            this.sendHtmlMainService.sendHtml(
                    dto.getEmail(),
                    "Verify account",
                    EmailVerificationHtml.html(
                            verificationUrl,
                            code
                    )
            );

        } catch (Exception e) {
            throw e;
        }
    }
}
