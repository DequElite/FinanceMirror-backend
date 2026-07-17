package dequelite.financemirror_backend.app.auth.refresh_token.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RefreshTokenResponseDto {
    private String accessToken;
}
