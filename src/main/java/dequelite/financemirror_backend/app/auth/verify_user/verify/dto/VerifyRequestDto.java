package dequelite.financemirror_backend.app.auth.verify_user.verify.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class VerifyRequestDto {
    @NotBlank(message = "token is required")
    private String token;

    @NotBlank(message = "code is required")
    @Size(max = 6, message = "code must be 6 characters")
    private String code;

}
