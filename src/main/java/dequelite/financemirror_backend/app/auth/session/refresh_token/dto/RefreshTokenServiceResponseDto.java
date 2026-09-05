package dequelite.financemirror_backend.app.auth.session.refresh_token.dto;

import dequelite.financemirror_backend.domain.user.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
public class RefreshTokenServiceResponseDto {
    private String accessToken;
    private String refreshToken;
    private LocalDateTime expiresAt;
}
