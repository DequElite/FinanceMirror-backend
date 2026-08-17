package dequelite.financemirror_backend.app.profile.subscription.paypal.create_payment;

import dequelite.financemirror_backend.infra.security.processors.AuthProcessor.TokenHeaderDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/profile/payment/paypal/create")
@RequiredArgsConstructor
public class CreatePaymentController {
    private final CreatePaymentService service;

    @PostMapping
    public ResponseEntity<String> createPayment (
            @AuthenticationPrincipal TokenHeaderDTO user
    ) {
        String approvalUrl = this.service.createPayment(UUID.fromString(user.getSub()));
        return ResponseEntity.ok(approvalUrl);
    }
}
