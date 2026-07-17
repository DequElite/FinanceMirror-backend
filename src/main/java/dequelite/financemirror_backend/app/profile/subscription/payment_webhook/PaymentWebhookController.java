package dequelite.financemirror_backend.app.profile.subscription.payment_webhook;

import dequelite.financemirror_backend.app.profile.subscription.payment_webhook.dto.PaymentWebhookRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile/payment/paypal/webhook")
@RequiredArgsConstructor
public class PaymentWebhookController {
    private final PaymentWebhookService service;

    @PostMapping
    public ResponseEntity<String> webhook(
            @RequestHeader("PAYPAL-TRANSMISSION-ID") String transmissionId,
            @RequestHeader("PAYPAL-TRANSMISSION-TIME") String transmissionTime,
            @RequestHeader("PAYPAL-TRANSMISSION-SIG") String transmissionSig,
            @RequestHeader("PAYPAL-CERT-URL") String certUrl,
            @RequestHeader("PAYPAL-AUTH-ALGO") String authAlgo,
            @RequestBody String rawBody
            ) {
        this.service.completeSubscription(
                transmissionId,
                transmissionTime,
                transmissionSig,
                certUrl,
                authAlgo,
                rawBody
        );

        return  ResponseEntity.ok("COMPLETED SUCCESSFULLY");
    }
}
