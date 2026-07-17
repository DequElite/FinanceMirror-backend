package dequelite.financemirror_backend.app.analytics.ai_report;

import dequelite.financemirror_backend.infra.security.processors.AuthProcessor.TokenHeaderDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/analytics/ai-report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService service;

    @PostMapping
    public ResponseEntity<Void> createReport(
            @AuthenticationPrincipal TokenHeaderDTO user
            ){
        this.service.createReport(UUID.fromString(user.getSub()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
