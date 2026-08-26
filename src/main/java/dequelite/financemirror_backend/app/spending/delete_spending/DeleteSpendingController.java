package dequelite.financemirror_backend.app.spending.delete_spending;

import dequelite.financemirror_backend.app.common.response.ApiResponse;
import dequelite.financemirror_backend.infra.security.processors.AuthProcessor.TokenHeaderDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/spending/delete")
@RequiredArgsConstructor
public class DeleteSpendingController {
    private final DeleteSpendingService service;
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSpending(
            @PathVariable String id,
            @AuthenticationPrincipal TokenHeaderDTO user
            ) {
        this.service.deleteSpending(
                UUID.fromString(user.getSub()),
                UUID.fromString(id)
        );
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Successfully deleted", null
                )
        );
    }

}
