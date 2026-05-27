package dequelite.financemirror_backend.app.common.session;

import dequelite.financemirror_backend.app.common.token_pair.dto.TokenPairDto;
import dequelite.financemirror_backend.domain.session.SessionEntity;
import dequelite.financemirror_backend.domain.session.SessionRepository;
import dequelite.financemirror_backend.domain.user.UserEntity;
import dequelite.financemirror_backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    public void createSession(TokenPairDto tokenPairDto, UserEntity user){
        SessionEntity session = SessionEntity.builder()
                .user(user)
                .refreshToken(tokenPairDto.getHashedRefreshToken())
                .refreshTokenExpiresAt(tokenPairDto.getRefreshTokenExpiration())
                .accessTokenId(tokenPairDto.getAccessTokenId())
                .latestLogin(LocalDateTime.now())
                .build();

        SessionEntity savedSession = this.sessionRepository.save(session);

        user.setSession(savedSession);
        userRepository.save(user);
    }
}
