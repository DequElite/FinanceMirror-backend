package dequelite.financemirror_backend.app.profile.subscription.paddle.payment_webhook;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import dequelite.financemirror_backend.app.profile.subscription.paddle.payment_webhook.PaymentWebhookService;

@RestController
@RequestMapping("/profile/payment/paddle/webhook")
@RequiredArgsConstructor
@Slf4j
public class PaymentWebhookController {
    private final PaymentWebhookService service;

    @PostMapping
    public ResponseEntity<String> webhook(
            @RequestHeader("Paddle-Signature") String paddleSignature,
            @RequestBody String rawBody
    ) {
        log.info("PaymentWebhookController webhook started");
        this.service.completeSubscription(paddleSignature, rawBody);
        return ResponseEntity.ok("COMPLETED SUCCESSFULLY");
    }
}
