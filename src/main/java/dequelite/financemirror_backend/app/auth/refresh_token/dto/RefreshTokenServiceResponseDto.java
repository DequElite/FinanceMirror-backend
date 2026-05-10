package dequelite.financemirror_backend.app.auth.refresh_token.dto;

import dequelite.financemirror_backend.domain.user.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class RefreshTokenServiceResponseDto {
    private String accessToken;
    private String refreshToken;
    private LocalDateTime refreshTokenExpiration;
}
