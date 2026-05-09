package dequelite.financemirror_backend.app.auth.log_in;

import dequelite.financemirror_backend.app.auth.log_in.dto.LogInRequestDto;
import dequelite.financemirror_backend.app.auth.log_in.dto.LogInResponseDto;
import dequelite.financemirror_backend.app.auth.log_in.dto.LogInServiceResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @PostMapping
    private ResponseEntity<LogInResponseDto> logIn(
            @Valid @RequestBody LogInRequestDto body
            ) {
        LogInServiceResponseDto serviceResponseDto = this.service.logIn(body);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", serviceResponseDto.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.between(
                        LocalDateTime.now(),
                        serviceResponseDto.getRefreshTokenExpiration()
                ).getSeconds())
                .sameSite("none")
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
