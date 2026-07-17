package dequelite.financemirror_backend.app.auth.verify_user.GenerateVerification.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class GenerateVerificationDto {
    private String email;
    private UUID userId;
}
