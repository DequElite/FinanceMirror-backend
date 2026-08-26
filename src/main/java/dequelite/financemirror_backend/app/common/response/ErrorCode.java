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
                ),
    SUBSCRIPTION_ALREADY_ACTIVE(
            HttpStatus.BAD_REQUEST.value(),
            "Subscription is already active"
    ),
    PADDLE_PAYMENT_CREATION_FAILED(
            HttpStatus.BAD_GATEWAY.value(),
            "Payment provider request failed"
    ),

    PADDLE_INVALID_RESPONSE(
            HttpStatus.BAD_GATEWAY.value(),
            "Invalid payment provider response"
    ),
    PADDLE_WEBHOOK_INVALID_SIGNATURE(
            HttpStatus.FORBIDDEN.value(),
            "Invalid webhook signature"
    ),

    PADDLE_WEBHOOK_INVALID_DATA(
            HttpStatus.BAD_REQUEST.value(),
            "Invalid webhook data"
    ),

    PADDLE_WEBHOOK_PROCESSING_FAILED(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Webhook processing failed"
    ),
    SPENDING_NOT_FOUND(
            HttpStatus.NOT_FOUND.value(),
            "Spending not found"
    );

    private final Integer status;
    private final String message;
}
