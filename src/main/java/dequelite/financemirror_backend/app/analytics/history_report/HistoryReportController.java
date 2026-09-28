package dequelite.financemirror_backend.app.analytics.history_report;

import dequelite.financemirror_backend.app.common.response.ApiResponse;
import dequelite.financemirror_backend.infra.security.processors.AuthProcessor.TokenHeaderDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/analytics/history")
@RequiredArgsConstructor
public class HistoryReportController {
    private final HistoryReportService service;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createHistoryReport(
            @AuthenticationPrincipal TokenHeaderDTO user
    ){
        this.service.createHistoryReport(UUID.fromString(user.getSub()));
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Successfully created",
                        null
                )
        );
    }
}
