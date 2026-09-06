package dequelite.financemirror_backend.app.auth.session.revoke_session;

import dequelite.financemirror_backend.app.auth.session.revoke_session.dto.RevokeSessionRequestDto;
import dequelite.financemirror_backend.app.common.response.ApiResponse;
import dequelite.financemirror_backend.infra.security.processors.AuthProcessor.TokenHeaderDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth/session/revoke")
@RequiredArgsConstructor
@Slf4j
public class RevokeSessionController {
    private final RevokeSessionService service;

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> revokeSession(
            @AuthenticationPrincipal TokenHeaderDTO user,
            @PathVariable String id
    ) {
        this.service.revokeSession(
                UUID.fromString(id),
                UUID.fromString(user.getSub())
        );
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Success", null
                )
        );
    }
}
