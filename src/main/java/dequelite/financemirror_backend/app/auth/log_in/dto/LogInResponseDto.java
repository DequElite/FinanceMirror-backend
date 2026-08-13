package dequelite.financemirror_backend.app.auth.log_in.dto;

import dequelite.financemirror_backend.domain.dto.UserDto;
import dequelite.financemirror_backend.domain.user.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LogInResponseDto {
//    private String accessToken;
//    private UserDto user;
    private String message;
}
