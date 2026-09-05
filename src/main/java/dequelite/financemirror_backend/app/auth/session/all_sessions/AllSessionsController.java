package dequelite.financemirror_backend.app.auth.session.all_sessions;

import dequelite.financemirror_backend.app.common.response.ApiResponse;
import dequelite.financemirror_backend.domain.dto.SessionDto;
import dequelite.financemirror_backend.infra.security.processors.AuthProcessor.TokenHeaderDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/auth/session/all")
@RequiredArgsConstructor
@Slf4j
public class AllSessionsController {
    private final AllSessionsService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SessionDto>>> getAllSessions(
            @AuthenticationPrincipal TokenHeaderDTO tokenHeaderDTO
            ) {
        List<SessionDto> sessions = this.service.activeSessions(UUID.fromString(tokenHeaderDTO.getSub()));
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Success",
                        sessions
                )
        );
    }
}
