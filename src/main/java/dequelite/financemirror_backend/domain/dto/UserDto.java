package dequelite.financemirror_backend.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class UserDto {
    private UUID id;
    private String email;
    private LocalDateTime createdAt;

    private ProfileDto profile;
}