package dequelite.financemirror_backend.app.profile.balance.update_balance;

import dequelite.financemirror_backend.app.common.response.ApiResponse;
import dequelite.financemirror_backend.app.profile.balance.update_balance.dto.UpdateBalanceRequestDto;
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

import java.util.UUID;

@RestController
@RequestMapping("/profile/balance/update")
@RequiredArgsConstructor
public class UpdateBalanceController {
    private final UpdateBalanceService service;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> updateBalance(
            @Valid @RequestBody UpdateBalanceRequestDto body,
            @AuthenticationPrincipal TokenHeaderDTO user
            ){
        this.service.updateBalance(body, UUID.fromString(user.getSub()));
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Successfully updated", null
                )
        );
    }
}
