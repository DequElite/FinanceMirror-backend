package dequelite.financemirror_backend.app.auth.password_recovery.reset_password.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ResetPasswordRequestDto {
    @NotBlank(message = "token is required")
    private String token;

    @NotBlank(message = "password is required")
    private String password;
}
