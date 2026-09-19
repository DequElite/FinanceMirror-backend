package dequelite.financemirror_backend.app.auth.sign_up;

import dequelite.financemirror_backend.app.auth.sign_up.dto.SignUpRequestDto;
import dequelite.financemirror_backend.app.auth.verify_user.GenerateVerification.GenerateVerificationService;
import dequelite.financemirror_backend.app.auth.verify_user.GenerateVerification.dto.GenerateVerificationDto;
import dequelite.financemirror_backend.domain.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignUpService {
    private final RegisterUserService registerUserService;
    private final GenerateVerificationService generateVerificationService;

    @Transactional
    public void signUp(SignUpRequestDto body) {
        UserEntity user = this.registerUserService.createUser(body);
        generateVerificationService.generateVerification(
                GenerateVerificationDto.builder()
                        .email(user.getEmail())
                        .userId(user.getId())
                        .build()
        );
    }

}
