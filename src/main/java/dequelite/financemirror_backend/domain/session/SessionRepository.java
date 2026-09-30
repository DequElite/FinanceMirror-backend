package dequelite.financemirror_backend.domain.session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<SessionEntity, UUID> {
    @Modifying
    @Query("""
        DELETE FROM SessionEntity s
            WHERE s.user.id = :userId
                AND s.expiresAt < :now
    """)
    void deleteExpiredSessions(
            @Param("userId") UUID userId,
            @Param("now") LocalDateTime now
    );

    List<SessionEntity> findByUser_IdAndExpiresAtAfterOrderByExpiresAtAsc(
            UUID userId,
            LocalDateTime now
    );
}
