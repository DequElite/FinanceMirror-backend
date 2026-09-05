package dequelite.financemirror_backend.app.common.token_pair;

import dequelite.financemirror_backend.app.common.jwt.JwtService;
import dequelite.financemirror_backend.app.common.token_pair.dto.TokenPairDto;
import dequelite.financemirror_backend.domain.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenGenerationService {
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${jwt.secret.session}")
    private String accessTokenSecret;
    @Value("${jwt.ttl.session}")
    private long accessTokenTtl;

    @Value("${jwt.secret.refresh}")
    private String refreshTokenSecret;
    @Value("${jwt.ttl.refresh}")
    private long refreshTokenTtl;

    public TokenPairDto generateTokenPair(UserEntity user, String sessionId) {
        String accessTokenId = UUID.randomUUID().toString();
        String accessToken = this.jwtService.generateToken(
                user.getId().toString(),
                accessTokenId,
                Map.of(),
                accessTokenSecret,
                accessTokenTtl
        );

        String refreshToken = this.jwtService.generateToken(
                user.getId().toString(),
                null,
                Map.of("sid", sessionId),
                refreshTokenSecret,
                refreshTokenTtl
        );
        String hashedRefreshToken = this.passwordEncoder.encode(refreshToken);
        LocalDateTime refreshTokenExpiration = LocalDateTime.now()
                .plus(Duration.ofMillis(refreshTokenTtl));

        return TokenPairDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenId(accessTokenId)
                .hashedRefreshToken(hashedRefreshToken)
                .expiresAt(refreshTokenExpiration)
                .build();
    }
}
