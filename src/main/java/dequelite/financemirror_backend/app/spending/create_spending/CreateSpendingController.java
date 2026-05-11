package dequelite.financemirror_backend.app.spending.create_spending;

import dequelite.financemirror_backend.app.spending.create_spending.dto.CreateSpendingRequestDto;
import dequelite.financemirror_backend.infra.security.processors.AuthProcessor.TokenHeaderDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/spending/new")
@RequiredArgsConstructor
public class CreateSpendingController {
    private final CreateSpendingService service;

    @PostMapping
    public ResponseEntity<Void> createSpending(
            @AuthenticationPrincipal TokenHeaderDTO user,
            @Valid @RequestBody CreateSpendingRequestDto body
            ) {
        this.service.createSpending(
                body,
                user
        );
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
