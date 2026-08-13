package dequelite.financemirror_backend.app.auth.verify_user.verify.dto;

import dequelite.financemirror_backend.domain.dto.UserDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class VerifyServiceResponseDto {
    private String accessToken;
    private String refreshToken;
    private LocalDateTime refreshTokenExpiration;
    private UserDto user;
}
