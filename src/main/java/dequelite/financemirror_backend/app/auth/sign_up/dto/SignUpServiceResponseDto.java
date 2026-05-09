package dequelite.financemirror_backend.app.auth.sign_up.dto;

import dequelite.financemirror_backend.domain.user.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class SignUpServiceResponseDto {
    private String accessToken;
    private String refreshToken;
    private LocalDateTime refreshTokenExpiration;
    private UserEntity user;
}
