package dequelite.financemirror_backend.app.auth.log_in;

import dequelite.financemirror_backend.app.auth.log_in.dto.LogInRequestDto;
import dequelite.financemirror_backend.app.auth.log_in.dto.LogInServiceResponseDto;
import dequelite.financemirror_backend.app.common.mapper.EntityMapper;
import dequelite.financemirror_backend.app.common.session.SessionService;
import dequelite.financemirror_backend.app.common.token_pair.TokenGenerationService;
import dequelite.financemirror_backend.app.common.token_pair.dto.TokenPairDto;
import dequelite.financemirror_backend.domain.user.UserEntity;
import dequelite.financemirror_backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LogInService {
    private final AuthUserService authUserService;
    private final TokenGenerationService tokenGenerationService;
    private final SessionService sessionService;

    public LogInServiceResponseDto logIn(LogInRequestDto body){
        UserEntity user = this.authUserService.auth(body.getEmail(), body.getPassword());
        TokenPairDto tokenPairDto = this.tokenGenerationService.generateTokenPair(user);
        this.sessionService.createSession(tokenPairDto, user);

        return LogInServiceResponseDto.builder()
                .accessToken(tokenPairDto.getAccessToken())
                .refreshToken(tokenPairDto.getRefreshToken())
                .refreshTokenExpiration(tokenPairDto.getRefreshTokenExpiration())
                .user(EntityMapper.toUserDto(user))
                .build();
    }
}
