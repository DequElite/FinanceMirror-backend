package dequelite.financemirror_backend.app.auth.refresh_token;

import dequelite.financemirror_backend.app.auth.refresh_token.dto.RefreshTokenResponseDto;
import dequelite.financemirror_backend.app.auth.refresh_token.dto.RefreshTokenServiceResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth/refresh-token")
@RequiredArgsConstructor
public class RefreshTokenController {
    private final RefreshTokenService service;

    @PostMapping
    public ResponseEntity<RefreshTokenResponseDto> refreshToken(
            @CookieValue(name = "refreshToken") String refreshToken
    ) {
        RefreshTokenServiceResponseDto responseDto = this.service.refreshToken(refreshToken);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", responseDto.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.between(
                        LocalDateTime.now(),
                        responseDto.getRefreshTokenExpiration()
                ).getSeconds())
                .sameSite("none")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(
                        RefreshTokenResponseDto.builder()
                                .accessToken(responseDto.getAccessToken())
                                .build()
                );
    }

}
