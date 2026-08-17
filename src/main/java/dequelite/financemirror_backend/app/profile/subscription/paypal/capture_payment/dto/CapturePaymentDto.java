package dequelite.financemirror_backend.app.profile.subscription.paypal.capture_payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CapturePaymentDto {
    @NotBlank(message = "token is required")
    private String token;
}
