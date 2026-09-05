package dequelite.financemirror_backend.app.auth.session.refresh_token;

import dequelite.financemirror_backend.app.auth.session.refresh_token.dto.RefreshTokenServiceResponseDto;
import dequelite.financemirror_backend.app.common.session.SessionService;
import dequelite.financemirror_backend.app.common.token_pair.TokenGenerationService;
import dequelite.financemirror_backend.app.common.token_pair.dto.TokenPairDto;
import dequelite.financemirror_backend.domain.session.SessionEntity;
import dequelite.financemirror_backend.domain.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.antlr.runtime.Token;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final CheckRefreshTokenService checkRefreshTokenService;
    private final SessionService sessionService;

    public RefreshTokenServiceResponseDto refreshToken(String token) {
        SessionEntity session = this.checkRefreshTokenService.checkAndReturn(token);

        TokenPairDto tokenPairDto = this.sessionService.updateSession(session);

        return RefreshTokenServiceResponseDto.builder()
                .accessToken(tokenPairDto.getAccessToken())
                .refreshToken(tokenPairDto.getRefreshToken())
                .expiresAt(tokenPairDto.getExpiresAt())
                .build();
    }

}
