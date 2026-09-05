package dequelite.financemirror_backend.app.auth.session.refresh_token;

import dequelite.financemirror_backend.app.auth.session.refresh_token.dto.RefreshTokenResponseDto;
import dequelite.financemirror_backend.app.auth.session.refresh_token.dto.RefreshTokenServiceResponseDto;
import dequelite.financemirror_backend.app.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
@RequestMapping("/auth/session/refresh-token")
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenController {
    private final RefreshTokenService service;

    @Value("${app.cookie.secure}")
    private boolean cookieSecure;

    @Value("${app.cookie.same-site}")
    private String cookieSameSite;

    @Value("${app.cookie.domain}")
    private String cookieDomain;

    @PostMapping
    public ResponseEntity<ApiResponse<RefreshTokenResponseDto>> refreshToken(
            @CookieValue(name = "refreshToken") String refreshToken
    ) {
        log.info("REFRESH STARTED: {}", refreshToken);
        RefreshTokenServiceResponseDto responseDto = this.service.refreshToken(refreshToken);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", responseDto.getRefreshToken())
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(Duration.between(
                        LocalDateTime.now(),
                        responseDto.getExpiresAt()
                ).getSeconds())
                .sameSite(cookieSameSite)
                .domain(cookieDomain.isBlank() ? null : cookieDomain)
                .build();

        log.info("REFRESH FINISHED: {}", responseDto);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(
                        ApiResponse.success(
                                "Successfully refreshed",
                                RefreshTokenResponseDto.builder()
                                        .accessToken(responseDto.getAccessToken())
                                        .build()
                        )
                );
    }

}
