package dequelite.financemirror_backend.app.auth.log_in;

import dequelite.financemirror_backend.app.auth.log_in.dto.LogInRequestDto;
import dequelite.financemirror_backend.app.auth.log_in.dto.LogInResponseDto;
import dequelite.financemirror_backend.app.auth.log_in.dto.LogInServiceResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth/log-in")
@RequiredArgsConstructor
public class LogInController {
    private final LogInService service;

    @Value("${app.cookie.secure}")
    private boolean cookieSecure;

    @Value("${app.cookie.same-site}")
    private String cookieSameSite;

    @Value("${app.cookie.domain:}")
    private String cookieDomain;

    @PostMapping
    private ResponseEntity<LogInResponseDto> logIn(
            @Valid @RequestBody LogInRequestDto body
            ) {
        LogInServiceResponseDto serviceResponseDto = this.service.logIn(body);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", serviceResponseDto.getRefreshToken())
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(Duration.between(
                        LocalDateTime.now(),
                        serviceResponseDto.getRefreshTokenExpiration()
                ).getSeconds())
                .sameSite(cookieSameSite)
                .domain(cookieDomain.isBlank() ? null : cookieDomain)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(
                        LogInResponseDto.builder()
                                .accessToken(serviceResponseDto.getAccessToken())
                                .user(serviceResponseDto.getUser())
                                .build()
                );
    }
}
