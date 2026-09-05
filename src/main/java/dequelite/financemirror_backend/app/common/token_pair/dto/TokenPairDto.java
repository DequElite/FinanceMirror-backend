package dequelite.financemirror_backend.app.common.token_pair.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class TokenPairDto {
    private String accessToken;
    private String accessTokenId;

    private String refreshToken;
    private String hashedRefreshToken;

    private LocalDateTime expiresAt;
}
