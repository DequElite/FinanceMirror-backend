package dequelite.financemirror_backend.app.auth.verify_user.verify;

import dequelite.financemirror_backend.app.auth.verify_user.verify.dto.VerifyServiceResponseDto;
import dequelite.financemirror_backend.app.common.mapper.EntityMapper;
import dequelite.financemirror_backend.app.common.session.SessionService;
import dequelite.financemirror_backend.app.common.token_pair.TokenGenerationService;
import dequelite.financemirror_backend.app.common.token_pair.dto.TokenPairDto;
import dequelite.financemirror_backend.domain.user.UserEntity;
import dequelite.financemirror_backend.domain.user.UserRepository;
import dequelite.financemirror_backend.domain.verification.VerificationEntity;
import dequelite.financemirror_backend.domain.verification.VerificationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VerifyService {
    private final VerificationRepo verificationRepo;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenGenerationService tokenGenerationService;
    private final SessionService sessionService;

    @Transactional(noRollbackFor = ResponseStatusException.class)
    public VerifyServiceResponseDto verifyUser(String token, String code) {
        VerificationEntity verification = this.verificationRepo
                .findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

        this.checkExpiration(verification.getExpiresAt(), verification);

        this.checkAttempts(code, verification);

        UserEntity user = this.verifyUser(verification);

        TokenPairDto tokenPairDto = this.tokenGenerationService.generateTokenPair(user);
        this.sessionService.createSession(tokenPairDto, user);

        return VerifyServiceResponseDto.builder()
                .refreshToken(tokenPairDto.getRefreshToken())
                .accessToken(tokenPairDto.getAccessToken())
                .refreshTokenExpiration(tokenPairDto.getRefreshTokenExpiration())
                .user(EntityMapper.toUserDto(user))
                .build();
    }

    private void checkExpiration(LocalDateTime expiresAt, VerificationEntity verification){
        if (expiresAt.isBefore(LocalDateTime.now())) {
            verificationRepo.delete(verification);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Verification has expired");
        }
    }

    private void checkAttempts(String code, VerificationEntity verification) {
        int attempts = verification.getAttempts();
        if (!passwordEncoder.matches(code, verification.getCode())) {
            attempts++;
            verification.setAttempts(attempts);

            if (attempts >= 3) {
                verificationRepo.delete(verification);
            } else {
                verificationRepo.save(verification);
            }

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Attemps");
        }
    }

    private UserEntity verifyUser(VerificationEntity verification) {
        UserEntity user = verification.getUser();

        user.setVerified(true);
        userRepository.save(user);

        verification.setUser(null);
        verificationRepo.delete(verification);

        return user;
    }
}
