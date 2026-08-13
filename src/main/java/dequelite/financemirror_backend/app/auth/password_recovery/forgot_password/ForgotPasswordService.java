package dequelite.financemirror_backend.app.auth.password_recovery.forgot_password;

import dequelite.financemirror_backend.app.auth.password_recovery.forgot_password.dto.ForgotPasswordRequestDto;
import dequelite.financemirror_backend.app.auth.password_recovery.forgot_password.dto.GenerateTokenDto;
import dequelite.financemirror_backend.app.common.mail.SendHtmlMainService;
import dequelite.financemirror_backend.domain.password_recovery.PasswordRecoveryEntity;
import dequelite.financemirror_backend.domain.password_recovery.PasswordRecoveryRepo;
import dequelite.financemirror_backend.domain.user.UserEntity;
import dequelite.financemirror_backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForgotPasswordService {
    private final UserRepository userRepository;
    private final PasswordRecoveryRepo passwordRecoveryRepo;

    private final GenerateTokenService generateTokenService;

    private final SendHtmlMainService mainService;

    @Value("${email.passwordRecovery.expiresAt}")
    private long passwordRecoveryExpiration;

    @Value("${email.passwordRecovery.link}")
    private String passwordRecoveryLink;

    public void sendRecoveryMail(ForgotPasswordRequestDto body) {
        Optional<UserEntity> foundUser = this.userRepository.findByEmail(body.getEmail());
        if(foundUser.isEmpty()) {
            return;
        }

        UserEntity user = foundUser.get();

        this.passwordRecoveryRepo.findByUser(user).ifPresent(passwordRecoveryRepo::delete);

        GenerateTokenDto tokenDto = this.generateTokenService.generateToken();
        LocalDateTime expiresAt = LocalDateTime.now()
                .plus(Duration.ofMillis(passwordRecoveryExpiration));

        passwordRecoveryRepo.save(
                PasswordRecoveryEntity.builder()
                        .token(tokenDto.hashedToken())
                        .expiresAt(expiresAt)
                        .user(user)
                        .build()
        );

        this.mainService.sendHtml(
                body.getEmail(),
                "Reset Password",
                PasswordRecoveryHtml.html(
                        passwordRecoveryLink + tokenDto.token()
                )
        );
    }

}
