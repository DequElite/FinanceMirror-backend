package dequelite.financemirror_backend.app.analytics.ai.can_i_buy_it;


import dequelite.financemirror_backend.app.analytics.ai.can_i_buy_it.dto.CIBIRequestDto;
import dequelite.financemirror_backend.app.analytics.ai.can_i_buy_it.dto.CIBIResponseDto;
import dequelite.financemirror_backend.app.auth.log_in.dto.LogInResponseDto;
import dequelite.financemirror_backend.app.common.response.ApiResponse;
import dequelite.financemirror_backend.infra.security.processors.AuthProcessor.TokenHeaderDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/analytics/ai/can-i-buy-it")
@RequiredArgsConstructor
public class CIBIController {
    private final CIBIService service;

    @PostMapping
    public ResponseEntity<ApiResponse<CIBIResponseDto>> createReport(
            @AuthenticationPrincipal TokenHeaderDTO user,
            @RequestHeader("Accept-Language") String language,
            @Valid @RequestBody CIBIRequestDto body
            ){
        CIBIResponseDto responseDto = this.service.cibiReport(
                language,
                UUID.fromString(user.getSub()),
                body
        );
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Success",
                        responseDto
                )
        );
    }
}
