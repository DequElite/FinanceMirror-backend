package dequelite.financemirror_backend.app.auth.password_recovery.reset_password;

import dequelite.financemirror_backend.app.auth.password_recovery.reset_password.dto.ResetPasswordRequestDto;
import dequelite.financemirror_backend.app.common.response.ApiException;
import dequelite.financemirror_backend.app.common.response.ErrorCode;
import dequelite.financemirror_backend.domain.password_recovery.PasswordRecoveryEntity;
import dequelite.financemirror_backend.domain.password_recovery.PasswordRecoveryRepo;
import dequelite.financemirror_backend.domain.user.UserEntity;
import dequelite.financemirror_backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResetPasswordService {
    private final PasswordRecoveryRepo passwordRecoveryRepo;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void resetPassword(ResetPasswordRequestDto body) {

        String hashToken = this.hashToken(body.getToken());

        PasswordRecoveryEntity passwordRecoveryEntity =
                this.passwordRecoveryRepo
                        .findByToken(
                                hashToken
                        )
                        .orElseThrow(() -> new ApiException(ErrorCode.PASSWORD_RECOVERY_NOT_FOUND));

        this.checkExpiration(passwordRecoveryEntity.getExpiresAt(), passwordRecoveryEntity);

        this.updateUserPassword(body.getPassword(), passwordRecoveryEntity);
    }

    private void updateUserPassword(String password, PasswordRecoveryEntity passwordRecovery) {
        UserEntity user = passwordRecovery.getUser();

        passwordRecoveryRepo.delete(passwordRecovery);

        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);
    }

    private void checkExpiration(LocalDateTime expiresAt, PasswordRecoveryEntity passwordRecovery){
        if (expiresAt.isBefore(LocalDateTime.now())) {
            this.passwordRecoveryRepo.delete(passwordRecovery);
            throw new ApiException(ErrorCode.PASSWORD_RECOVERY_EXPIRED);
        }
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(token.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }
}
