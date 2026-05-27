package dequelite.financemirror_backend.app.auth.refresh_token;

import dequelite.financemirror_backend.app.common.jwt.JwtService;
import dequelite.financemirror_backend.domain.session.SessionEntity;
import dequelite.financemirror_backend.domain.user.UserEntity;
import dequelite.financemirror_backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
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
        Optional<UserEntity> foundUser = this.userRepository.findById(userId);
        if(foundUser.isEmpty()) {
            log.error("USER NOT FOUND: {}", userId);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        UserEntity user = foundUser.get();
        SessionEntity session = user.getSession();

        if(session == null) {
            log.error("SESSION NULL for user: {}", userId);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        String hashedRefreshToken = session.getRefreshToken();
        LocalDateTime refreshTokenExpiration = session.getRefreshTokenExpiresAt();

        log.info("TOKEN MATCHES CHECK...");
        boolean isTokenValid = this.passwordEncoder.matches(token, hashedRefreshToken);
        log.info("TOKEN VALID: {}", isTokenValid);

        if(!isTokenValid) {
            log.error("TOKEN INVALID");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        if(LocalDateTime.now().isAfter(refreshTokenExpiration)) {
            log.error("TOKEN EXPIRED: {}", refreshTokenExpiration);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        return user;
    }
    private String checkJwtAndReturnSub(String token) {
        if(token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        Map<String, Object> tokenDecodedFromJwt;
        try {
            tokenDecodedFromJwt = this.jwtService.verifyToken(token, refreshTokenSecret);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_UNVALID");
        }

        if(
                tokenDecodedFromJwt.isEmpty() ||
                        !tokenDecodedFromJwt.containsKey("sub")
        ){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        return tokenDecodedFromJwt.get("sub").toString();
    }

}
