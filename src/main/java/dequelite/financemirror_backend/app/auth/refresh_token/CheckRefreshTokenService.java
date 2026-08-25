package dequelite.financemirror_backend.app.auth.refresh_token;

import dequelite.financemirror_backend.app.common.jwt.JwtService;
import dequelite.financemirror_backend.app.common.response.ApiException;
import dequelite.financemirror_backend.app.common.response.ErrorCode;
import dequelite.financemirror_backend.domain.session.SessionEntity;
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
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret.refresh}")
    private String refreshTokenSecret;

    public UserEntity checkAndReturn(String token){
        String refreshTokenSub = this.checkJwtAndReturnSub(token);

        return this.checkUserSessionAndReturn(UUID.fromString(refreshTokenSub), token);
    }

    private UserEntity checkUserSessionAndReturn(UUID userId, String token){
        UserEntity user = this.userRepository
                .findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.INVALID_REFRESH_TOKEN));

        SessionEntity session = user.getSession();

        if(session == null) {
            throw new ApiException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String hashedRefreshToken = session.getRefreshToken();
        LocalDateTime refreshTokenExpiration = session.getRefreshTokenExpiresAt();

        boolean isTokenValid = this.passwordEncoder.matches(token, hashedRefreshToken);

        if(!isTokenValid) {
            throw new ApiException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        if(LocalDateTime.now().isAfter(refreshTokenExpiration)) {
            throw new ApiException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        return user;
    }
    private String checkJwtAndReturnSub(String token) {
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
                        !tokenDecodedFromJwt.containsKey("sub")
        ){
            throw new ApiException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        return tokenDecodedFromJwt.get("sub").toString();
    }

}
