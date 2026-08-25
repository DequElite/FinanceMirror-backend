package dequelite.financemirror_backend.app.auth.password_recovery.forgot_password;

import dequelite.financemirror_backend.app.auth.password_recovery.forgot_password.dto.ForgotPasswordRequestDto;
import dequelite.financemirror_backend.app.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/password-recovery/forgot-password")
@RequiredArgsConstructor
public class ForgotPasswordController {
    private final ForgotPasswordService service;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDto body
            ) {
        this.service.sendRecoveryMail(body);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "If user exists, email sent",
                        null
                )
        );
    }
}
