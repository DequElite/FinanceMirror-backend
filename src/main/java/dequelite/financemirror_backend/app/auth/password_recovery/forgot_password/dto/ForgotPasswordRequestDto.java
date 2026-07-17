package dequelite.financemirror_backend.app.auth.password_recovery.forgot_password.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ForgotPasswordRequestDto {
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
}
