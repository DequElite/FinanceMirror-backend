package dequelite.financemirror_backend.app.profile.subscription.paypal.capture_payment;

import dequelite.financemirror_backend.app.profile.subscription.paypal.capture_payment.dto.CapturePaymentDto;
import dequelite.financemirror_backend.infra.security.processors.AuthProcessor.TokenHeaderDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/profile/payment/paypal/capture")
@RequiredArgsConstructor
public class CapturePaymentController {
    private final CapturePaymentService service;

    @PostMapping
    public ResponseEntity<String> capturePayment(
            @Valid @RequestBody CapturePaymentDto body,
            @AuthenticationPrincipal TokenHeaderDTO tokenHeaderDTO
            ){
        this.service.capture(body.getToken(), UUID.fromString(tokenHeaderDTO.getSub()));

        return ResponseEntity.ok("COMPLETED");
    }
}
