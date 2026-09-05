package dequelite.financemirror_backend.app.auth.verify_user.verify;

import dequelite.financemirror_backend.app.auth.verify_user.verify.dto.VerifyRequestDto;
import dequelite.financemirror_backend.app.auth.verify_user.verify.dto.VerifyResponseDto;
import dequelite.financemirror_backend.app.auth.verify_user.verify.dto.VerifyServiceResponseDto;
import dequelite.financemirror_backend.app.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping("/auth/verify")
@Slf4j
@RequiredArgsConstructor
public class VerifyController {
    private final VerifyService service;

    @Value("${app.cookie.secure}")
    private boolean cookieSecure;

    @Value("${app.cookie.same-site}")
    private String cookieSameSite;

    @Value("${app.cookie.domain:}")
    private String cookieDomain;

    @PostMapping
    public ResponseEntity<ApiResponse<VerifyResponseDto>> verify(
            @Valid @RequestBody VerifyRequestDto body,
            HttpServletRequest request
            ) {
        VerifyServiceResponseDto responseDto =
                this.service.verifyUser(body.getToken(), body.getCode(), request.getRemoteAddr(), request.getHeader("User-Agent"));


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

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(
                        ApiResponse.success(
                                "Successfully verified",
                                VerifyResponseDto.builder()
                                        .accessToken(responseDto.getAccessToken())
                                        .user(responseDto.getUser())
                                        .build()
                        )
                );
    }
}
