package dequelite.financemirror_backend.app.auth.log_in;

import dequelite.financemirror_backend.app.auth.log_in.dto.LogInRequestDto;
import dequelite.financemirror_backend.app.auth.log_in.dto.LogInResponseDto;
import dequelite.financemirror_backend.app.auth.log_in.dto.LogInServiceResponseDto;
import dequelite.financemirror_backend.app.auth.sign_up.dto.SignUpResponseDto;
import dequelite.financemirror_backend.app.common.response.ApiResponse;
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

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> logIn(
            @Valid @RequestBody LogInRequestDto body
            ) {
          this.service.logIn(body);
         return ResponseEntity.ok(
                 ApiResponse.success(
                         "Logged in successfully. Verification sent",
                         null
                 )
         );

    }
}
