package dequelite.financemirror_backend.app.auth.session.revoke_session.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RevokeSessionRequestDto {
    @NotBlank(message = "sid is required")
    private String sessionId;
}
