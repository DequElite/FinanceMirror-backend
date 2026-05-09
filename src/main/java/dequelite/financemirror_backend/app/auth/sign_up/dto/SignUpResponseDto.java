package dequelite.financemirror_backend.app.auth.sign_up.dto;


import dequelite.financemirror_backend.domain.user.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SignUpResponseDto {
    private UserEntity user;
    private String accessToken;
}
