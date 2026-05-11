package dequelite.financemirror_backend.app.auth.sign_up;

import dequelite.financemirror_backend.app.auth.sign_up.dto.SignUpRequestDto;
import dequelite.financemirror_backend.app.auth.sign_up.dto.SignUpServiceResponseDto;
import dequelite.financemirror_backend.app.common.jwt.JwtService;
import dequelite.financemirror_backend.app.common.mapper.EntityMapper;
import dequelite.financemirror_backend.app.common.session.SessionService;
import dequelite.financemirror_backend.app.common.token_pair.TokenGenerationService;
import dequelite.financemirror_backend.app.common.token_pair.dto.TokenPairDto;
import dequelite.financemirror_backend.domain.profile.ProfileEntity;
import dequelite.financemirror_backend.domain.session.SessionEntity;
import dequelite.financemirror_backend.domain.session.SessionRepository;
import dequelite.financemirror_backend.domain.user.UserEntity;
import dequelite.financemirror_backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SignUpService {
    private final RegisterUserService registerUserService;
    private final TokenGenerationService tokenGenerationService;
    private final SessionService sessionService;

    public SignUpServiceResponseDto signUp(SignUpRequestDto body) {
        UserEntity user = this.registerUserService.createUser(body);
        TokenPairDto tokenPairDto = this.tokenGenerationService.generateTokenPair(user);
        this.sessionService.createSession(tokenPairDto, user);

        return SignUpServiceResponseDto.builder()
                .refreshToken(tokenPairDto.getRefreshToken())
                .accessToken(tokenPairDto.getAccessToken())
                .refreshTokenExpiration(tokenPairDto.getRefreshTokenExpiration())
                .user(EntityMapper.toUserDto(user))
                .build();
    }

}
