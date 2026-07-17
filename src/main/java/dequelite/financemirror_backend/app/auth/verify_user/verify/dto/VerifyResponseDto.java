package dequelite.financemirror_backend.app.auth.verify_user.verify.dto;


import dequelite.financemirror_backend.domain.dto.UserDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VerifyResponseDto {
    private UserDto user;
    private String accessToken;
}
