package dequelite.financemirror_backend.app.auth.sign_up.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class SignUpRequestDto {
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 10 characters")
    private String password;

    @NotNull
    @Min(0)
    private Integer salary;

    @NotBlank
    @Length(max = 3, min = 3)
    private String currency;
}
