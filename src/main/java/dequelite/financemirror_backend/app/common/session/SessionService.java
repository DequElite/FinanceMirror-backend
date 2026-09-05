package dequelite.financemirror_backend.app.common.session;

import dequelite.financemirror_backend.app.common.token_pair.TokenGenerationService;
import dequelite.financemirror_backend.app.common.token_pair.dto.TokenPairDto;
import dequelite.financemirror_backend.domain.session.SessionEntity;
import dequelite.financemirror_backend.domain.session.SessionRepository;
import dequelite.financemirror_backend.domain.user.UserEntity;
import dequelite.financemirror_backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.antlr.runtime.Token;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private final TokenGenerationService tokenGenerationService;

    @Transactional
    public TokenPairDto createSession(UserEntity user, String ipAddress, String userAgent){
        SessionEntity session = SessionEntity.builder()
                .user(user)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        this.sessionRepository.saveAndFlush(session);

        UUID sessionId = session.getId();

        TokenPairDto tokenPairDto = this.tokenGenerationService.generateTokenPair(user, sessionId.toString());

        session.setRefreshToken(tokenPairDto.getRefreshToken());
        session.setAccessTokenId(tokenPairDto.getAccessTokenId());
        session.setExpiresAt(tokenPairDto.getExpiresAt());

        this.sessionRepository.save(session);

        return tokenPairDto;
    }

    @Transactional
    public void deleteExpiredSessions(UserEntity user) {
        this.sessionRepository.deleteExpiredSessions(user.getId(), LocalDateTime.now());
    }
}
