package dequelite.financemirror_backend.app.auth.password_recovery.reset_password;

import dequelite.financemirror_backend.app.auth.password_recovery.reset_password.dto.ResetPasswordRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/password-recovery/reset-password")
@RequiredArgsConstructor
public class ResetPasswordController {
    private final ResetPasswordService service;

    @PostMapping
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDto body
    ) {
        this.service.resetPassword(body);

        return ResponseEntity.ok("Success");
    }
}
