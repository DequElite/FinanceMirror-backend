package dequelite.financemirror_backend.app.auth.log_in;

import dequelite.financemirror_backend.app.auth.log_in.dto.LogInRequestDto;
import dequelite.financemirror_backend.app.auth.verify_user.GenerateVerification.GenerateVerificationService;
import dequelite.financemirror_backend.app.auth.verify_user.GenerateVerification.dto.GenerateVerificationDto;
import dequelite.financemirror_backend.domain.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogInService {
    private final AuthUserService authUserService;
    private final GenerateVerificationService generateVerificationService;

    public void logIn(LogInRequestDto body){
        UserEntity user = this.authUserService.auth(body.getEmail(), body.getPassword());
        generateVerificationService.generateVerification(
                GenerateVerificationDto.builder()
                        .email(user.getEmail())
                        .userId(user.getId())
                        .build()
        );
    }
}
