package dequelite.financemirror_backend.app.auth.password_recovery.forgot_password.dto;

public record GenerateTokenDto(
        String token,
        String hashedToken
) {
}
