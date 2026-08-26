package dequelite.financemirror_backend.app.spending.create_spending;

import dequelite.financemirror_backend.app.common.response.ApiResponse;
import dequelite.financemirror_backend.app.spending.create_spending.dto.CreateSpendingRequestDto;
import dequelite.financemirror_backend.infra.security.processors.AuthProcessor.TokenHeaderDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class CreateSpendingController {
    private final CreateSpendingService service;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createSpending(
            @AuthenticationPrincipal TokenHeaderDTO user,
            @Valid @RequestBody CreateSpendingRequestDto body
            ) {
        this.service.createSpending(
                body,
                user
        );
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Successfully created", null
                )
        );
    }
}
