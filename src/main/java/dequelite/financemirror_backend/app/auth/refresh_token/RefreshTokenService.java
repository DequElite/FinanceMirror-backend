package dequelite.financemirror_backend.app.auth.refresh_token;

import dequelite.financemirror_backend.app.auth.refresh_token.dto.RefreshTokenServiceResponseDto;
import dequelite.financemirror_backend.app.common.session.SessionService;
import dequelite.financemirror_backend.app.common.token_pair.TokenGenerationService;
import dequelite.financemirror_backend.app.common.token_pair.dto.TokenPairDto;
import dequelite.financemirror_backend.domain.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final CheckRefreshTokenService checkRefreshTokenService;
    private final TokenGenerationService tokenGenerationService;
    private final SessionService sessionService;

    public RefreshTokenServiceResponseDto refreshToken(String token) {
        UserEntity user = this.checkRefreshTokenService.checkAndReturn(token);

        TokenPairDto tokenPairDto = this.tokenGenerationService.generateTokenPair(user);

        this.sessionService.createSession(tokenPairDto, user);

        return RefreshTokenServiceResponseDto.builder()
                .accessToken(tokenPairDto.getAccessToken())
                .refreshToken(tokenPairDto.getRefreshToken())
                .refreshTokenExpiration(tokenPairDto.getRefreshTokenExpiration())
                .build();
    }

}
