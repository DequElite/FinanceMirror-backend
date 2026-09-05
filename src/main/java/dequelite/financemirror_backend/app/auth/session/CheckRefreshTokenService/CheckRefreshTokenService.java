package dequelite.financemirror_backend.app.auth.session.CheckRefreshTokenService;

import dequelite.financemirror_backend.app.auth.session.refresh_token.dto.RefreshTokenPayloadDto;
import dequelite.financemirror_backend.app.common.jwt.JwtService;
import dequelite.financemirror_backend.app.common.response.ApiException;
import dequelite.financemirror_backend.app.common.response.ErrorCode;
import dequelite.financemirror_backend.domain.session.SessionEntity;
import dequelite.financemirror_backend.domain.session.SessionRepository;
import dequelite.financemirror_backend.domain.user.UserEntity;
import dequelite.financemirror_backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckRefreshTokenService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret.refresh}")
    private String refreshTokenSecret;

    public SessionEntity checkAndReturn(String token){
        RefreshTokenPayloadDto payload = this.checkJwtAndReturnPayload(token);

        UUID userId = parseUuid(payload.getSub());
        UUID sessionId = parseUuid(payload.getSid());

        UserEntity user = this.userRepository
                .findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.INVALID_REFRESH_TOKEN));

        SessionEntity session = this.sessionRepository
                .findById(sessionId)
                .orElseThrow(() -> new ApiException(ErrorCode.INVALID_REFRESH_TOKEN));

        checkSessionOwnership(user, session);
        checkSessionExpiration(session);
        checkRefreshToken(token, session);

        return session;
    }

    private void checkSessionOwnership(
            UserEntity user,
            SessionEntity session
    ) {
        if (!session.getUser().getId().equals(user.getId())) {
            throw new ApiException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    private void checkSessionExpiration(SessionEntity session) {
        if (!session.getExpiresAt().isAfter(LocalDateTime.now())) {
            throw new ApiException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    private void checkRefreshToken(
            String token,
            SessionEntity session
    ) {
        if (!passwordEncoder.matches(
                token,
                session.getRefreshToken()
        )) {
            throw new ApiException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    private RefreshTokenPayloadDto checkJwtAndReturnPayload(String token) {
        if(token == null || token.isBlank()) {
            throw new ApiException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Map<String, Object> tokenDecodedFromJwt;
        try {
            tokenDecodedFromJwt = this.jwtService.verifyToken(token, refreshTokenSecret);
        } catch (Exception e) {
            throw new ApiException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        if(
                tokenDecodedFromJwt.isEmpty() ||
                        !tokenDecodedFromJwt.containsKey("sub") || !tokenDecodedFromJwt.containsKey("sid")
        ){
            throw new ApiException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        return RefreshTokenPayloadDto.builder()
                .sub(tokenDecodedFromJwt.get("sub").toString())
                .sid(tokenDecodedFromJwt.get("sid").toString())
                .build();
    }

    private UUID parseUuid(String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new ApiException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }
}
