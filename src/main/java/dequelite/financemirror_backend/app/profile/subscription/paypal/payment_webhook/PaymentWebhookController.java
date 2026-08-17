package dequelite.financemirror_backend.app.profile.subscription.paypal.payment_webhook;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile/payment/paypal/webhook")
@RequiredArgsConstructor
@Slf4j
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
        log.info("PaymentWebhookController webhook started");
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
