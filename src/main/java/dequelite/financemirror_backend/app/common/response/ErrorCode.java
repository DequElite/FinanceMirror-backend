package dequelite.financemirror_backend.app.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    EMAIL_ALREADY_USED(HttpStatus.CONFLICT.value(), "Email is already used"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED.value(), "Invalid credentials"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "User not found"),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST.value(), "Invalid request"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"),
    PASSWORD_RECOVERY_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "Password recovery token not found"),
    PASSWORD_RECOVERY_EXPIRED(HttpStatus.BAD_REQUEST.value(), "Password recovery has expired"),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED.value(), "Invalid refresh token"),
    VERIFICATION_USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "User for verification not found"),
    VERIFICATION_INVALID(HttpStatus.BAD_REQUEST.value(), "Invalid verification"),
    CIBI_ANALYSIS_DATA_NOT_FOUND(
            HttpStatus.BAD_REQUEST.value(),
        "Not enough financial data for analysis"
                );

    private final Integer status;
    private final String message;
}
