package dequelite.financemirror_backend.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
public class SessionDto {
    private String id;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private String ipAddress;
    private String userAgent;
}
