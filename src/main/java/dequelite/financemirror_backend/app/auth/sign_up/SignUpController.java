package dequelite.financemirror_backend.app.auth.sign_up;

import dequelite.financemirror_backend.app.auth.sign_up.dto.SignUpRequestDto;
import dequelite.financemirror_backend.app.auth.sign_up.dto.SignUpResponseDto;
import dequelite.financemirror_backend.app.auth.sign_up.dto.SignUpServiceResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/auth/sign-up")
@Slf4j
@RequiredArgsConstructor
public class SignUpController {
    private final SignUpService service;

    @Value("${app.cookie.secure}")
    private boolean cookieSecure;

    @Value("${app.cookie.same-site}")
    private String cookieSameSite;

    @Value("${app.cookie.domain:}")
    private String cookieDomain;

    @PostMapping
    public ResponseEntity<SignUpResponseDto> signUp(
            @Valid @RequestBody SignUpRequestDto body
            ){
        SignUpServiceResponseDto responseDto = this.service.signUp(body);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", responseDto.getRefreshToken())
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(Duration.between(
                        LocalDateTime.now(),
                        responseDto.getRefreshTokenExpiration()
                ).getSeconds())
                .sameSite(cookieSameSite)
                .domain(cookieDomain.isBlank() ? null : cookieDomain)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(
                        SignUpResponseDto.builder()
                                .accessToken(responseDto.getAccessToken())
                                .user(responseDto.getUser())
                                .build()
                );
    }
}
