package dequelite.financemirror_backend.app.auth.session.all_sessions;

import dequelite.financemirror_backend.app.common.mapper.EntityMapper;
import dequelite.financemirror_backend.domain.dto.SessionDto;
import dequelite.financemirror_backend.domain.session.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AllSessionsService {
    private final SessionRepository sessionRepository;

    public List<SessionDto> activeSessions(UUID userId) {
        return EntityMapper.mapSessionDto(
          sessionRepository.findByUser_IdAndExpiresAtAfterOrderByExpiresAtAsc(userId, LocalDateTime.now())
        );
    }

}
