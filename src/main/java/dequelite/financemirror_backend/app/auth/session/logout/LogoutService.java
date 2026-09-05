package dequelite.financemirror_backend.app.auth.session.logout;

import dequelite.financemirror_backend.app.auth.session.CheckRefreshTokenService.CheckRefreshTokenService;
import dequelite.financemirror_backend.domain.session.SessionEntity;
import dequelite.financemirror_backend.domain.session.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogoutService {
    private final CheckRefreshTokenService checkRefreshTokenService;
    private final SessionRepository sessionRepository;

    @Transactional
    public void logout(String token) {
        SessionEntity session = this.checkRefreshTokenService.checkAndReturn(token);

        sessionRepository.delete(session);
    }
}
