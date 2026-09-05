package dequelite.financemirror_backend.app.auth.session.revoke_session;

import dequelite.financemirror_backend.app.common.response.ApiException;
import dequelite.financemirror_backend.app.common.response.ErrorCode;
import dequelite.financemirror_backend.domain.session.SessionEntity;
import dequelite.financemirror_backend.domain.session.SessionRepository;
import dequelite.financemirror_backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RevokeSessionService {
    private final SessionRepository sessionRepository;

    public void revokeSession(UUID sessionId, UUID userId) {
        SessionEntity session = this.sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ApiException(ErrorCode.SESSION_NOT_FOUNT));

        if (!session.getUser().getId().equals(userId)) {
            throw new ApiException(ErrorCode.SESSION_FORBIDDEN);
        }

        sessionRepository.delete(session);
    }
}
